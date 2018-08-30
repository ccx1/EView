package com.example.e.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ZXingSwitchActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_switch);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ZXingSwitchActivity.this, ZXingActivity.class);
        ;
        switch (v.getId()) {
            case R.id.btn1:
                intent.putExtra("fragmentType", "camera");
                break;
            case R.id.btn2:
                intent.putExtra("fragmentType", "create");
                break;
            case R.id.btn3:
                intent.putExtra("fragmentType", "select");
                break;
        }
        startActivity(intent);
    }
}
