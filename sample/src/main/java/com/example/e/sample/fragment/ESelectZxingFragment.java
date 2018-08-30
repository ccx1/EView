package com.example.e.sample.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccx.ezxing.decode.DecodeResult;
import com.ccx.ezxing.utils.ZXingUtils;
import com.example.e.sample.R;

public class ESelectZxingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DecodeResult decodeResult = ZXingUtils.encodeImage(BitmapFactory.decodeResource(getResources(), R.mipmap.max));
        System.out.println(decodeResult.rawResult);
        System.out.println(decodeResult.handingTime);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
