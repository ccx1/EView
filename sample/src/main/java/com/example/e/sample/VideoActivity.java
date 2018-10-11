package com.example.e.sample;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.ccx.evideoview.EVideoView;

import java.net.URI;

public class VideoActivity extends AppCompatActivity {


    private EVideoView mViewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        mViewById = (EVideoView) findViewById(R.id.video);
        mViewById.setVideoPath(Uri.parse("http://xunleib.zuida360.com/1807/复仇者LM3.BD1280高清中英双字版.mp4"));

    }

    @Override
    protected void onDestroy() {
        mViewById.release();
        super.onDestroy();
    }
}
