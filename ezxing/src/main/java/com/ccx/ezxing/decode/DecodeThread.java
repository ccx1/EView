package com.ccx.ezxing.decode;

import android.os.Handler;
import android.os.Looper;

import com.ccx.ezxing.camera.CameraManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP        = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private final Map<DecodeHintType, Object> hints;
    private final CameraManager               cameraManager;
    private final ResultHandler               captureActivityHandler;
    private       Handler                     handler;
    private final CountDownLatch              handlerInitLatch;

    DecodeThread(ResultHandler captureActivityHandler, Vector<BarcodeFormat> decodeFormats,
                 String characterSet,
                 ResultPointCallback resultPointCallback, CameraManager cameraManager) {
        handlerInitLatch = new CountDownLatch(1);
        this.cameraManager = cameraManager;
        this.captureActivityHandler = captureActivityHandler;
        hints = new EnumMap<>(DecodeHintType.class);
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<>(EnumSet.noneOf(BarcodeFormat.class));
//            decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
//            decodeFormats.addAll(DecodeFormatManager.COMMON);
//            decodeFormats.addAll(DecodeFormatManager.ALL);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.TRY_HARDER, true);
//        hints.put(DecodeHintType.PURE_BARCODE, true);
//        hints.put(DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT, true);
//        hints.put(DecodeHintType.ASSUME_GS1, true);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        }
//        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    public Map<DecodeHintType, Object> getHints() {
        return hints;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(cameraManager, captureActivityHandler, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
