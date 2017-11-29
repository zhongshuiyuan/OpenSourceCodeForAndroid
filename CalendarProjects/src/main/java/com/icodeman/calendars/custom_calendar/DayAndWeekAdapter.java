package com.icodeman.calendars.custom_calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.icodeman.baselib.adapter.BaseHolder;
import com.icodeman.calendars.R;

import java.util.List;


/**
 * @author ICodeMan
 * @date 2017/11/24
 */

public class DayAndWeekAdapter extends CalendarView.CalendarAdapter {

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
        TextView tv = (TextView) baseView.findViewById(R.id.text);
        tv.setText(String.valueOf(pointInfo.day));
        return baseView;
    }

    @Override
    public void oItemSpecial(CalendarView.PointInfo info, boolean isSelected) {
        if(isSelected){
            DayHolder holder = new DayHolder(info.view);
            holder.textView.setTextColor(context.getResources().getColor(R.color.white_ffffffff));
        }else {
            DayHolder holder = new DayHolder(info.view);
            holder.textView.setTextColor(Color.RED);
        }
    }

    @Override
    public void onItemSelect(CalendarView.PointInfo info) {
        super.onItemSelect(info);
        DayHolder holder = new DayHolder(info.view);
        holder.textView.setTextColor(context.getResources().getColor(R.color.white_ffffffff));
    }

    @Override
    public void onItemReset(CalendarView.PointInfo info) {
        super.onItemReset(info);
        DayHolder holder = new DayHolder(info.view);
        holder.textView.setTextColor(context.getResources().getColor(R.color.black_ff000000));
    }

    private class DayHolder extends BaseHolder{
        public TextView textView;

        public DayHolder(View baseView){
            super(baseView);
            textView = findViewById(R.id.text);
        }
    }
}
