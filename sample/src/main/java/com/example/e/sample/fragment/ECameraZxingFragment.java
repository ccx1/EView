package com.example.e.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccx.ezxing.DecodeType;
import com.ccx.ezxing.listener.ParsingCompleteListener;
import com.ccx.ezxing.view.ScannerView;
import com.example.e.sample.R;


public class ECameraZxingFragment extends Fragment {


    private FragmentActivity mActivity;
    private ScannerView      mScannerView;
    private boolean isOpen = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_camera_zxing, container, false);
        mScannerView = view.findViewById(R.id.findview);
        mScannerView.setOnParsingCompleteListener(new ParsingCompleteListener() {

            @Override
            public void onComplete(String text, String handingTime, DecodeType type) {
                System.out.println(text);
                System.out.println(handingTime);
                Toast.makeText(mActivity, "检测到扫描结果 : " + text + " , 消耗时间 ：" + handingTime + " 秒", Toast.LENGTH_SHORT).show();
                switch (type) {
                    case EMAIL:
                        break;
                    case TEXT:
                        break;
                    case NUMBER:
                        break;
                    case URL:
                        break;
                }
            }
        });
        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen = !isOpen;
                mScannerView.openFlash(isOpen);
            }
        });
        return view;
    }


    @Override
    public void onDestroy() {
        mScannerView.release();
        super.onDestroy();
    }
}
