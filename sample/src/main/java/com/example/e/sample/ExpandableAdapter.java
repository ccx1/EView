package com.example.e.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 *
 * Created by v_chicunxiang on 2018/6/27.
 */

public class ExpandableAdapter extends com.example.e.erecycleview.ExpandableAdapter {

    private Context              context;
    private List<ExpandableBean> list;

    public ExpandableAdapter(Context context, List<ExpandableBean> list) {
        this.list = list;
        this.context = context;
    }


    @Override
    public View onBindGroupView(RecyclerView.ViewHolder holder, int position) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_group_item, null, false);
        ((TextView) inflate.findViewById(R.id.group_text)).setText(list.get(position).groupTitle);
        return inflate;
    }

    @Override
    public View onBindItemView(RecyclerView.ViewHolder holder, int position) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_item, null, false);
        ((TextView) inflate.findViewById(R.id.child_text)).setText(list.get(position).subMsg);
        return  inflate;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }



}
