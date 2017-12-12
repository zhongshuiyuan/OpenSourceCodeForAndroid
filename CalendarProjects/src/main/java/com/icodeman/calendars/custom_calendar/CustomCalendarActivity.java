package com.icodeman.calendars.custom_calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.icodeman.baselib.activity.BaseActionBarActivity;
import com.icodeman.baselib.utils.DensityUtil;
import com.icodeman.baselib.utils.TimeUtil;
import com.icodeman.calendars.R;
import com.icodeman.calendars.custom_calendar.timeselector.adapter.DayAndWeekAdapter;
import com.icodeman.calendars.custom_calendar.timeselector.widget.CalendarView;


/**
 * @author ICodeMan
 * @date 2017/11/23
 */
public class CustomCalendarActivity extends BaseActionBarActivity implements View.OnClickListener {

    TextView tvDate;

    CalendarView calendarView;
    DayAndWeekAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom_calendar;
    }

    private long time;
    private TimeUtil.TimeInfo timeInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvDate = (TextView) findViewById(R.id.tv_time);
        findViewById(R.id.bt_last).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);

        initTime();

        initCalendar();
    }

    private void initTime() {
        time = System.currentTimeMillis();
        timeInfo = TimeUtil.getTimeInfo(time,true);
        changeTitle(timeInfo);
    }

    private void initCalendar() {
        calendarView = (CalendarView) findViewById(R.id.calendar);

        calendarView.setSelectorStyle(CalendarView.STYLE_NO_SELECTOR);
        calendarView.setLineOffset(DensityUtil.dip2px(this, 6));
        calendarView.setLineColorNormal(getResources().getColor(R.color.gray_666666));
        calendarView.setLineColorSelect(getResources().getColor(R.color.black));
        calendarView.setCircleColor(this.getResources().getColor(R.color.color_eb5a39));

        updateAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.no_style) {
            calendarView.setSelectorStyle(CalendarView.STYLE_NO_SELECTOR);
        } else if (item.getItemId() == R.id.day_style) {
            calendarView.setSelectorStyle(CalendarView.STYLE_DAY_SELECTOR);
        } else if (item.getItemId() == R.id.week_style) {
            calendarView.setSelectorStyle(CalendarView.STYLE_WEEK_SELECTOR);
        }
        updateAdapter();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_last) {
            changeTimeInfo(-1);
        } else if (v.getId() == R.id.bt_next) {
            changeTimeInfo(1);
        }
    }

    private void changeTimeInfo(int plusDay) {
        timeInfo.month += plusDay;
        if (timeInfo.month < 0) {
            timeInfo.year --;
            timeInfo.month = 11;
        }
        if (timeInfo.month > 11) {
            timeInfo.year++;
            timeInfo.month = 0;
        }
        changeTitle(timeInfo);
        updateAdapter();
    }

    private void changeTitle(TimeUtil.TimeInfo info){
        tvDate.setText(getString(R.string.time_selector_month, info.year, info.month + 1));
    }

    private void updateAdapter() {
        if(adapter == null || calendarView.getAdapter() == null){
            adapter = new DayAndWeekAdapter(this);
            calendarView.setAdapter(adapter);
        }
//        adapter.setSpecialTime(timeInfo.year,timeInfo.month,timeInfo.day);
        adapter.setTime(timeInfo.year, timeInfo.month, timeInfo.week);
        adapter.notifyDataSetChange();
    }
}
