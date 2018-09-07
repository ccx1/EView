package com.ccx.ezxing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.ccx.ezxing.R;
import com.ccx.ezxing.camera.CameraManager;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY   = 20L;
    private static final int  MAX_RESULT_POINTS = 20;
    private static final int  POINT_SIZE        = 6;

    private       CameraManager     cameraManager;
    private final Paint             paint;
    private       Bitmap            resultBitmap;
    private final int               maskColor;
    private final int               resultColor;
    //    private final int               laserColor;
    private       List<ResultPoint> possibleResultPoints;
    private       boolean           isDraw;
    private int mBottom = -1;
    private int mTop    = -1;
    private Shader mShader;
    private Rect   mRect;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
//        laserColor = resources.getColor(R.color.viewfinder_laser);
        possibleResultPoints = new ArrayList<>(5);

    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {

        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
//        Rect frame        = cameraManager.getFramingRect();
//        System.out.println(frame);
        //(202, 568 - 877, 1243)
//        mRect = new Rect();
//        getFocusedRect(mRect);


        int width  = canvas.getWidth();
        int height = canvas.getHeight();

        // 中心点
        int XCenter = width / 2;
        int YCenter = height / 2;

//        如果要一个方形，那么需要取到两个的方形点
        int tailor = XCenter / 4 * 3;
        // 存放框的高度宽度信息
        Rect frame = new Rect(XCenter - tailor, YCenter - tailor, XCenter + tailor, YCenter + tailor);
//        Rect frame = new Rect();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        paint.setColor(Color.WHITE);

        paint.setStrokeWidth(10f);
        Path path = new Path();
        paint.setStyle(Paint.Style.STROKE);
        int left   = frame.left;
        int top    = frame.top;
        int right  = frame.right;
        int bottom = frame.bottom;
        // 出来是高度
        int RectHeight = (bottom - top) / 3;
        int RectWidth  = (right - left) / 3;

        // 因为是正方形，用哪个都可以

        path.moveTo(left, bottom - RectHeight);
        path.lineTo(left, bottom);
        path.lineTo(left + RectHeight, bottom);


        // left + RectHeight + 150
        path.rMoveTo(RectWidth, 0);

        path.lineTo(right, bottom);
        path.lineTo(right, bottom - RectHeight);


        path.rMoveTo(0, -RectHeight);
        path.lineTo(right, top);
        path.lineTo(right - RectWidth, top);

        // 偏移之后，450，300
        path.rMoveTo(-RectWidth, 0);
        path.lineTo(left, top);
        path.lineTo(left, top + RectHeight);

        canvas.drawPath(path, paint);

        if (mTop > frame.bottom - 100) {
            isDraw = true;
        } else if (mTop < frame.top + 100) {
            isDraw = false;
        }

        if (mTop == -1 || mBottom == -1) {
            mTop = top + 5;
            mBottom = mTop + 5;
        }

        if (isDraw) {
            mTop -= 10;
            mBottom -= 10;
        } else {
            mTop += 10;
            mBottom += 10;
        }

        // 画中间的动画线
        paint.setStyle(Paint.Style.FILL);
        mShader = new LinearGradient(frame.left, mTop, frame.right, mBottom, new int[]{Color.TRANSPARENT, Color.parseColor("#aaffffff"), Color.parseColor("#5526A69A"), Color.parseColor("#aaffffff"), Color.TRANSPARENT}, new float[]{0.1f, 0.2f, 0.5f, 0.8f, 1f}, LinearGradient.TileMode.CLAMP);
        paint.setShader(mShader);
//        paint.setAlpha(100);
        canvas.drawRect(left, mTop, frame.right, mBottom, paint);
        paint.setShader(null);
        postInvalidateDelayed(ANIMATION_DELAY,
                frame.left - POINT_SIZE,
                frame.top - POINT_SIZE,
                frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE);


    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }


}
