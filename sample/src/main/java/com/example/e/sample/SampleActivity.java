package com.example.e.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                startActivity(new Intent(SampleActivity.this, MainActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(SampleActivity.this, ExpandableActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(SampleActivity.this, WebActivity.class));
                break;
        }
    }
}
