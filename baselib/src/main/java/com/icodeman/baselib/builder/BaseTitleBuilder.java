package com.icodeman.baselib.builder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icodeman.baselib.R;

/**
 * 标题栏的构造器
 *
 * @author lmw
 * @date 2019/3/11.
 */
public class BaseTitleBuilder {
    private Context context;
    /*********   Views   ************/
    private View layoutView;
    private View dividerView;
    private ImageView leftImgView ;
    private TextView leftTextView ;
    private TextView centerTextView;
    private TextView rightTextView;
    private ImageView rightImgView;
    public BaseTitleBuilder(Activity activity) {
        this.context = activity;

        layoutView = activity.findViewById(R.id.base_title_layout);
        dividerView = activity.findViewById(R.id.base_title_divider);
        leftImgView = (ImageView)activity.findViewById(R.id.base_title_left_image);
        leftTextView = (TextView)activity.findViewById(R.id.base_title_left_text);
        centerTextView = (TextView)activity.findViewById(R.id.base_title_center_text);
        rightTextView = (TextView)activity.findViewById(R.id.base_title_right_text);
        rightImgView = (ImageView)activity.findViewById(R.id.base_title_right_image);
    }

    public BaseTitleBuilder showTitleLayout(boolean showTitleLayout){
        layoutView.setVisibility(showTitleLayout?View.VISIBLE:View.GONE);
        return this;
    }

    public BaseTitleBuilder backgroundColor(int backgroundColor) {
        layoutView.setBackgroundColor(backgroundColor);
        return this;
    }

    public BaseTitleBuilder showDividerLine(boolean showDividerLine) {
        dividerView.setVisibility(showDividerLine?View.VISIBLE:View.GONE);
        return this;
    }

    public BaseTitleBuilder showLeftImg(boolean showLeftImg) {
        leftImgView.setVisibility(showLeftImg?View.VISIBLE:View.GONE);
        return this;
    }

    public BaseTitleBuilder leftImage(int resId) {
        Bitmap leftImage = BitmapFactory.decodeResource(context.getResources(),resId);
        leftImgView.setImageBitmap(leftImage);
        return this;
    }

    public BaseTitleBuilder leftImage(Bitmap leftImage) {
        leftImgView.setImageBitmap(leftImage);
        return this;
    }

    public BaseTitleBuilder leftImgClick(View.OnClickListener listener) {
        leftImgView.setOnClickListener(listener);
        return this;
    }

    public BaseTitleBuilder showLeftText(boolean showLeftText) {
        leftTextView.setVisibility(showLeftText?View.VISIBLE:View.GONE);
        return this;
    }

    public BaseTitleBuilder leftTextColor(int leftTextColor) {
        leftTextView.setTextColor(leftTextColor);
        return this;
    }

    public BaseTitleBuilder leftText(String leftText) {
        leftTextView.setText(leftText);
        return this;
    }

    public BaseTitleBuilder leftTextClick(View.OnClickListener listener) {
        leftTextView.setOnClickListener(listener);
        return this;
    }

    public BaseTitleBuilder centerTitleColor(int centerTitleColor) {
        centerTextView.setTextColor(centerTitleColor);
        return this;
    }

    public BaseTitleBuilder centerTitleText(String centerTitleText) {
        centerTextView.setText(centerTitleText);
        return this;
    }

    public BaseTitleBuilder showRightText(boolean showRightText) {
        rightTextView.setVisibility(showRightText?View.VISIBLE:View.GONE);
        return this;
    }

    public BaseTitleBuilder rightTextColor(int rightTextColor) {
        rightTextView.setTextColor(rightTextColor);
        return this;
    }

    public BaseTitleBuilder rightText(String rightText) {
        rightTextView.setText(rightText);
        return this;
    }

    public BaseTitleBuilder rightTextClick(View.OnClickListener listener) {
        rightTextView.setOnClickListener(listener);
        return this;
    }

    public BaseTitleBuilder showRightImg(boolean showRightImg) {
        rightImgView.setVisibility(showRightImg?View.VISIBLE:View.GONE);
        return this;
    }

    public BaseTitleBuilder rightImage(Bitmap rightImage) {
        rightImgView.setImageBitmap(rightImage);
        return this;
    }

    public BaseTitleBuilder rightImgClick(View.OnClickListener listener) {
        rightImgView.setOnClickListener(listener);
        return this;
    }
}
