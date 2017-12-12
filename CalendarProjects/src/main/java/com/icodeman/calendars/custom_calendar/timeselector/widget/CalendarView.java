package com.icodeman.calendars.custom_calendar.timeselector.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
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
 * @github https://github.com/LMW-ICodeMan
 * @date 2017/12/8
 */
public class CalendarView extends FrameLayout {

    @IntDef({STYLE_NO_SELECTOR,STYLE_WEEK_SELECTOR, STYLE_DAY_SELECTOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectorStyle {
    }
    public static final int STYLE_NO_SELECTOR = 0;
    public static final int STYLE_DAY_SELECTOR = 1;
    public static final int STYLE_WEEK_SELECTOR = 2;

    // ------------------------      用于记录 Item 的信息

    private SparseArray<List<View>> views = new SparseArray<>();
    private List<LineInfo> lineInfos = new ArrayList<>();
    private int lastSelectRow = -1;
    private int lastSelectColumn = -1;
    private CalendarAdapter adapter;

    // ------------------------      用于绘制 Item

    private static final int DRAW_STATE_NEED = 0;//需要刷新
    private static final int DRAW_STATE_DOING = 1;//正在刷新
    private static final int DRAW_STATE_FINISH = 2;//刷新结束

    private int drawState = DRAW_STATE_FINISH;
    private int lastViewLeft;
    private int lastViewTop;


    // ------------------------      用于绘制CalendarView的具体宽高

    private int itemWidth = 0;
    private int itemHeight = 0;
    private int titleHeight = 0;
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
    private float[] downPosition;
    /**
     * 绘制单日点击效果的Paint
     */
    private Paint circlePaint;
    private int circleWidth = -1;
    private int circleColor = Color.parseColor("#888888");
    /**
     * 绘制单周点击效果的Paint和绘制虚线的Effect
     */
    private Paint linePaint;
    private PathEffect effects;
    /**
     * 虚线宽度
     */
    private int lineWidth = 1;
    /**
     * 虚线的偏移量（与每行边界的距离）
     */
    private int lineOffset = -1;
    /**
     * 虚线的圆角
     */
    private int lineCircleWidth = 10;
    /**
     * 三种状态的颜色（不可点击，未选择状态，选择状态）
     */
    private int lineColorNormal = Color.parseColor("#888888");
    private int lineColorSelect = Color.parseColor("#ff0000");

    /**
     * 用来记录是否有默认值
     */
    private boolean hasDefault = false;
    private int defaultYear;
    private int defaultMonth;
    private int defaultDay;
    private int defaultWeek;

    public CalendarView(@NonNull Context context) {
        super(context);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    @SuppressLint("WrongConstant")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY));
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
        if (adapter == null || drawState != DRAW_STATE_NEED) {
            return;
        }
        resetSelects();
        drawState = DRAW_STATE_DOING;
        if (selectorStyle == STYLE_DAY_SELECTOR) {
            drawCircle(canvas);
        } else if (selectorStyle == STYLE_WEEK_SELECTOR) {
            drawLines(canvas);
        }
        super.onDraw(canvas);
        drawState = DRAW_STATE_FINISH;
    }

    private void resetCirclePaint() {
        if (circlePaint == null) {
            circlePaint = new Paint();
        }
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
    }

    private void resetLinePaint() {
        if (linePaint == null) {
            linePaint = new Paint();
        }
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        //设置虚线的间隔和点的长度
        effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        linePaint.setPathEffect(effects);
        linePaint.setColor(lineColorNormal);
    }

    private void drawCircle(Canvas canvas) {
        if (circlePaint == null) {
            resetCirclePaint();
        }
        float[] center = null;
        int lastHeight = 0;
        for (int row = 0; row < lineInfos.size(); row++) {
            int height = lastHeight + lineInfos.get(row).height;
            if (circleWidth == -1) {
                circleWidth = (int) Math.min(0.3f * lineInfos.get(row).width, 0.3f * lineInfos.get(row).height);
            }
            if (row > 0) {
                if (downPosition != null && isDownPositionIn(downPosition[1], lastHeight, height)) {
                    int column = (int) (downPosition[0] / lineInfos.get(row).width);
                    center = new float[2];
                    //在点击区域
                    buildSelectorPoint(row, column, height, center);
                    break;
                } else if (downPosition == null) {
                    for (int column = 0; column < adapter.getColumnCount(); column++) {
                        if (isDefaultPoint(row, column)) {
                            center = new float[2];
                            //是默认选中区域
                            buildSelectorPoint(row, column, height, center);
                            break;
                        }
                    }
                }
            }
            lastHeight = height;
        }
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
            if (lineOffset == -1) {
                lineOffset = (int) Math.min(0.125f * lineInfos.get(row).width, 0.125f * lineInfos.get(row).height) - lineWidth;
            }
            if (row > 0) {
                List<PointInfo> infos = adapter.getPointInfos(row);
                if(infos == null || infos.size()<0){
                    resetLinePaint();
                }
                if (downPosition == null && isDefaultLine(row)) {
                    //没有点击过，并且是默认点击区域
                    buildSelectorLine(row, infos);
                }else if (downPosition != null
                        && isDownPositionIn(downPosition[1], top, bottom)) {
                    //在点击区域
                    buildSelectorLine(row, infos);
                } else if (downPosition != null
                        && lastSelectRow == row
                        && !isInClickableSpace(downPosition[0], downPosition[1])) {
                    //不在点击区域，上次的点击在点击区域
                    buildSelectorLine(row, infos);
                }else if(downPosition == null && adapter.getSelectedTimes() != null && adapter.getSelectedTimes().length > 0) {
                    //切换月份之前有点击过
                    long time = adapter.getSelectedTimes()[0];
                    if(infos != null && infos.size() > 0 && time == infos.get(0).time) {
                        buildSelectorLine(row, infos);
                    }else {
                        resetLinePaint();
                    }
                } else {
                    resetLinePaint();
                }
                if (selectorStyle == STYLE_WEEK_SELECTOR) {
                    canvas.drawPath(getLinePath(left, top, right, bottom, lineOffset), linePaint);
                }
            }
            top = bottom;
        }
    }

    private void buildSelectorLine(int row, List<PointInfo> infos) {
        hasDefault = false;//绘制了一次之后记得重置默认状态，否则会出现可以选择两行的情况
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(lineColorSelect);
        lastSelectRow = row;
        adapter.onLineSelect(infos);
    }

    private void buildSelectorPoint(int row, int column, int height, float[] center) {
        hasDefault = false;
        center[0] = (column + 0.5f) * lineInfos.get(row).width;
        center[1] = height - 0.5f * lineInfos.get(row).height;
        lastSelectRow = row;
        lastSelectColumn = column;
        PointInfo info = adapter.getPointInfo(row, column);
        adapter.onItemSelect(info);
    }

    private boolean isDefaultPoint(int row, int column) {
        PointInfo info = adapter.getPointInfo(row, column);
        if (info != null) {
            return hasDefault
                    && info.year == defaultYear
                    && info.month == defaultMonth
                    && info.day == defaultDay;
        }
        return false;
    }

    private boolean isDefaultLine(int row) {
        List<PointInfo> infos = adapter.getPointInfos(row);
        if (infos != null && infos.size() > 0) {
            PointInfo info = infos.get(0);
            if (info != null) {
                return hasDefault
                        && info.year == defaultYear
                        && info.month == defaultMonth
                        && info.week == defaultWeek;
            }
        }
        return false;
    }

    private Path getLinePath(int left, int top, int right, int bottom, int lineOffset) {
        Path path = new Path();
        top = top + lineOffset;
        bottom = bottom - lineOffset;
        int lineCircleWidth = (bottom - top) / 2;
        path.moveTo(left + lineCircleWidth, top);

        path.lineTo(right - lineCircleWidth, top);
        RectF rectF;
        rectF = new RectF(right - 2 * lineCircleWidth, top, right, bottom);
        path.arcTo(rectF, -90, 180, false);

        path.lineTo(left + lineCircleWidth, bottom);
        rectF = new RectF(left, top, left + 2 * lineCircleWidth, bottom);
        path.arcTo(rectF, 90, 180, false);
        return path;
    }

    private boolean isDownPositionIn(float num, int min, int max) {
        return num >= min && num < max;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downPosition = new float[2];
            downPosition[0] = event.getX();
            downPosition[1] = event.getY();
            refreshViews();
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
            drawState = DRAW_STATE_NEED;
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
        if (adapter == null) {
            return;
        }
        measure(0, 0);
        resetDatas();
        for (int row = 0; row < adapter.getRowCount(); row++) {
            List<View> line = new ArrayList<>();
            int itemWidth = 0;
            int itemHeight = 0;
            for (int column = 0; column < adapter.getColumnCount(); column++) {
                View view;
                if (row == 0) {
                    view = adapter.getWeeksView(column);
                    view.measure(0, 0);
                    itemWidth = this.itemWidth <= 0 ? view.getMeasuredWidth() : this.itemWidth;
                    itemHeight = view.getMeasuredHeight();
                } else {
                    PointInfo info = adapter.getPointInfo(row, column);
                    view = adapter.getDaysView(info);
                    info.view = view;
                    view.measure(0, 0);
                    itemWidth = this.itemWidth <= 0 ? view.getMeasuredWidth() : this.itemWidth;
                    itemHeight = this.itemHeight <= 0 ? view.getMeasuredHeight() : this.itemHeight;
                }
                addView(view);
                line.add(view);
            }
            measureWidth = itemWidth * adapter.getColumnCount();
            measureHeight += itemHeight;
            spaceRight = measureWidth;
            if (row == 0) {
                spaceTop = measureHeight;
            } else {
                spaceBottom = measureHeight;
            }
            lineInfos.add(new LineInfo(itemWidth, itemHeight));
            views.put(row, line);
        }
        requestLayout();
        refreshViews();
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

    /**
     * 设置默认选择的周
     */
    public void setDefaultWeek(int year, int month, int week) {
        defaultYear = year;
        defaultMonth = month;
        defaultWeek = week;
        hasDefault = true;
        refreshViews();
    }

    /**
     * 设置默认选择的日
     */
    public void setDefaultDay(int year, int month, int day) {
        defaultYear = year;
        defaultMonth = month;
        defaultDay = day;
        hasDefault = true;
        refreshViews();
    }

    public void setAdapter(CalendarAdapter adapter) {
        this.adapter = adapter;
        this.adapter.bindView(this);
        resetViews();
    }

    public ICalendarAdapter getAdapter() {
        return adapter;
    }

    public void setSelectorStyle(@SelectorStyle int style) {
        this.selectorStyle = style;
        refreshViews();
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        refreshViews();
    }

    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
        refreshViews();
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        refreshViews();
    }

    public void setLineOffset(int lineOffset) {
        this.lineOffset = lineOffset;
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

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
        resetViews();
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        resetViews();
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
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
        public View view;

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
        public long time;

        public PointInfo(long time, int week) {
            this.time = time;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            this.week = week;
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

        private List<Long> selectedTimes = new ArrayList<>();

        public CalendarAdapter(Context context) {
            this.context = context;
        }

        public void setTime(int year, int month, int day) {
            lineInfos = getNumListByTime(getZeroTime(year, month, day));
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
                    PointInfo pointInfo = new PointInfo(temp.getTimeInMillis(), week);
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
                PointInfo pointInfo = new PointInfo(calendar.getTimeInMillis(), week);
                pointInfos.get(rowCount).add(pointInfo);
                calendar.setTimeInMillis(calendar.getTimeInMillis() + ONE_DAY);
                if (calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                    rowCount++;
                }
            } while (calendar.get(Calendar.MONTH) == month);
            //计算下个月需要显示的天数
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                PointInfo pointInfo = new PointInfo(calendar.getTimeInMillis(), week + 1);
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
            if (info == null) {
                return;
            }
            selectedTimes.clear();
            selectedTimes.add(info.time);
        }

        @Override
        public void onLineSelect(List<PointInfo> infos) {
            if (infos == null || infos.size() == 0) {
                return;
            }
            selectedTimes.clear();
            for (int i = 0; i < infos.size(); i++) {
                PointInfo info = infos.get(i);
                if (info != null) {
                    selectedTimes.add(info.time);
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

        private long getZeroTime(int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        }

        public void notifyDataSetChange() {
            if (view != null) {
                view.resetViews();
            }
        }

        public long[] getSelectedTimes() {
            if (selectedTimes.size() != 0) {
                long[] times = new long[selectedTimes.size()];
                for (int i = 0; i < times.length; i++) {
                    times[i] = selectedTimes.get(i);
                }
                return times;
            }
            return null;
        }
    }
}
