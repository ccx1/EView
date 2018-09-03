package com.example.e.sample.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccx.ezxing.utils.ZXingUtils;
import com.example.e.sample.R;

import static android.content.Context.WINDOW_SERVICE;


public class ECreateEncodeZxingFragment extends Fragment {

    private FragmentActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        // 生成二维码。
        LinearLayout                    VerticalLinearLayout = new LinearLayout(mActivity);
        LinearLayout.MarginLayoutParams source               = new LinearLayout.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams       params               = new LinearLayout.LayoutParams(source);
        VerticalLinearLayout.setOrientation(LinearLayout.VERTICAL);
        params.gravity = Gravity.CENTER;
        VerticalLinearLayout.setLayoutParams(params);

        LinearLayout.LayoutParams layoutParams             = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams linearLayoutlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(mActivity);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(linearLayoutlayoutParams);
        final EditText            editText             = new EditText(mActivity);
        LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        editText.setLayoutParams(editTextLayoutParams);
        linearLayout.addView(editText);

        Button button = new Button(mActivity);
        button.setLayoutParams(layoutParams);
        button.setText("点击生成");
        linearLayout.addView(button);

        VerticalLinearLayout.addView(linearLayout);
        final ImageView imageView = new ImageView(mActivity);
        imageView.setLayoutParams(layoutParams);
        VerticalLinearLayout.addView(imageView);


        WindowManager manager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
        assert manager != null;
        Display display     = manager.getDefaultDisplay();
        Point   displaySize = new Point();
        display.getSize(displaySize);
        int screenWdith      = displaySize.x;
        int screenHeight     = displaySize.y;
        int smallerDimension = screenWdith < screenHeight ? screenWdith : screenHeight;
        smallerDimension = smallerDimension * 7 / 8;
        final int Dimension = smallerDimension;

        button.setOnClickListener(new View.OnClickListener() {

            private Bitmap mBitmap;

            @Override
            public void onClick(View v) {
                String s = editText.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    if (mBitmap != null) {
                        mBitmap.recycle();
                    }
                    mBitmap = ZXingUtils.encodeAsBitmap(s, Dimension);
                    mBitmap = ZXingUtils.addLogo(mBitmap, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    imageView.setImageBitmap(mBitmap);
                    editText.clearFocus();
                } else {
                    Toast.makeText(mActivity, "文本不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return VerticalLinearLayout;
    }
}
