package com.ccx.ezxing.decode;

import android.content.Intent;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

public class DecodeFormatManager {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public static final  Vector<BarcodeFormat> PRODUCT_FORMATS;
    public static final  Vector<BarcodeFormat> INDUSTRIAL_FORMATS;
    private static final Vector<BarcodeFormat> ONE_D_FORMATS;
    public static final Vector<BarcodeFormat> QR_CODE_FORMATS     = new Vector<>();
    public static final Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<>();
    public static final Vector<BarcodeFormat> AZTEC_FORMATS       = new Vector<>();
    public static final Vector<BarcodeFormat> PDF417_FORMATS      = new Vector<>();
    public static final Vector<BarcodeFormat> ALL      = new Vector<>();
    public static final Vector<BarcodeFormat> COMMON      = new Vector<>();

    static {
        PRODUCT_FORMATS = new Vector<>();
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_14);
        PRODUCT_FORMATS.add(BarcodeFormat.RSS_EXPANDED);

        INDUSTRIAL_FORMATS = new Vector<>();
        INDUSTRIAL_FORMATS.add(BarcodeFormat.CODE_39);
        INDUSTRIAL_FORMATS.add(BarcodeFormat.CODE_93);
        INDUSTRIAL_FORMATS.add(BarcodeFormat.CODE_128);
        INDUSTRIAL_FORMATS.add(BarcodeFormat.ITF);
        INDUSTRIAL_FORMATS.add(BarcodeFormat.CODABAR);

        ONE_D_FORMATS = new Vector<>();
        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
        ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);

        COMMON.add(BarcodeFormat.CODE_128);
        COMMON.add(BarcodeFormat.QR_CODE);

        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
        DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        AZTEC_FORMATS.add(BarcodeFormat.AZTEC);
        PDF417_FORMATS.add(BarcodeFormat.PDF_417);
    }

    static {
        ALL.addAll(QR_CODE_FORMATS);
        ALL.addAll(DATA_MATRIX_FORMATS);
        ALL.addAll(AZTEC_FORMATS);
        ALL.addAll(PDF417_FORMATS);
        ALL.addAll(ONE_D_FORMATS);
        ALL.addAll(INDUSTRIAL_FORMATS);
        ALL.addAll(PRODUCT_FORMATS);
        ALL.add(BarcodeFormat.MAXICODE);
        ALL.add(BarcodeFormat.UPC_EAN_EXTENSION);
    }

    private static final Map<String, Vector<BarcodeFormat>> FORMATS_FOR_MODE;

    static {
        FORMATS_FOR_MODE = new HashMap<>();
        FORMATS_FOR_MODE.put(Intents.Scan.ONE_D_MODE, ONE_D_FORMATS);
        FORMATS_FOR_MODE.put(Intents.Scan.PRODUCT_MODE, PRODUCT_FORMATS);
        FORMATS_FOR_MODE.put(Intents.Scan.QR_CODE_MODE, QR_CODE_FORMATS);
        FORMATS_FOR_MODE.put(Intents.Scan.DATA_MATRIX_MODE, DATA_MATRIX_FORMATS);
        FORMATS_FOR_MODE.put(Intents.Scan.AZTEC_MODE, AZTEC_FORMATS);
        FORMATS_FOR_MODE.put(Intents.Scan.PDF417_MODE, PDF417_FORMATS);
    }

    private DecodeFormatManager() {
    }

    static Vector<BarcodeFormat> parseDecodeFormats(Intent intent) {
        Iterable<String> scanFormats       = null;
        CharSequence     scanFormatsString = intent.getStringExtra(Intents.Scan.FORMATS);
        if (scanFormatsString != null) {
            scanFormats = Arrays.asList(COMMA_PATTERN.split(scanFormatsString));
        }
        return parseDecodeFormats(scanFormats, intent.getStringExtra(Intents.Scan.MODE));
    }

    static Vector<BarcodeFormat> parseDecodeFormats(Uri inputUri) {
        List<String> formats = inputUri.getQueryParameters(Intents.Scan.FORMATS);
        if (formats != null && formats.size() == 1 && formats.get(0) != null) {
            formats = Arrays.asList(COMMA_PATTERN.split(formats.get(0)));
        }
        return parseDecodeFormats(formats, inputUri.getQueryParameter(Intents.Scan.MODE));
    }

    private static Vector<BarcodeFormat> parseDecodeFormats(Iterable<String> scanFormats, String decodeMode) {
        if (scanFormats != null) {
            Vector<BarcodeFormat> formats = new Vector<>(EnumSet.noneOf(BarcodeFormat.class));
            try {
                for (String format : scanFormats) {
                    formats.add(BarcodeFormat.valueOf(format));
                }
                return formats;
            } catch (IllegalArgumentException iae) {
                // ignore it then
            }
        }
        if (decodeMode != null) {
            return FORMATS_FOR_MODE.get(decodeMode);
        }
        return null;
    }
}