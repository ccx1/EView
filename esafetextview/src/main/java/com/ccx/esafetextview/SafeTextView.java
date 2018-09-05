package com.ccx.esafetextview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.ContentFrameLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ScrollView;

import java.lang.reflect.Method;


public class SafeTextView extends EditText implements View.OnKeyListener, KeyboardView.OnKeyboardActionListener {

    private final Context          mContext;
    private       long             mCurrentTime;
    private       SafeKeyboardView mKeyboardView;
    private       boolean          hasKeyBoard;
    private       int              mResId;
    private       View             mParentView;
    private       int              mScreenHeight;
    private       ScrollView       mScrollView;
    private       int              mScrollHeight;
    private       int              mVirtualBarHeight;

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
        mScreenHeight = ScreenUtils.getScreenHeight(mContext);
        mVirtualBarHeight = ScreenUtils.getVirtualBarHeight(mContext);
        this.measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));

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
                        hideSystemSoftKeyboard(this);
                    }
                } else if (clickTime > 2000) {
                    System.out.println("长按事件，则需要显示复制窗口");
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 隐藏系统键盘
     *
     * @param editText 本身
     */
    public static void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method          setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getDeclaredMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }

    // 显示键盘
    private void showKeyBoard() {
        ViewGroup rootView = (ViewGroup) this.getRootView();
        rootView.addView(mKeyboardView);
        if (mParentView == null) {
            getParentView();
        }

        // 说明有scrollview
        if (mScrollView != null) {
            ViewGroup.LayoutParams layoutParams = mScrollView.getLayoutParams();
            // 记录高度
            mScrollHeight = layoutParams.height;
            layoutParams.height = mScreenHeight - mKeyboardView.getMeasuredHeight() - this.getMeasuredHeight() - mVirtualBarHeight;
            mScrollView.setLayoutParams(layoutParams);
        }
        mParentView.setPadding(0, -mKeyboardView.getMeasuredHeight(), 0, mKeyboardView.getMeasuredHeight());

        getAnimator(mKeyboardView, "translationY", mScreenHeight, mScreenHeight - mKeyboardView.getMeasuredHeight());
        hasKeyBoard = true;
    }

    private Animator getAnimator(SafeKeyboardView keyboardView, String target, int value1, int value2) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(keyboardView, target, value1, value2);
        animator.start();
        return animator;
    }

    private void getParentView() {
        mParentView = this;
        while (true) {
            ViewParent parent = mParentView.getParent();
            if (parent instanceof ContentFrameLayout) {
                mParentView = (View) parent;
                break;
            } else {
                mParentView = (View) parent;
                if (mParentView instanceof ScrollView) {
                    mScrollView = (ScrollView) parent;
                }
            }
        }
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
        // 寻找真正的view
        if (mParentView == null) {
            getParentView();
        }
        // 说明有scrollview
        if (mScrollView != null) {
            ViewGroup.LayoutParams layoutParams = mScrollView.getLayoutParams();
            layoutParams.height = mScrollHeight;
            mScrollView.setLayoutParams(layoutParams);
        }
        mParentView.setPadding(0, 0, 0, 0);
        Animator animator = getAnimator(mKeyboardView, "translationY", mScreenHeight - mKeyboardView.getHeight(), mScreenHeight);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        System.out.println("onKey" + primaryCode);
        if (primaryCode == getInteger(R.integer.keycode_backspace_keyboard)) {
            CharSequence text = this.getText();
            if (!TextUtils.isEmpty(text)) {
                int selectionEnd = getSelectionEnd();
                int selectionStart = getSelectionStart();
                if (selectionStart != selectionEnd) {
                    this.getEditableText().delete(selectionStart, selectionEnd);
                } else if (selectionEnd != 0) {
                    this.getEditableText().delete(selectionEnd - 1, selectionEnd);
                }
            }
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
        int selectionEnd = this.getSelectionEnd();
        this.getEditableText().insert(selectionEnd, text);
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
