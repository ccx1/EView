package com.ccx.ezxing.reader;

import android.graphics.Rect;
import android.hardware.Camera;

import com.ccx.ezxing.camera.CameraManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.decoder.Decoder;
import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;
import com.google.zxing.qrcode.detector.Detector;

import java.util.List;
import java.util.Map;

public class QRCodeReader implements Reader {

    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];

    private final Decoder decoder = new Decoder();
    private final CameraManager mCameraManager;

    QRCodeReader(CameraManager cameraManager) {
        this.mCameraManager = cameraManager;
    }

    protected final Decoder getDecoder() {
        return decoder;
    }

    /**
     * Locates and decodes a QR code in an image.
     *
     * @return a String representing the content encoded by the QR code
     * @throws NotFoundException if a QR code cannot be found
     * @throws FormatException   if a QR code cannot be decoded
     * @throws ChecksumException if error correction fails
     */
    @Override
    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return decode(image, null);
    }

    @Override
    public final Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints)
            throws NotFoundException, ChecksumException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            BitMatrix bits = extractPureBits(image.getBlackMatrix());
            decoderResult = decoder.decode(bits, hints);
            points = NO_POINTS;
        } else {
            //1、将图像进行二值化处理，1、0代表黑、白。( 二维码的使用getBlackMatrix方法 )
            //2、寻找定位符、校正符，然后将原图像中符号码部分取出。（detector代码实现的功能）
            DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect(hints);
            if (mCameraManager != null) {
                CameraManager cameraManager = mCameraManager;
                ResultPoint[] p             = detectorResult.getPoints();
                //计算扫描框中的二维码的宽度，两点间距离公式
                float point1X   = p[0].getX();
                float point1Y   = p[0].getY();
                float point2X   = p[1].getX();
                float point2Y   = p[1].getY();
                int   len       = (int) Math.sqrt(Math.abs(point1X - point2X) * Math.abs(point1X - point2X) + Math.abs(point1Y - point2Y) * Math.abs(point1Y - point2Y));
                Rect  frameRect = cameraManager.getFramingRect();
                if (frameRect != null) {
                    int               frameWidth = frameRect.right - frameRect.left;
                    Camera            camera     = cameraManager.getCamera().getCamera();
                    Camera.Parameters parameters = camera.getParameters();
                    int               maxZoom    = parameters.getMaxZoom();
                    int               zoom       = parameters.getZoom();
                    if (parameters.isZoomSupported()) {
                        if (len <= frameWidth / 4) {
                            //二维码在扫描框中的宽度小于扫描框的1/4，放大镜头
                            if (zoom == 0) {
                                zoom = maxZoom / 3;
                            } else if (zoom <= maxZoom - 5) {
                                zoom = zoom + 5;
                            } else {
                                zoom = maxZoom;
                            }
                            parameters.setZoom(zoom);
                            camera.setParameters(parameters);
                            return null;
                        }
                    }
                }
            }
            //3、对符号码矩阵按照编码规范进行解码，得到实际信息（decoder代码实现的功能）
            decoderResult = decoder.decode(detectorResult.getBits(), hints);
            points = detectorResult.getPoints();
//            System.out.println("检测到二维码");
        }

        // If the code was mirrored: swap the bottom-left and the top-right points.
        if (decoderResult.getOther() instanceof QRCodeDecoderMetaData) {
            ((QRCodeDecoderMetaData) decoderResult.getOther()).applyMirroredCorrection(points);
        }

        Result       result       = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
        List<byte[]> byteSegments = decoderResult.getByteSegments();
        if (byteSegments != null) {
            result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
        }
        String ecLevel = decoderResult.getECLevel();
        if (ecLevel != null) {
            result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
        }
        if (decoderResult.hasStructuredAppend()) {
            result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE,
                    decoderResult.getStructuredAppendSequenceNumber());
            result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_PARITY,
                    decoderResult.getStructuredAppendParity());
        }
        return result;
    }

    @Override
    public void reset() {
        // do nothing
    }

    /**
     * This method detects a code in a "pure" image -- that is, pure monochrome image
     * which contains only an unrotated, unskewed, image of a code, with some white border
     * around it. This is a specialized method that works exceptionally fast in this special
     * case.
     *
     * @see com.google.zxing.datamatrix.DataMatrixReader#extractPureBits(BitMatrix)
     */
    private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {

        int[] leftTopBlack     = image.getTopLeftOnBit();
        int[] rightBottomBlack = image.getBottomRightOnBit();
        if (leftTopBlack == null || rightBottomBlack == null) {
            throw NotFoundException.getNotFoundInstance();
        }

        float moduleSize = moduleSize(leftTopBlack, image);

        int top    = leftTopBlack[1];
        int bottom = rightBottomBlack[1];
        int left   = leftTopBlack[0];
        int right  = rightBottomBlack[0];

        // Sanity check!
        if (left >= right || top >= bottom) {
            throw NotFoundException.getNotFoundInstance();
        }

        if (bottom - top != right - left) {
            // Special case, where bottom-right module wasn't black so we found something else in the last row
            // Assume it's a square, so use height as the width
            right = left + (bottom - top);
            if (right >= image.getWidth()) {
                // Abort if that would not make sense -- off image
                throw NotFoundException.getNotFoundInstance();
            }
        }

        int matrixWidth  = Math.round((right - left + 1) / moduleSize);
        int matrixHeight = Math.round((bottom - top + 1) / moduleSize);
        if (matrixWidth <= 0 || matrixHeight <= 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (matrixHeight != matrixWidth) {
            // Only possibly decode square regions
            throw NotFoundException.getNotFoundInstance();
        }

        // Push in the "border" by half the module width so that we start
        // sampling in the middle of the module. Just in case the image is a
        // little off, this will help recover.
        int nudge = (int) (moduleSize / 2.0f);
        top += nudge;
        left += nudge;

        // But careful that this does not sample off the edge
        // "right" is the farthest-right valid pixel location -- right+1 is not necessarily
        // This is positive by how much the inner x loop below would be too large
        int nudgedTooFarRight = left + (int) ((matrixWidth - 1) * moduleSize) - right;
        if (nudgedTooFarRight > 0) {
            if (nudgedTooFarRight > nudge) {
                // Neither way fits; abort
                throw NotFoundException.getNotFoundInstance();
            }
            left -= nudgedTooFarRight;
        }
        // See logic above
        int nudgedTooFarDown = top + (int) ((matrixHeight - 1) * moduleSize) - bottom;
        if (nudgedTooFarDown > 0) {
            if (nudgedTooFarDown > nudge) {
                // Neither way fits; abort
                throw NotFoundException.getNotFoundInstance();
            }
            top -= nudgedTooFarDown;
        }

        // Now just read off the bits
        BitMatrix bits = new BitMatrix(matrixWidth, matrixHeight);
        for (int y = 0; y < matrixHeight; y++) {
            int iOffset = top + (int) (y * moduleSize);
            for (int x = 0; x < matrixWidth; x++) {
                if (image.get(left + (int) (x * moduleSize), iOffset)) {
                    bits.set(x, y);
                }
            }
        }
        return bits;
    }

    private static float moduleSize(int[] leftTopBlack, BitMatrix image) throws NotFoundException {
        int     height      = image.getHeight();
        int     width       = image.getWidth();
        int     x           = leftTopBlack[0];
        int     y           = leftTopBlack[1];
        boolean inBlack     = true;
        int     transitions = 0;
        while (x < width && y < height) {
            if (inBlack != image.get(x, y)) {
                if (++transitions == 5) {
                    break;
                }
                inBlack = !inBlack;
            }
            x++;
            y++;
        }
        if (x == width || y == height) {
            throw NotFoundException.getNotFoundInstance();
        }
        return (x - leftTopBlack[0]) / 7.0f;
    }

}
