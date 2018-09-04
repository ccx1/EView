package com.ccx.esafetextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;
import java.util.List;

class SafeKeyboardView extends KeyboardView  {
    private Context      mContext;
    private int          mLabelTextColor;
    private int          mBackgroundColor;
    private int          mTextSize;
    private Rect         mClipRegion;
    private Keyboard.Key mInvalidatedKey;
    private Paint        mPaint;
    private int          mKeyTextColor;
    private float mX = -1;
    private float mY = -1;

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
        mBackgroundColor = Color.parseColor("#557f7f7f");
        mKeyTextColor = Color.WHITE;
        mTextSize = 34;
        mPaint = new Paint();
        mClipRegion = (Rect) getField("mClipRegion");
        mInvalidatedKey = (Keyboard.Key) getField("mInvalidatedKey");
        setBackgroundColor(Color.parseColor("#dddddd"));
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (getKeyboard() == null || !(getKeyboard() instanceof KeyBoard)) {

            super.onDraw(canvas);
            return;
        }
        @SuppressLint("DrawAllocation") Rect rect = new Rect();
        getFocusedRect(rect);
        mPaint.setColor(mLabelTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        refreshKey(canvas);
    }

    private void refreshKey(Canvas canvas) {
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        // 如果是左边，那么就是1,说明要换行了
        // 宽度计算等于，如果是第一个，0，width，第二个就是，width，width+this.width
        // 需要记录之前的宽度。来保证接下来的宽度
        // 第二行，宽度是一样的计算，高度是，0，height,第三行就是height，height+this.height
        int sumWidth  = 1;
        int sumHeight = 1;
        mPaint.setTextSize(mTextSize);
        for (int i = 0; i < keys.size(); i++) {
            Keyboard.Key key       = keys.get(i);
            int          edgeFlags = key.edgeFlags;
            if (edgeFlags == 1) {
                sumWidth = 0;
                if (i != 0)
                    sumHeight += keys.get(i - 1).height + 7;
            }
            CharSequence label = key.label;
            // 说明x点在当中。
            if (mX > sumWidth && mX < key.width + sumWidth
                    && mY > sumHeight && mY < key.height + sumHeight) {
            }
            mPaint.setColor(mBackgroundColor);
            // 画框
            canvas.drawRect(sumWidth, sumHeight, key.width + sumWidth, key.height + sumHeight, mPaint);
            // 求中心
            int centerWidth  = key.width / 2;
            int centerHeight = key.height / 2 + sumHeight;
            // 画字
            mPaint.setColor(Color.WHITE);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText(label.toString(), centerWidth + sumWidth, centerHeight, mPaint);
            sumWidth = key.width + sumWidth + 7;
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mX = event.getX();
                mY = event.getY();
                break;
        }
        return super.onTouchEvent(event);
    }


}
