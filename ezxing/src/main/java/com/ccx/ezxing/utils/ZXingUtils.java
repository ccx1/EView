package com.ccx.ezxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.ccx.ezxing.DecodeType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import static android.content.Context.WINDOW_SERVICE;

public class ZXingUtils {
    private ZXingUtils() {

    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static Bitmap encodeAsBitmap(Context context, String contents) {
        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        assert manager != null;
        Display display     = manager.getDefaultDisplay();
        Point   displaySize = new Point();
        display.getSize(displaySize);
        int screenWdith      = displaySize.x;
        int screenHeight     = displaySize.y;
        int smallerDimension = screenWdith < screenHeight ? screenWdith : screenHeight;
        smallerDimension = smallerDimension * 7 / 8;
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints    = null;
        String                      encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        BitMatrix result = null;
        try {
            BarcodeFormat format = BarcodeFormat.QR_CODE;
            result = new MultiFormatWriter().encode(contents, format, smallerDimension, smallerDimension, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            iae.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        assert result != null;
        int   width  = result.getWidth();
        int   height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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


}
