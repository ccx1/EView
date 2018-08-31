package com.example.e.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.e.sample.fragment.ECameraZxingFragment;
import com.example.e.sample.fragment.ECreateEncodeZxingFragment;
import com.example.e.sample.fragment.ESelectZxingFragment;

public class ZXingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);

        String   type     = getIntent().getStringExtra("fragmentType");
        Fragment fragment = null;
        switch (type) {
            case "create":
                fragment = new ECreateEncodeZxingFragment();
                break;
            case "camera":
                getSupportActionBar().hide();
                fragment = new ECameraZxingFragment();
                break;
            case "select":
                fragment = new ESelectZxingFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.zxing_fl, fragment).commit();
    }


    @Override
    protected void onResume() {
        if (ContextCompat.checkSelfPermission(ZXingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ZXingActivity.this,
                    new String[]{Manifest.permission.CAMERA,}, 1);
        }
        super.onResume();
    }
}
