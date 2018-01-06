package com.icodeman.qrcodeprojects.base.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.icodeman.baselib.utils.LogUtil;
import com.icodeman.qrcodeprojects.base.ZXingConstants;
import com.icodeman.qrcodeprojects.base.utils.BGAQRCodeUtil;
import com.icodeman.qrcodeprojects.base.views.QRCodeView;


public class ZXingView extends QRCodeView {
    private static final String TAG_CLASS = "ZXingView";
    
    private QRCodeMultiReader reader;

    public ZXingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMultiFormatReader();
    }

    private void initMultiFormatReader() {
        reader = new QRCodeMultiReader();
    }

    @Override
    public String processData(byte[] data,int orientation,int width, int height, boolean isRetry) {
        Result rawResult = null;
        try {
            PlanarYUVLuminanceSource source = null;
            Rect rect = null;
            if(orientation == BGAQRCodeUtil.ORIENTATION_PORTRAIT) {
                rect = mScanBoxView.getScanBoxAreaRect(width);
                source = new PlanarYUVLuminanceSource(data, width, height, rect.top, rect.left, rect.height(), rect.width(), false);
            }else {
                rect = mScanBoxView.getScanBoxAreaRect(height);
                source = new PlanarYUVLuminanceSource(data,width,height,rect.left,rect.right,rect.width(),rect.height(),false);
            }
            //TODO:删除
            LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "data size : " + width + "," + height);
            LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "box point : "+rect.left+","+rect.top);
            LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "box size : "+rect.width()+","+rect.height());
            rawResult = reader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.reset();
        }
        if(rawResult != null){
            return rawResult.getText();
        }else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}