package com.ccx.esafetextview;

import android.app.Dialog;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class KeyBoard extends Keyboard {

    public KeyBoard(Context context) {
        super(context, R.xml.num_keyboard);
    }

    public KeyBoard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public KeyBoard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public KeyBoard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }
}
