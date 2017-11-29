package com.icodeman.baselib.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.icodeman.baselib.R;

/**
 * @author ICodeMan
 * @date 2017/11/24
 */

public abstract class BaseActionBarActivity extends AppCompatActivity{
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar);
        setSupportActionBar(toolbar);
        ((ViewGroup)findViewById(R.id.frame)).addView(getLayoutInflater().inflate(getLayoutId(),null));
    }


    protected void initToolbar(Toolbar toolbar) {

    }

    /**
     * 获取界面的资源Id
     * @return
     */
    public abstract int getLayoutId();
}
