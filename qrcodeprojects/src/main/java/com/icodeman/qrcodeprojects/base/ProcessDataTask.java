package com.icodeman.qrcodeprojects.base;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;

import com.icodeman.qrcodeprojects.base.utils.BarcodeLockUtil;
import com.icodeman.qrcodeprojects.base.utils.TimeRecordUtil;

public class ProcessDataTask extends AsyncTask<Void, Void, String> {

    private byte[] mData;
    private Delegate mDelegate;
    private Camera mCamera;
    private int orientation;

    public ProcessDataTask(Camera camera, byte[] data, Delegate delegate, int orientation) {
        mCamera = camera;
        mData = data;
        mDelegate = delegate;
        this.orientation = orientation;
    }

    public ProcessDataTask perform() {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
        return this;
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mDelegate = null;
        TimeRecordUtil.resetTime();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(Void... params) {
        if(!BarcodeLockUtil.requestLock()){
            return null;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;

//        byte[] rotatedData = new byte[mData.length];
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                rotatedData[x * height + height - y - 1] = mData[x + y * width];
//            }
//        }
//        int tmp = width;
//        width = height;
//        height = tmp;

        try {
            if (mDelegate == null) {
                return null;
            }
            return mDelegate.processData(mData,orientation,width, height, false);
        } catch (Exception e1) {
            try {
                return mDelegate.processData(mData,orientation,width, height, true);
            } catch (Exception e2) {
                return null;
            }
        } finally {
            BarcodeLockUtil.releaseLock();
        }
    }

    public interface Delegate {
        String processData(byte[] data,int orientation, int width, int height, boolean isRetry);
    }
}
