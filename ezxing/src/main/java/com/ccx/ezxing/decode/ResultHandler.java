package com.ccx.ezxing.decode;

import android.os.Handler;
import android.os.Message;

import com.ccx.ezxing.camera.CameraManager;
import com.ccx.ezxing.conts.Conts;
import com.ccx.ezxing.view.ScannerView;
import com.ccx.ezxing.view.ViewfinderResultPointCallback;
import com.ccx.ezxing.view.ViewfinderView;

public class ResultHandler extends Handler {

    private static final String TAG = ResultHandler.class.getSimpleName();

    private final DecodeThread   decodeThread;
    private final ViewfinderView findView;
    private final ScannerView    mScannerView;
    private       State          state;
    private final CameraManager  cameraManager;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }


    public ResultHandler(ScannerView scannerView, CameraManager cameraManager, ViewfinderView findView) {
        // 解析线程
        decodeThread = new DecodeThread(this, null, null,
                new ViewfinderResultPointCallback(findView), cameraManager);
        decodeThread.start();
        state = State.SUCCESS;
        this.findView = findView;
        this.mScannerView = scannerView;
        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Conts.Result.PREVIEW:
                restartPreviewAndDecode();
                break;
            case Conts.Result.SUECCESS:
                state = State.SUCCESS;
                DecodeHandler.DecodeResult decodeResult = (DecodeHandler.DecodeResult) message.obj;
                mScannerView.handleMessage(decodeResult.rawResult.getText(),decodeResult.handingTime);
                break;
            case Conts.Result.FAIL:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), 0);
                break;

        }

    }


    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(),1);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }
        // Be absolutely sure we don't send any queued up messages
        removeMessages(Conts.Result.PREVIEW);
        removeMessages(Conts.Result.FAIL);
        removeMessages(Conts.Result.SUECCESS);
    }

    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), 0);
            findView.drawViewfinder();
        }
    }

}
