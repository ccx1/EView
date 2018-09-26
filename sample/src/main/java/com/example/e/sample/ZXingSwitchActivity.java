package com.example.e.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
        switch (v.getId()) {
            case R.id.btn1:
                if (ContextCompat.checkSelfPermission(ZXingSwitchActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ZXingSwitchActivity.this,
                            new String[]{Manifest.permission.CAMERA,}, 1);
                    return;
                }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(ZXingSwitchActivity.this, ZXingActivity.class);
            intent.putExtra("fragmentType", "camera");
            startActivity(intent);
        }
    }
}
