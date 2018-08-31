package com.ccx.ezxing.binarizer;

import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.Random;

public class RandomBinarizer extends GlobalHistogramBinarizer {

    private static float[] CANDIDATES = {
            -1.0f, -1.0f -1.0f, -1.0f, -1.0f,  // < 0 : global histogram binarizer
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,       // == 0 : hybrid binarizer
            1.02f, 1.04f, 1.06f, 1.08f, 1.1f    // > 0 : Adjusted hybrid binarizer with scale factor K.
    };

    private BitMatrix mMatrix;

    public RandomBinarizer(LuminanceSource source) {
        super(source);
    }

    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        if (mMatrix != null) {
            return mMatrix;
        }

        int rand = new Random().nextInt(CANDIDATES.length);
        float factor = CANDIDATES[rand];
        if (factor < 0.0f) {
            mMatrix =  super.getBlackMatrix();      // Global histogram binarizer.
        } else if (factor < 0.00001f && factor > -0.00001f) {
            HybridBinarizer binarizer = new HybridBinarizer(getLuminanceSource());
            mMatrix = binarizer.getBlackMatrix();
        } else {
            AdjustedHybridBinarizer binarizer = new AdjustedHybridBinarizer(getLuminanceSource(), factor);
            mMatrix = binarizer.getBlackMatrix();
        }
        return mMatrix;
    }

    @Override
    public Binarizer createBinarizer(LuminanceSource source) {
        return new RandomBinarizer(source);
    }
}