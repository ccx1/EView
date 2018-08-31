package com.ccx.ezxing.listener;

import com.ccx.ezxing.DecodeType;

public interface ParsingCompleteListener {

    void onComplete(String text, String handingTime,DecodeType type);
}
