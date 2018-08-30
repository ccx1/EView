package com.example.e.sample;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
                fragment = new ECameraZxingFragment();
                break;
            case "select":
                fragment = new ESelectZxingFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.zxing_fl,fragment).commit();
    }
}
