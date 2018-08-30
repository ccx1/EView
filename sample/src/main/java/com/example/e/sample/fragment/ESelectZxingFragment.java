package com.example.e.sample.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ccx.ezxing.decode.DecodeResult;
import com.ccx.ezxing.utils.ZXingUtils;
import com.example.e.sample.R;

public class ESelectZxingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DecodeResult decodeResult = ZXingUtils.encodeImage(BitmapFactory.decodeResource(getResources(), R.mipmap.max));

        Toast.makeText(getActivity(), "解析结果： " + decodeResult.rawResult + " ， 处理时间 : " + decodeResult.handingTime, Toast.LENGTH_SHORT).show();

        TextView textView = new TextView(getActivity());
        textView.setText("没有，懒得做选择图片选择了。你看代码把，" +
                "一看就知道没有看readme,图片选择器你可以看我做的Eimageselector" +
                "，github.com/ci250454344/EImageSelector,任性");

        return textView;
    }
}
