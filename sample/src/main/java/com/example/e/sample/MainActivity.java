package com.example.e.sample;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.e.erecycleview.ERecycleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ERecycleView.LoadingListener {

    private List<String> list = new ArrayList<>();
    private MainAdapter mMainAdapter;
    private ERecycleView mRecycleView;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycleView = (ERecycleView) findViewById(R.id.erv);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        for (int i = 0; i < 100; i++) {
            list.add("aaaaa" + i);
        }
        mMainAdapter = new MainAdapter(list);
        mRecycleView.setAdapter(mMainAdapter);
        mRecycleView.setLoadingListener(this);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(18);
        textView.setText("我是头部局");
        mRecycleView.setHeaderView(textView);

        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView1.setTextSize(18);
        textView1.setText("正在加载");
        mRecycleView.setFootView(textView1);

    }

    @Override
    public void onRefresh() {
        System.out.println("刷新");
        list.clear();
        for (int i = 0; i < 100; i++) {
            list.add("我是刷新的aaaaa" + new Random().nextInt() + i);
        }

        mMainAdapter.notifyDataSetChanged();
        mRecycleView.stopRefresh(true);
    }

    @Override
    public void onLoadMore() {
        System.out.println("加载更多");
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    list.add("我是加载更多的aaaaa" + i);
                }
                SystemClock.sleep(3000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread().getName());
                        mMainAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

    }
}
