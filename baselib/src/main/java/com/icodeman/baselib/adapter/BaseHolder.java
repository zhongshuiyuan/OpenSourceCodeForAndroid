package com.icodeman.baselib.adapter;

import android.view.View;

/**
 * @author ICodeMan
 * @date 2017/11/24
 */

public abstract class BaseHolder {
    private View baseView;

    public BaseHolder(View view){
        baseView = view;
    }

    public <V extends View> V findViewById(int resId){
        return (V)baseView.findViewById(resId);
    }

    public View getBaseView(){
        return baseView;
    }
}
