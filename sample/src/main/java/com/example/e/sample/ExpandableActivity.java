package com.example.e.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpandableActivity extends AppCompatActivity {

    private List<ExpandableBean> mList;
    private ExpandableAdapter mExpandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ExpandableBean mainBean = new ExpandableBean();
            mainBean.groupTitle = "我是标题" + i;
            mainBean.subMsg = "我是 " + i+ "标题下的 ： 我是内容" + i;
            mList.add(mainBean);
        }
        mExpandableAdapter = new ExpandableAdapter(this, mList);
        rv.setAdapter(mExpandableAdapter);
    }


}
