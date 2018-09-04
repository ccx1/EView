package com.example.e.sample;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.ccx.evideoview.EVideoView;

public class VideoActivity extends AppCompatActivity {


    private EVideoView mViewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        mViewById = (EVideoView) findViewById(R.id.video);
        mViewById.setVideoPath("http://218.202.220.2:5000/nn_live.ts?id=STARTV");

    }

    @Override
    protected void onDestroy() {
        mViewById.release();
        super.onDestroy();
    }
}
