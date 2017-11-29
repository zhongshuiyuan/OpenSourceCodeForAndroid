package com.icodeman.calendars.custom_calendar;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.icodeman.baselib.activity.BaseActionBarActivity;
import com.icodeman.baselib.utils.TimeUtil;
import com.icodeman.calendars.R;

import java.util.Calendar;


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
    private int[] timeInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendarView = (CalendarView) findViewById(R.id.calendar);
        tvDate = (TextView) findViewById(R.id.tv_time);

        findViewById(R.id.bt_last).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);


        adapter = new DayAndWeekAdapter(CustomCalendarActivity.this);
        calendarView.setSelectorStyle(CalendarView.STYLE_WEEK_SELECTOR);
        calendarView.setLineColorUnable(getResources().getColor(R.color.white_ffffffff));
        calendarView.setLineColorNormal(getResources().getColor(R.color.white_ff8e8e8e));
        calendarView.setLineColorSelect(getResources().getColor(R.color.white_808e8e8e));

        time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int week = calendar.get(Calendar.WEEK_OF_MONTH);
        timeInfo = TimeUtil.getTimeInfo(time);
        changeTimeInfo(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDefaltWeek(year, month, week - 1);
                adapter.addSpecial(year, month, day);
                calendarView.setAdapter(adapter);
            }
        }, 2000);
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
        } else if (item.getItemId() == R.id.day_and_week) {
            calendarView.setSelectorStyle(CalendarView.STYLE_DAY_AND_WEEK_SELECTOR);
        }
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
        timeInfo[1] += plusDay;
        if (timeInfo[1] < 0) {
            timeInfo[0]--;
            timeInfo[1] = TimeUtil.WEEKS_OF_YEAR - 1;
        }
        if (timeInfo[1] > TimeUtil.WEEKS_OF_YEAR - 1) {
            timeInfo[0]++;
            timeInfo[1] = 0;
        }
        tvDate.setText(getString(R.string.date_string_year_month, timeInfo[0], timeInfo[1] + 1));
        updateAdapter();
    }

    private void updateAdapter() {
        adapter.setTime(timeInfo[0], timeInfo[1], timeInfo[2]);
        adapter.notifyDataSetChange();
    }
}
