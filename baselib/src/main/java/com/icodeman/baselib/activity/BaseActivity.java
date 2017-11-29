package com.icodeman.baselib.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.icodeman.baselib.R;

/**
 * @author ICodeMan
 * @date 2017/11/24
 */

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
    }

    /**
     * 获取界面的资源Id
     * @return
     */
    public abstract int getLayoutId();
}
