package com.icodeman.qrcodeprojects.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.icodeman.baselib.activity.BaseActivity;
import com.icodeman.baselib.utils.LogUtil;
import com.icodeman.qrcodeprojects.R;
import com.icodeman.qrcodeprojects.base.ZXingConstants;
import com.icodeman.qrcodeprojects.base.views.ZXingView;
import com.icodeman.qrcodeprojects.base.views.QRCodeView;

/**
 * Created by lmw on 2017/9/19.
 */

public class BarCodeLoginActivity extends BaseActivity implements QRCodeView.Delegate {
    public static final String TAG = "BarCodeLoginActivity";

    private ZXingView mQRCodeView;

    @Override
    protected void onCreate(Bundle saveinstance) {
        super.onCreate(saveinstance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bar_code_login_new;
    }

    private void init() {
        mQRCodeView = (ZXingView) findViewById(R.id.zxing_view);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopSpot();
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtil.e(ZXingConstants.TAG_MODEL,TAG," result : "+result);
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        mQRCodeView.stopSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LogUtil.e(ZXingConstants.TAG_MODEL,TAG,"摄像头打开失败 ！");
    }

}
