package com.ccx.esafetextview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SafeTextView extends TextView implements  View.OnKeyListener, KeyboardView.OnKeyboardActionListener {

    private final Context      mContext;
    private       long         mCurrentTime;
    private       SafeKeyboardView mKeyboardView;
    private       boolean      hasKeyBoard;

    public SafeTextView(Context context) {
        this(context, null);
    }

    public SafeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SafeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, android.R.attr.editTextStyle);
        this.mContext = context;
        this.setOnKeyListener(this);
        // 取到父亲，然后取显示这个keyBoard
        mKeyboardView = new SafeKeyboardView(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyboardView.setLayoutParams(layoutParams);
        mKeyboardView.setFocusable(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setFocusableInTouchMode(true);
        KeyBoard keyBoard = new KeyBoard(mContext);
        mKeyboardView.setKeyboard(keyBoard);
        mKeyboardView.measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
        mKeyboardView.setOnKeyboardActionListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                long currentThreadTimeMillis = System.currentTimeMillis();
                long clickTime = currentThreadTimeMillis - mCurrentTime;
                if (clickTime < 100) {
                    if (!hasKeyBoard) {
                        showKeyBoard();
                    }
                } else if (clickTime > 2000) {
                    System.out.println("长按事件，则需要显示复制窗口");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    // 显示键盘
    private void showKeyBoard() {
        ViewGroup rootView = (ViewGroup) this.getRootView();
        rootView.addView(mKeyboardView);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mKeyboardView, "translationY", ScreenUtils.getScreenHeight(mContext), ScreenUtils.getScreenHeight(mContext) -  mKeyboardView.getMeasuredHeight());
        animator.start();
        hasKeyBoard = true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (hasKeyBoard) {
                    hideKeyBoard();
                    return true;
                }
        }
        return false;
    }

    private void hideKeyBoard() {
        final ViewGroup rootView = (ViewGroup) this.getRootView();
        ObjectAnimator  animator = ObjectAnimator.ofFloat(mKeyboardView, "translationY", ScreenUtils.getScreenHeight(mContext) -  mKeyboardView.getHeight(), ScreenUtils.getRealHeight(mContext));
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
                rootView.removeView(mKeyboardView);
                hasKeyBoard = false;
            }
        });
//
    }


    @Override
    public void onPress(int primaryCode) {
        System.out.println("onPress");
    }

    @Override
    public void onRelease(int primaryCode) {
        System.out.println("onRelease");
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        System.out.println("onKey" + primaryCode);
        if (primaryCode ==  getInteger(R.integer.keycode_backspace_keyboard)) {
            CharSequence text = this.getText();
            this.setText(text.subSequence(0, text.length() - 1));
        } else if (primaryCode == getInteger(R.integer.keycode_sure_keyboard)) {
            hideKeyBoard();
        } else if (primaryCode == getInteger(R.integer.keycode_hide_keyboard)) {
            hideKeyBoard();
        }
    }

    private int getInteger(int key) {
        return getResources().getInteger(key);
    }

    @Override
    public void onText(CharSequence text) {
        System.out.println("onText");
        this.append(text);
    }

    @Override
    public void swipeLeft() {
        System.out.println("swipeLeft");
    }

    @Override
    public void swipeRight() {
        System.out.println("swipeRight");
    }

    @Override
    public void swipeDown() {
        System.out.println("swipeDown");
    }

    @Override
    public void swipeUp() {
        System.out.println("swipeUp");
    }
}