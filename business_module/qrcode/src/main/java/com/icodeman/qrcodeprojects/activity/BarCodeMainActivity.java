package com.icodeman.qrcodeprojects.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.icodeman.baselib.activity.BaseActivity;
import com.icodeman.baselib.builder.BaseTitleBuilder;
import com.icodeman.baselib.utils.LogUtil;
import com.icodeman.baselib.utils.PermissionUtils;
import com.icodeman.qrcodeprojects.R;
import com.icodeman.qrcodeprojects.base.ZXingConstants;
import com.icodeman.qrcodeprojects.base.views.QRCodeView;
import com.icodeman.qrcodeprojects.base.views.ZXingView;

/**
 * @author lmw
 * @date 2019/3/11.
 */
public class BarCodeMainActivity extends BaseActivity implements QRCodeView.Delegate {
    public static final String TAG = "BarCodeLoginActivity";

    private ZXingView mQRCodeView;

    @Override
    protected void onCreate(Bundle saveinstance) {
        super.onCreate(saveinstance);
        mQRCodeView = (ZXingView) findViewById(R.id.zxing_view);
        mQRCodeView.setDelegate(this);
        PermissionUtils.checkAndRequestPermission(this,1, Manifest.permission.CAMERA);
    }

    @Override
    protected void initTitle(BaseTitleBuilder builder) {
        super.initTitle(builder);
        builder.showLeftImg(true)
                .leftImage(R.drawable.open_left)
                .leftImgClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_barcode_main;
    }

    @Override
    public String getCenterTitle() {
        return getString(R.string.project_name_qrcode);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户授权成功
                mQRCodeView.startCamera();
                mQRCodeView.startSpot();
            }else{
                //用户授权失败
                Toast.makeText(this,"用户未开启相机权限！",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
