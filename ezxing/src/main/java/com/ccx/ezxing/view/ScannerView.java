package com.ccx.ezxing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ccx.ezxing.R;
import com.ccx.ezxing.camera.CameraManager;
import com.ccx.ezxing.camera.open.OpenCamera;
import com.ccx.ezxing.decode.AmbientLightManager;
import com.ccx.ezxing.decode.DecodeFormatManager;
import com.ccx.ezxing.decode.ResultHandler;
import com.ccx.ezxing.listener.ParsingCompleteListener;
import com.ccx.ezxing.utils.ZXingUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.io.IOException;
import java.util.Vector;

public class ScannerView extends FrameLayout {

    private Context                 mContext;
    private ViewfinderView          mViewfinderView;
    private CameraManager           cameraManager;
    private boolean                 hasSurface;
    private ResultHandler           handler;
    private ParsingCompleteListener parsingCompleteListener;
    private SurfaceView             mSurfaceView;
    private boolean Product    = true;
    private boolean Industrial = true;
    private boolean QrCode     = true;
    private boolean DataMatrix = true;
    private boolean Aztec;
    private boolean Pdf417;
    public static final int CAMERA_FACING_BACK = 0;

    /**
     * The facing of the camera is the same as that of the screen.
     */
    public static final int CAMERA_FACING_FRONT = 1;
    private boolean mOpenFront;


    public ScannerView(Context context) {
        this(context, null);
    }


    public ScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("Recycle") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScannerView);
        mOpenFront = typedArray.getBoolean(R.styleable.ScannerView_openFront, false);
        typedArray.recycle();
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        initBasicView();
        initCamera();
    }

    private void initCamera() {
        cameraManager = new CameraManager(mContext.getApplicationContext());
        cameraManager.setManualCameraId(mOpenFront ? CAMERA_FACING_FRONT : CAMERA_FACING_BACK);
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

    private void initBasicView() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.setLayoutParams(layoutParams);
        this.addView(mSurfaceView);
        mSurfaceView.setZOrderOnTop(false);
        mViewfinderView = new ViewfinderView(mContext, null);
        mViewfinderView.setLayoutParams(layoutParams);
        this.addView(mViewfinderView);
    }


    public void openFlash(boolean isOpen) {
        OpenCamera        camera      = cameraManager.getCamera();
        Camera.Parameters mParameters = null;
        Camera            theCamera   = null;
        if (camera != null) {
            theCamera = camera.getCamera();
            mParameters = theCamera.getParameters();
        }
        if (isOpen) {
            try {
                assert mParameters != null;
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                theCamera.setParameters(mParameters);
            } catch (Exception ignored) {
            }
        } else {
            try {
                assert mParameters != null;
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                theCamera.setParameters(mParameters);
            } catch (Exception ignored) {
            }

        }
    }

    private synchronized void initCamera(SurfaceHolder holder) {
        try {
            cameraManager.openDriver(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (handler == null) {
            handler = new ResultHandler(this, cameraManager, mViewfinderView);
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
        if (parsingCompleteListener != null) {
            parsingCompleteListener.onComplete(text, handingTime, ZXingUtils.regexText(text));
        }
    }

    public void release() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        OpenCamera camera = cameraManager.getCamera();
        if (camera != null) {
            if (cameraManager.isPreviewing()) {
                cameraManager.stopPreview();
            }
            Camera theCamera = camera.getCamera();
            assert theCamera != null;
            theCamera.release();
            cameraManager.setPreviewing(false);
        }

    }


    class HolderCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!hasSurface) {
                hasSurface = true;
                initCamera();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            hasSurface = false;
            release();
        }
    }

}
