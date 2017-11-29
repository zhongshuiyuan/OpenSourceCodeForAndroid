package com.icodeman.calendars.custom_calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author ICodeMan
 * @date 2017/11/24
 */

public class CalendarView extends FrameLayout {

    @IntDef({STYLE_NO_SELECTOR, STYLE_WEEK_SELECTOR, STYLE_DAY_SELECTOR, STYLE_DAY_AND_WEEK_SELECTOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectorStyle {
    }

    public static final int STYLE_NO_SELECTOR = 0;
    public static final int STYLE_DAY_SELECTOR = 1;
    public static final int STYLE_WEEK_SELECTOR = 2;
    public static final int STYLE_DAY_AND_WEEK_SELECTOR = 3;

    // ------------------------      用于记录 Item 的信息

    private SparseArray<List<View>> views = new SparseArray<>();
    private List<LineInfo> lineInfos = new ArrayList<>();
    private int lastSelectRow = -1;
    private int lastSelectColumn = -1;
    private CalendarAdapter adapter;

    // ------------------------      用于绘制 Item

    private int lastViewLeft;
    private int lastViewTop;


    // ------------------------      用于绘制CalendarView的具体宽高

    private int origWidthSpec = 0;
    private int origHeightSpec = 0;
    private int measureWidth = 0;
    private int measureHeight = 0;
    /**
     * 可点击的有效区域(left,top,right,bottom)
     */
    private int spaceLeft = 0;
    private int spaceTop = 0;
    private int spaceBottom = 0;
    private int spaceRight = 0;


    // ------------------------      用于绘制选择效果

    /**
     * 选择日期的方式
     */
    private @SelectorStyle
    int selectorStyle;
    /**
     * down动作的坐标
     */
    private float[] downPosition = new float[]{-1, -1};
    /**
     * 绘制单日点击效果的Paint
     */
    private Paint circlePaint;
    private int circleWidth;
    private int circleColor = Color.parseColor("#888888");
    /**
     * 绘制单周点击效果的Paint和绘制虚线的Effect
     */
    private Paint linePaint;
    private PathEffect effects;
    /**
     * 虚线宽度
     */
    private int lineWidth = 4;
    /**
     * 虚线的偏移量（与每行边界的距离）
     */
    private int lineOffset;
    /**
     * 三种状态的颜色（不可点击，未选择状态，选择状态）
     */
    private int lineColorUnable = Color.parseColor("#800000");
    private int lineColorNormal = Color.parseColor("#888888");
    private int lineColorSelect = Color.parseColor("#ff0000");


    // --------------    用于特殊值记录

    /**
     * 用来记录是否有默认值
     */
    boolean hasDefault = false;
    PointInfo defaultPoint;

    /**
     * 特殊点
     * PointInfo中只有时间相关参数
     */
    List<PointInfo> specialPoint;

    public CalendarView(@NonNull Context context) {
        super(context);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    @SuppressLint("WrongConstant")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adapter == null) {
            origWidthSpec = MeasureSpec.getMode(widthMeasureSpec);
            origHeightSpec = MeasureSpec.getMode(heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (adapter == null) {
            return;
        }
        lastViewTop = 0;
        for (int row = 0; row < adapter.getRowCount(); row++) {
            layoutChild(row);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("LMW", "onDraw()");
        if (adapter == null) {
            return;
        }
        resetSelects();
        if (selectorStyle == STYLE_DAY_SELECTOR) {
            drawCircle(canvas);
        } else if (selectorStyle == STYLE_WEEK_SELECTOR) {
            drawLines(canvas);
        } else if (selectorStyle == STYLE_DAY_AND_WEEK_SELECTOR) {
            drawCircle(canvas);
            drawLines(canvas);
        }
        super.onDraw(canvas);
    }

    private void resetCirclePaint() {
        if (circlePaint == null) {
            circlePaint = new Paint();
        }
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
    }

    private void resetLinePaint() {
        if (linePaint == null) {
            linePaint = new Paint();
        }
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        //设置虚线的间隔和点的长度
        effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        linePaint.setPathEffect(effects);
    }

    private void drawLines(Canvas canvas) {
        if (linePaint == null) {
            resetLinePaint();
        }
        int left, top, right, bottom;
        left = 0;
        top = 0;
        for (int row = 0; row < lineInfos.size(); row++) {
            right = lineInfos.get(row).width * adapter.getColumnCount();
            bottom = top + lineInfos.get(row).height;
            lineOffset = (int) Math.min(0.125f * lineInfos.get(row).width, 0.125f * lineInfos.get(row).height);
            if (row > 0) {
                List<PointInfo> infos = adapter.getPointInfos(row);
                if (infos != null && infos.size() > 0 && infos.get(0).week == -1) {
                    //不是本月的周，不可选中
                    linePaint.setStyle(Paint.Style.STROKE);
                    linePaint.setColor(lineColorUnable);
                } else if (isDownPositionIn(downPosition[1], top, bottom) || isDefaultLine(row)) {
                    //选中
                    lineOffset -= lineWidth;
                    linePaint.setStyle(Paint.Style.FILL);
                    linePaint.setColor(lineColorSelect);
                    lastSelectRow = row;
                    adapter.onLineSelect(infos);
                } else {
                    linePaint.setStyle(Paint.Style.STROKE);
                    linePaint.setColor(lineColorNormal);
                }
                if (selectorStyle == STYLE_WEEK_SELECTOR || selectorStyle == STYLE_DAY_AND_WEEK_SELECTOR) {
                    canvas.drawPath(getLinePath(left, top, right, bottom, lineOffset), linePaint);
                }
            }
            top = bottom;
        }
    }

    private boolean isDefaultPoint(int row, int column) {
        PointInfo info = adapter.getPointInfo(row, column);
        if (defaultPoint != null && info != null) {
            return hasDefault && info.year == defaultPoint.year && info.month == defaultPoint.month && info.day == defaultPoint.month;
        }
        return false;
    }

    private boolean isDefaultLine(int row) {
        List<PointInfo> infos = adapter.getPointInfos(row);
        if (defaultPoint != null && infos != null && infos.size() > 0) {
            PointInfo info = infos.get(0);
            if (info != null) {
                return hasDefault && info.year == defaultPoint.year && info.month == defaultPoint.month && info.week == defaultPoint.week;
            }
        }
        return false;
    }

    private Path getLinePath(int left, int top, int right, int bottom, int lineOffset) {
        Path path = new Path();
        path.moveTo(left + lineOffset, top + lineOffset);
        path.lineTo(right - lineOffset, top + lineOffset);
        path.lineTo(right - lineOffset, bottom - lineOffset);
        path.lineTo(left + lineOffset, bottom - lineOffset);
        path.lineTo(left + lineOffset, top + lineOffset);
        return path;
    }

    private void drawCircle(Canvas canvas) {
        if (circlePaint == null) {
            resetCirclePaint();
        }
        float[] center = new float[2];
        int lastHeight = 0;
        for (int row = 0; row < lineInfos.size(); row++) {
            int height = lastHeight + lineInfos.get(row).height;
            circleWidth = (int) Math.min(0.3f * lineInfos.get(row).width, 0.3f * lineInfos.get(row).height);
            if (row > 0) {
                int column = (int) (downPosition[0] / lineInfos.get(row).width);
                PointInfo info = adapter.getPointInfo(row, column);
                if (isDownPositionIn(downPosition[1], lastHeight, height) || isDefaultPoint(row, column)) {
                    //在点击区域
                    center[0] = (column + 0.5f) * lineInfos.get(row).width;
                    center[1] = height - 0.5f * lineInfos.get(row).height;
                    lastSelectRow = row;
                    lastSelectColumn = column;
                    adapter.onItemSelect(info);
                    checkBackSpecial(info,true);
                    break;
                }
                checkBackSpecial(info,false);
            }
            lastHeight = height;
        }
        if (selectorStyle == STYLE_DAY_SELECTOR || selectorStyle == STYLE_DAY_AND_WEEK_SELECTOR) {
            canvas.drawCircle(center[0], center[1], circleWidth, circlePaint);
        }
    }

    private void checkBackSpecial(PointInfo info,boolean inSelect){
        if(specialPoint == null || specialPoint.size() == 0){
            return;
        }else {
            for (PointInfo sp :specialPoint){
                if(sp.isSameDay(info)){
                    adapter.oItemSpecial(info,inSelect);
                }
            }
        }
    }

    private boolean isDownPositionIn(float num, int min, int max) {
        return num >= min && num < max;
    }

    private boolean hasCircleLast() {
        return (selectorStyle == STYLE_DAY_AND_WEEK_SELECTOR || selectorStyle == STYLE_DAY_SELECTOR)
                && lastSelectRow != -1 && lastSelectColumn != -1;
    }

    private boolean hasLineLast() {
        return (selectorStyle == STYLE_DAY_AND_WEEK_SELECTOR || selectorStyle == STYLE_WEEK_SELECTOR)
                && lastSelectRow != -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInClickableSpace(event.getX(), event.getY())) {
                downPosition = new float[2];
                downPosition[0] = event.getX();
                downPosition[1] = event.getY();
                invalidate();
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isInClickableSpace(float x, float y) {
        if (x < spaceLeft || x > spaceRight) {
            return false;
        } else if (y < spaceTop || y > spaceBottom) {
            return false;
        }
        return true;
    }

    /**
     * 需要调用这个方法的情况：
     * 1. 改变了画笔属性
     */
    public void refreshViews() {
        resetCirclePaint();
        resetLinePaint();
        try {
            invalidate();
        } catch (Exception e) {
            postInvalidate();
        }
    }

    /**
     * 需要调用这个方法的情况：
     * 1. Item的宽度和高度发生变化
     * 2. Adapter的数据集发生变化
     */
    public void resetViews() {
        resetDatas();
        for (int row = 0; row < adapter.getRowCount(); row++) {
            List<View> line = new ArrayList<>();
            int itemWidth = -1;
            int itemHeight = -1;
            int weekFlag = -1;
            for (int column = 0; column < adapter.getColumnCount(); column++) {
                View view;
                if (row == 0) {
                    view = adapter.getWeeksView(column);
                } else {
                    PointInfo info = adapter.getPointInfo(row, column);
                    view = adapter.getDaysView(info);
                    info.view = view;
                    weekFlag = info.week;
                }
                addView(view);
                view.measure(0, 0);

                itemWidth = Math.max(itemWidth, view.getMeasuredWidth());
                if (origWidthSpec == MeasureSpec.EXACTLY) {
                    itemWidth = Math.max(itemWidth, (int) (1f * getMeasuredWidth() / adapter.getColumnCount()));
                }
                itemHeight = Math.max(itemHeight, view.getMeasuredHeight());
                line.add(view);
            }
            measureWidth = itemWidth * adapter.getColumnCount();
            measureHeight += itemHeight;
            spaceRight = measureWidth;
            if (weekFlag == -1) {
                spaceTop = measureHeight;
            } else {
                spaceBottom = measureHeight;
            }
            lineInfos.add(new LineInfo(itemWidth, itemHeight));
            views.put(row, line);
        }
        requestLayout();
    }

    /**
     * 重置所有界面
     */
    private void resetSelects() {
        if (lastSelectRow != -1) {
            List<PointInfo> infoList = adapter.getPointInfos(lastSelectRow);
            if (infoList != null) {
                for (PointInfo info : infoList) {
                    if (info != null) {
                        adapter.onItemReset(info);
                    }
                }
            }
        }
        lastSelectRow = -1;
        lastSelectColumn = -1;
    }

    /**
     * 重置所有数据
     */
    private void resetDatas() {
        measureWidth = 0;
        measureHeight = 0;
        lastSelectRow = -1;
        lastSelectColumn = -1;
        downPosition = null;
        if (lineInfos != null) {
            lineInfos.clear();
        }
        if (views != null) {
            views.clear();
        }
        removeAllViews();
    }

    private void setClickableSpace(int left, int top, int bottom, int right) {
        spaceLeft = left;
        spaceTop = top;
        spaceRight = right;
        spaceBottom = bottom;
    }

    private void layoutChild(int row) {
        lastViewLeft = 0;
        LineInfo info = lineInfos.get(row);
        List<View> line = views.get(row);
        for (View view : line) {
            view.layout(lastViewLeft, lastViewTop, lastViewLeft + info.width, lastViewTop + info.height);
            lastViewLeft += info.width;
        }
        lastViewTop = lastViewTop + info.height;
    }

    public void setAdapter(CalendarAdapter adapter) {
        this.adapter = adapter;
        this.adapter.bindView(this);
        resetViews();
    }

    public void setSelectorStyle(@SelectorStyle int style) {
        this.selectorStyle = style;
        refreshViews();
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        resetViews();
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        resetViews();
    }

    public void setLineColorUnable(int lineColorUnable) {
        this.lineColorUnable = lineColorUnable;
        refreshViews();
    }

    public void setLineColorNormal(int lineColorNormal) {
        this.lineColorNormal = lineColorNormal;
        refreshViews();
    }

    public void setLineColorSelect(int lineColorSelect) {
        this.lineColorSelect = lineColorSelect;
        refreshViews();
    }

    private class LineInfo {
        int width;
        int height;

        public LineInfo(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static class PointInfo {
        //布局相关参数

        public View view;

        //时间相关参数

        public int year;
        public int month;
        /**
         * 范围为 1 - 31
         */
        public int day;
        /**
         * week = -1 指的是当前日期属于前一个月的周
         */
        public int week;

        public PointInfo(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public PointInfo(int year, int month, int day, int week) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.week = week;
        }

        public boolean isSameDay(PointInfo info) {
            return info.year == year && info.month == month && info.day == day;
        }

        public boolean isSameWeek(PointInfo info) {
            return info.year == year && info.month == month && info.week == week;
        }
    }

    public interface ICalendarAdapter {
        /**
         * 获得总行数
         *
         * @return
         */
        int getRowCount();

        /**
         * 获得列数
         *
         * @return
         */
        int getColumnCount();

        /**
         * 获得Item
         *
         * @return
         */
        View getWeeksView(int column);

        View getDaysView(PointInfo pointInfo);

        /**
         * 特殊点处理
         *
         * @param pointInfo  特殊点信息
         * @param isSelected 特殊点是否在选择范围内
         */
        void oItemSpecial(PointInfo pointInfo, boolean isSelected);

        /**
         * 当某个Item被选择时
         */
        void onItemSelect(PointInfo pointInfo);

        /**
         * 当某一行被选择时
         */
        void onLineSelect(List<PointInfo> infos);

        /**
         * 当切换选择时恢复之前的Item
         */
        void onItemReset(PointInfo info);

        PointInfo getPointInfo(int row, int column);

        List<PointInfo> getPointInfos(int row);
    }

    public abstract static class CalendarAdapter implements ICalendarAdapter {
        private static final long ONE_HOURS = 1000 * 60 * 60 * 1;
        private static final long ONE_DAY = 24 * ONE_HOURS;

        protected Context context;

        private CalendarView view;
        private SparseArray<List<PointInfo>> lineInfos;
        private int rowCount;

        private int selectYear;
        private int selectMonth;
        private int selectDay;
        private int selectWeek;

        public CalendarAdapter(Context context) {
            this.context = context;
        }

        public void setTime(int year, int month) {
            setTime(year, month, 1);
        }

        public void setTime(int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            lineInfos = getNumListByTime(calendar.getTimeInMillis());
        }

        private SparseArray<List<PointInfo>> getNumListByTime(long time) {
            SparseArray<List<PointInfo>> pointInfos = new SparseArray<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.DATE, 1);
            int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            List<PointInfo> infos;
            int week = 0;
            rowCount = 0;
            //计算上月需要显示的天数
            if (firstDayOfWeek != Calendar.MONDAY) {
                week = -1;
                infos = new ArrayList<>();
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(calendar.getTimeInMillis());
                int tempDayOfWeek = firstDayOfWeek == Calendar.SUNDAY ? Calendar.SATURDAY - 1 : firstDayOfWeek - Calendar.MONDAY;
                for (int i = tempDayOfWeek; i > 0; i--) {
                    temp.setTimeInMillis(calendar.getTimeInMillis() - i * ONE_DAY);
                    PointInfo pointInfo = new PointInfo(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH), week);
                    infos.add(pointInfo);
                }
                pointInfos.put(rowCount, infos);
            }

            //计算当月需要显示的天数
            int month = calendar.get(Calendar.MONTH);
            boolean hasLast = (week == -1);
            do {
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                week = calendar.get(Calendar.WEEK_OF_MONTH) - 1;
                if (hasLast && firstDayOfWeek != Calendar.SUNDAY) {
                    --week;
                }
                if (week == 0 && dayOfWeek != Calendar.MONDAY) {
                    --week;
                }
                if (pointInfos.get(rowCount) == null) {
                    pointInfos.put(rowCount, new ArrayList<PointInfo>());
                }
                PointInfo pointInfo = new PointInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), week);
                pointInfos.get(rowCount).add(pointInfo);
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ONE_DAY);
                if (calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    rowCount++;
                }
            } while (calendar.get(Calendar.MONTH) == month);
            //计算下个月需要显示的天数
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                PointInfo pointInfo = new PointInfo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), week + 1);
                if (pointInfos.get(rowCount) == null) {
                    pointInfos.put(rowCount, new ArrayList<PointInfo>());
                }
                pointInfos.get(rowCount).add(pointInfo);
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ONE_DAY);
            }
            rowCount++;
            return pointInfos;
        }

        @Override
        public int getRowCount() {
            return rowCount + 1;
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public PointInfo getPointInfo(int row, int column) {
            return lineInfos.get(row - 1).get(column);
        }

        @Override
        public List<PointInfo> getPointInfos(int row) {
            return lineInfos.get(row - 1);
        }

        @Override
        public void onItemSelect(PointInfo info) {
            selectYear = info.year;
            selectMonth = info.month;
            selectDay = info.day;
            selectWeek = info.week;
            if (info == null) {
                return;
            }
        }

        @Override
        public void onLineSelect(List<PointInfo> infos) {
            if (infos == null || infos.size() == 0) {
                return;
            }
            for (PointInfo info : infos) {
                if (info != null) {
                    onItemSelect(info);
                }
            }
        }

        @Override
        public void onItemReset(PointInfo info) {
            if (info == null) {
                return;
            }
        }

        private void bindView(CalendarView view) {
            this.view = view;
        }

        public void notifyDataSetChange() {
            if (view != null) {
                view.resetViews();
            }
        }

        public void setDefatltDay(int year, int month, int day) {
            view.defaultPoint = new PointInfo(year, month, day);
            view.refreshViews();
        }

        public void setDefaltWeek(int year, int month, int week) {
            view.defaultPoint = new PointInfo(year, month, 0, week);
            view.refreshViews();
        }

        public void addSpecial(int year, int month, int day) {
            if (view.specialPoint == null) {
                view.specialPoint = new ArrayList<>();
            }
            view.specialPoint.add(new PointInfo(year, month, day));
        }

        /**
         * @return int[]{year,month,day,week} week = -1 时表示是上月的最后一周
         */
        public int[] getSelected() {
            int[] selected = new int[4];
            selected[0] = selectYear;
            selected[1] = selectMonth;
            selected[2] = selectDay;
            selected[3] = selectWeek;
            return selected;
        }
    }
}
