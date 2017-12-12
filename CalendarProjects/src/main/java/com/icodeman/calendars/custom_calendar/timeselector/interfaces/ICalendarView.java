package com.icodeman.calendars.custom_calendar.timeselector.interfaces;


import com.icodeman.calendars.custom_calendar.timeselector.widget.CalendarView;

import java.util.List;

/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2017/12/8
 */
public interface ICalendarView {

    CalendarView.ICalendarAdapter getAdapter();

    List<CalendarView.PointInfo> getData();

    float[] getDownPosition();

    int getItemWidth();

    int getItemHeight();
}
