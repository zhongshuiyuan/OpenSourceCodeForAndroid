package com.icodeman.calendars;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.icodeman.baselib.activity.BaseActivity;
import com.icodeman.baselib.builder.BaseTitleBuilder;

/**
 * @author lmw
 * @date 2019/3/11.
 */
public class CalendarMainActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTitle(BaseTitleBuilder builder) {
        super.initTitle(builder);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_calendar_main;
    }

    @Override
    public String getCenterTitle() {
        return null;
    }
}
