package com.ccx.ezxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.ccx.ezxing.DecodeType;
import com.ccx.ezxing.binarizer.RandomBinarizer;
import com.ccx.ezxing.decode.DecodeFormatManager;
import com.ccx.ezxing.decode.DecodeResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import static android.content.Context.WINDOW_SERVICE;

public class ZXingUtils {
    private ZXingUtils() {

    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;


    /**
     * @param contents       内容
     * @param widthAndHeight 宽高，因为是正方形
     * @return bitmap
     */
    public static Bitmap encodeAsBitmap(String contents, int widthAndHeight) {
        if (contents == null) {
            return null;
        }
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (matrix == null) {
            return null;
        }
        int   width  = matrix.getWidth();
        int   height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static DecodeType regexText(String text) {
        String email  = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        String URL    = "[a-zA-z]+://[^\\s]*";
        String number = "^[0-9]*$";
        if (regex(email, text)) {
            return DecodeType.EMAIL;
        } else if (regex(URL, text)) {
            return DecodeType.URL;
        } else if (regex(number, text)) {
            return DecodeType.NUMBER;
        } else {
            return DecodeType.TEXT;
        }

    }

    private static boolean regex(String regex, String text) {
        return Pattern.matches(regex, text);
    }


    public static DecodeResult decodeImage(Bitmap bitmap) {
        long start = System.currentTimeMillis();
        bitmap = getSmallerBitmap(bitmap);
        int   width  = bitmap.getWidth();
        int   height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        // 查看源码发现还有这个子类
        RGBLuminanceSource source            = new RGBLuminanceSource(width, height, pixels);
        Result             result            = null;
        MultiFormatReader  multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(getHints());

        BinaryBitmap neoBitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            result = multiFormatReader.decodeWithState(neoBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        bitmap.recycle();
        System.gc();
        DecodeResult decodeResult = new DecodeResult();
        if (result != null) {
            decodeResult.rawResult = result.getText();
        } else {
            decodeResult.rawResult = "解析失败";
        }
        decodeResult.handingTime = (end - start + 0f) / 1000 + "";

        return decodeResult;
    }


    private static Bitmap getSmallerBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() / 160000;
        if (size <= 1) {
            return bitmap; // 如果小于
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale((float) (1 / Math.sqrt(size)), (float) (1 / Math.sqrt(size)));
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }


    public static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth   = src.getWidth();
        int srcHeight  = src.getHeight();
        int logoWidth  = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float  scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap      = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }


    private static Map<DecodeHintType, Vector<BarcodeFormat>> getHints() {
        EnumMap<DecodeHintType, Vector<BarcodeFormat>> hints         = new EnumMap<>(DecodeHintType.class);
        Vector<BarcodeFormat>                          decodeFormats = new Vector<>(EnumSet.noneOf(BarcodeFormat.class));
        decodeFormats.addAll(DecodeFormatManager.ALL);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        return hints;
    }
}
