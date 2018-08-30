/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ccx.ezxing.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ccx.ezxing.camera.CameraManager;
import com.ccx.ezxing.conts.Conts;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Map;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final MultiFormatReader multiFormatReader;
    private final CameraManager     cameraManager;
    private final ResultHandler     captureActivityHandler;
    private boolean running = true;

    DecodeHandler(CameraManager cameraManager, ResultHandler captureActivityHandler, Map<DecodeHintType, Object> hints) {
        this.cameraManager = cameraManager;
        this.captureActivityHandler = captureActivityHandler;
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null || !running) {
            return;
        }
        if (message.what == 1) {
            running = false;
            Looper.myLooper().quit();
        } else {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long   start     = System.currentTimeMillis();
        Result rawResult = getResult(data, width, height);
        if (rawResult == null) {
            rawResult = getResult(rotateByteDegree90(data, width, height), width, height);
        }
        long end = System.currentTimeMillis();
        if (rawResult != null) {
            DecodeResult decodeResult = new DecodeResult();
            decodeResult.handingTime = (end - start + 0f) / 1000 + "";
            decodeResult.rawResult = rawResult;
            Message message = Message.obtain(captureActivityHandler, Conts.Scan.SUECCESS, decodeResult);
            message.sendToTarget();
        } else {
            captureActivityHandler.sendEmptyMessage(Conts.Scan.FAIL);
        }
    }

    @Nullable
    private Result getResult(byte[] data, int width, int height) {
        Result                   rawResult = null;
        PlanarYUVLuminanceSource source    = cameraManager.buildLuminanceSource(data, width, height);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            multiFormatReader.reset();
        }
        return rawResult;
    }


    private byte[] rotateByteDegree90(byte[] data, int width, int height) {
        byte[] neoData = new byte[width * height * 3 / 2];
        int    i       = 0;
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                neoData[i] = data[y * width + x];
                i++;
            }
        }
        i = width * height * 3 / 2 - 1;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < height / 2; y++) {
                neoData[i] = data[(width * height) + (y * width) + x];
                i--;
                neoData[i] = data[(width * height) + (y * width) + (x - 1)];
                i--;
            }
        }
        return neoData;
    }

    protected static class DecodeResult {
        String handingTime;
        Result rawResult;
    }


}
