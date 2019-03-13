package com.icodeman.baselib.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.icodeman.baselib.R;
import com.icodeman.baselib.builder.BaseTitleBuilder;

/**
 * @author ICodeMan
 * @date 2017/11/24
 */

public abstract class BaseActivity extends Activity {
    private View baseView;
    private BaseTitleBuilder mTitleBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mTitleBuilder = new BaseTitleBuilder(this);
        initTitle(mTitleBuilder);

        baseView = getLayoutInflater().inflate(getLayoutId(),null);
        ((ViewGroup)findViewById(R.id.frame)).addView(baseView);
    }

    protected void initTitle(BaseTitleBuilder builder) {
        builder.centerTitleText(getCenterTitle());
        builder.showDividerLine(true);
    }

    /**
     * 获取界面的资源Id
     * @return
     */
    public abstract int getLayoutId();

    public abstract String getCenterTitle();

    public <V extends View> V getView(int resId){
        return (V)baseView.findViewById(resId);
    }
}
