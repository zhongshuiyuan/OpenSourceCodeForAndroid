package com.icodeman.qrcodeprojects.base.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.FoundPartException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.icodeman.baselib.utils.LogUtil;
import com.icodeman.qrcodeprojects.base.ZXingConstants;
import com.icodeman.qrcodeprojects.base.utils.BGAQRCodeUtil;
import com.icodeman.qrcodeprojects.base.views.QRCodeView;

import java.util.List;

/**
 * @author ICodeMan
 * @github https://github.com/LMW-ICodeMan
 * @date 2018/1/11
 */
public class ZXingView extends QRCodeView {
    private static final String TAG_CLASS = "ZXingView";
    
    private QRCodeReader reader;

    public ZXingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMultiFormatReader();
    }

    private void initMultiFormatReader() {
        reader = new QRCodeReader();
    }

    @Override
    public String processData(byte[] data,int orientation,int width, int height, boolean isRetry) {
        Result rawResult = null;
        PlanarYUVLuminanceSource source = null;
        Rect rect = null;
        int boxWidth = 0;
        int boxHeight = 0;
        try {
            if(orientation == BGAQRCodeUtil.ORIENTATION_PORTRAIT) {
                rect = mScanBoxView.getScanBoxAreaRect(width);
                boxWidth = rect.height();
                boxHeight = rect.width();
                source = new PlanarYUVLuminanceSource(data, width, height, rect.top, rect.left, rect.height(), rect.width(), false);
            }else {
                rect = mScanBoxView.getScanBoxAreaRect(height);
                boxWidth = rect.width();
                boxHeight = rect.height();
                source = new PlanarYUVLuminanceSource(data,width,height,rect.left,rect.top,rect.width(),rect.height(),false);
            }
            rawResult = reader.decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
        } catch (FoundPartException e) {
            List<ResultPoint> points = e.getFoundPoints();
            LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "find part size : "+points.size());
            if(points.size() <= 1){
                LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "发现的点不支持判断焦距。");
            }else if(points.size() == 2){
                setZoomPoint(points.get(0),points.get(1),boxWidth,boxHeight);
            }
            rawResult = null;
        } catch (Exception e){
            LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "error : "+Log.getStackTraceString(e));
            rawResult = null;
        }finally {
            reader.reset();
        }
        if(rawResult != null){
            ResultPoint[] points = rawResult.getResultPoints();
            if(points != null && points.length>=3) {
                setZoomPoint(points[0],points[2],boxWidth,boxHeight);
            }
            return rawResult.getText();
        }else {
            return null;
        }
    }

    public void setZoomPoint(ResultPoint point1,ResultPoint point2,int boxWidth,int boxHeight){
        float xPart = Math.abs(point1.getX() - point2.getX());
        float yPart = Math.abs(point1.getY() - point2.getY());
        LogUtil.e(ZXingConstants.TAG_MODEL,TAG_CLASS, "点间距：("+xPart+","+yPart+")");
        float ratio;
        if(xPart >= yPart){
            ratio = xPart/boxWidth;
        }else {
            ratio = yPart/boxHeight;
        }
        setZoomRatio(ratio);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}