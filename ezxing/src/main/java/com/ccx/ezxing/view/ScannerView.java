package com.ccx.ezxing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ccx.ezxing.camera.CameraManager;
import com.ccx.ezxing.decode.AmbientLightManager;
import com.ccx.ezxing.decode.ResultHandler;
import com.ccx.ezxing.listener.ParsingCompleteListener;
import com.ccx.ezxing.utils.ZXingUtils;

import java.io.IOException;

public class ScannerView extends FrameLayout {

    private Context        mContext;
    private ViewfinderView mViewfinderView;
    private CameraManager  cameraManager;
    private boolean        hasSurface;
    private ResultHandler  handler;
    private ParsingCompleteListener parsingCompleteListener;
    private SurfaceView mSurfaceView;

    public ScannerView(Context context) {
        super(context);
        initView(context);
    }


    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.setLayoutParams(layoutParams);
        this.addView(mSurfaceView);
        mSurfaceView.setZOrderOnTop(false);
        mViewfinderView = new ViewfinderView(mContext, null);
        mViewfinderView.setLayoutParams(layoutParams);
        this.addView(mViewfinderView);
        cameraManager = new CameraManager(mContext.getApplicationContext());
        AmbientLightManager ambientLightManager = new AmbientLightManager(mContext);
        mViewfinderView.setCameraManager(cameraManager);
        ambientLightManager.start(cameraManager);
        SurfaceHolder holder = mSurfaceView.getHolder();
        if (hasSurface) {
            initCamera(holder);
        } else {
            holder.addCallback(new HolderCallBack());
        }
    }

    private synchronized void initCamera(SurfaceHolder holder) {
        try {
            cameraManager.openDriver(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (handler == null) {
            handler = new ResultHandler(this,cameraManager, mViewfinderView);
        }
    }

    public void setOnParsingCompleteListener(ParsingCompleteListener parsingCompleteListener) {
        this.parsingCompleteListener = parsingCompleteListener;
    }


    public void reset() {
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    public void handleMessage(String text, String handingTime) {
        if (parsingCompleteListener!=null) {
            parsingCompleteListener.onComplete(text,handingTime,ZXingUtils.regexText(text));
        }
    }

    public void release() {
        if (handler!= null) {
           handler.quitSynchronously();
        }
    }


    class HolderCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!hasSurface) {
                hasSurface = true;
                initCamera(holder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            hasSurface = false;
        }
    }

}
