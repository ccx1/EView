package com.ccx.esafetextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.util.List;

class SafeKeyboardView extends KeyboardView {
    private Context mContext;
    private int     mLabelTextColor;
    private int     mBackgroundColor;
    private int     mTextSize;
    //    private Rect         mClipRegion;
//    private Keyboard.Key mInvalidatedKey;
    private Paint   mPaint;
    private int     mKeyTextColor;
    private @SuppressLint("DrawAllocation")
    Rect mRect;
    //    private float mX = -1;
//    private float mY = -1;

    public SafeKeyboardView(Context context) {
        super(context, null);
        this.initView(context);
    }


    public SafeKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        mLabelTextColor = Color.BLACK;
        mBackgroundColor = Color.parseColor("#E3E3E3");
        mKeyTextColor = Color.WHITE;
        mTextSize = sp2px(14);
        mPaint = new Paint();
//        mClipRegion = (Rect) getField("mClipRegion");
//        mInvalidatedKey = (Keyboard.Key) getField("mInvalidatedKey");
        setBackgroundColor(Color.TRANSPARENT);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (getKeyboard() == null || !(getKeyboard() instanceof KeyBoard)) {
            super.onDraw(canvas);
            return;
        }
        mRect = new Rect();
        getFocusedRect(mRect);
        mPaint.setColor(mLabelTextColor);
        mPaint.setStyle(Paint.Style.FILL);
//        super.onDraw(canvas);
        refreshKey(canvas);
    }

    private void refreshKey(Canvas canvas) {
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        // 如果是左边，那么就是1,说明要换行了
        // 宽度计算等于，如果是第一个，0，width，第二个就是，width，width+this.width
        // 需要记录之前的宽度。来保证接下来的宽度
        // 第二行，宽度是一样的计算，高度是，0，height,第三行就是height，height+this.height
        int sumWidth  = 10;
        int sumHeight = 10;
        mPaint.setColor(Color.parseColor("#dddddd"));
        canvas.drawRect(mRect, mPaint);
        mPaint.setTextSize(mTextSize);
        for (int i = 0; i < keys.size(); i++) {
            Keyboard.Key key       = keys.get(i);
            int          edgeFlags = key.edgeFlags;
            if (edgeFlags == 1) {
                sumWidth = 0;
                if (i != 0)
                    sumHeight += keys.get(i - 1).height + 10;
            }
            CharSequence label = key.label;

            mPaint.setColor(mBackgroundColor);
            // 画框
            canvas.drawRect(sumWidth, sumHeight, key.width + sumWidth, key.height + sumHeight, mPaint);

            int count = 0;
            for (int i1 = 1; i1 < 6; i1++) {
                mPaint.setColor(Color.parseColor("#E" + count + "E" + count + "E" + count));
                int j = i1 * 10;
                canvas.drawRect(sumWidth + j, sumHeight + j, key.width + sumWidth - j, key.height - j + sumHeight, mPaint);
                count += 2;
            }

            // 求中心

            if (label != null) {
                mPaint.setShadowLayer(5, 0, 0, 0);
                int centerWidth  = key.width / 2;
                int centerHeight = key.height / 2 + sumHeight + 10;
                // 画字
                mPaint.setColor(Color.parseColor("#1F1F1F"));
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(label.toString(), centerWidth + sumWidth, centerHeight, mPaint);
                mPaint.setShadowLayer(5, 0, 0, 0);
            }

            sumWidth = key.width + sumWidth + 10;
        }


    }


    private Object getField(String name) {
        try {
            Field field = KeyboardView.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int px2sp(float var1) {
        float var2 = getResources().getDisplayMetrics().scaledDensity;
        return (int) (var1 / var2 + 0.5F);
    }

    public  int sp2px(float var1) {
        float var2 = getResources().getDisplayMetrics().scaledDensity;
        return (int) (var1 * var2 + 0.5F);
    }

}
