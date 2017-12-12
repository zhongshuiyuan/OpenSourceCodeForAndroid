package com.icodeman.calendars.custom_calendar.timeselector.manager;


import android.graphics.Canvas;
import android.graphics.Paint;

import com.icodeman.calendars.custom_calendar.timeselector.interfaces.ICalendarView;
import com.icodeman.calendars.custom_calendar.timeselector.widget.CalendarView;

import java.util.List;


/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2017/12/8
 */
public class CalendarViewDayManager {
    private static final int COLUMN_COUNT = 7;

    private Paint paint;
    private int circleWidth;
    private int circleColor;

    private ICalendarView iView;

    private List<CalendarView.PointInfo> pointInfos;
    private int column;

    private int dYear, dMonth, dDay;

    public CalendarViewDayManager(ICalendarView calendar) {
        this.iView = calendar;
        pointInfos = calendar.getData();
        initPaint();
    }

    public void setDefaultDay(int year, int month, int day) {
        dYear = year;
        dMonth = month;
        dDay = day;
    }

    private void initPaint(){
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circleColor);
    }

    private void refreshPaint() {
        paint.setColor(circleColor);
        paint.setStrokeWidth(circleWidth);
    }

    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
        refreshPaint();
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        refreshPaint();
    }

    public void drawDayInfo(Canvas canvas) {
        if(iView.getAdapter() == null){
            return;
        }
        int row = 0;
        int column = 0;

        CalendarView.PointInfo info = null;

        float[] downPosition = iView.getDownPosition();
        if (checkPosition(downPosition)) {
            column = (int) downPosition[0] % iView.getItemWidth();
            row = (int) downPosition[1] / iView.getItemHeight();
            info = iView.getAdapter().getPointInfo(row,column);
        } else {
            for (int i = 0; i < COLUMN_COUNT; i++) {
                for (int j = 0; j < iView.getAdapter().getRowCount(); j++) {
                    info = iView.getAdapter().getPointInfo(row,column);
                    if (isDefaultPoint(info)) {
                        column = i;
                        row = j;
                    }
                }
            }
        }
        if(info != null) {
            iView.getAdapter().onItemSelect(info);
        }
    }

    private boolean isDefaultPoint(CalendarView.PointInfo info) {
        return info.year == dYear
                && info.month == dMonth
                && info.day == dDay;
    }

    private boolean checkPosition(float[] downPosition) {
        return downPosition != null && downPosition.length == 2;
    }
}
