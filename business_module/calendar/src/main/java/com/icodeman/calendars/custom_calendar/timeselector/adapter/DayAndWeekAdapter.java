package com.icodeman.calendars.custom_calendar.timeselector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.icodeman.calendars.R;
import com.icodeman.calendars.custom_calendar.timeselector.widget.CalendarView;

import java.util.List;

/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2017/12/8
 */
public class DayAndWeekAdapter extends CalendarView.CalendarAdapter {
    private static final int TYPE_SELECTED_DAY = 1;
    private static final int TYPE_SELECTED_MONTH = 2;

    private int spYear,spMonth,spDay;

    public DayAndWeekAdapter(Context context) {
        super(context);
    }

    @Override
    public View getWeeksView(int column) {
        String[] weeks = new String[]{"一", "二", "三", "四", "五", "六", "日"};
        View baseView = LayoutInflater.from(context).inflate(R.layout.layout_week_item, null);
        TextView tv = (TextView) baseView.findViewById(R.id.text);
        tv.setText(weeks[column]);
        return baseView;
    }

    @Override
    public View getDaysView(CalendarView.PointInfo pointInfo) {
        View baseView = LayoutInflater.from(context).inflate(R.layout.layout_day_item, null);
        pointInfo.view = baseView;
        resetDefaultView(pointInfo);
        return baseView;
    }

    @Override
    public void onItemSelect(CalendarView.PointInfo info) {
        super.onItemSelect(info);
        changeSelectView(info,TYPE_SELECTED_DAY);
    }

    @Override
    public void onLineSelect(List<CalendarView.PointInfo> infos) {
        super.onLineSelect(infos);
        for (CalendarView.PointInfo info:infos){
            changeSelectView(info,TYPE_SELECTED_MONTH);
        }
    }

    @Override
    public void onItemReset(CalendarView.PointInfo info) {
        super.onItemReset(info);
        resetDefaultView(info);
    }

    public void setSpecialTime(int year,int month,int day){
        spYear = year;
        spMonth = month;
        spDay = day;
    }

    private boolean isDefaultDay(int year,int month,int day){
        return spYear == year && spMonth == month && spDay == day;
    }

    private void resetDefaultView(CalendarView.PointInfo info){
        DayHolder holder = new DayHolder(info.view);
        if(isDefaultDay(info.year,info.month,info.day)) {
            holder.textView.setTextColor(context.getResources().getColor(R.color.color_eb5a39));
        }else {
            holder.textView.setTextColor(Color.BLACK);
        }
        holder.textView.setText(String.valueOf(info.day));
        holder.textView.setBackgroundDrawable(null);
    }

    private void changeSelectView(CalendarView.PointInfo info, int type){
        DayHolder holder = new DayHolder(info.view);
        if(isDefaultDay(info.year,info.month,info.day)) {
//            选中的是当天
            if(type == TYPE_SELECTED_DAY) {
                holder.textView.setTextColor(Color.WHITE);
                holder.textView.setBackgroundResource(R.drawable.drawable_calendar_bg_checked_today);
            }else {
                holder.textView.setTextColor(context.getResources().getColor(R.color.color_eb5a39));
            }
        }else {
            if(type == TYPE_SELECTED_DAY) {
                holder.textView.setTextColor(Color.WHITE);
                holder.textView.setBackgroundResource(R.drawable.drawable_calendar_bg_checked);
            }else {
                holder.textView.setTextColor(Color.WHITE);
            }
        }
    }

    private class DayHolder {
        public TextView textView;

        public DayHolder(View baseView){
            textView = (TextView)baseView.findViewById(R.id.text);
        }
    }
}
