package com.example.e.erecycleview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by v_chicunxiang on 2018/6/14.
 */

public class EAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerView.Adapter adapter;
    private static final int HEAD_VIEW    = 10000;
    private static final int CONTENT_VIEW = 30000;
    private static final int FOOT_VIEW    = 20000;
    private View headView;
    private View footView;

    public EAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEAD_VIEW:
                return new simpleViewHolder(headView);
            case CONTENT_VIEW:
                return adapter.onCreateViewHolder(parent, viewType);
            case FOOT_VIEW:
                return new simpleViewHolder(footView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 一共设置3个布局。头布局，内容布局，脚布局
        // 需要做的是头部局被拉出来
        // 脚布局不影响
        switch (getItemViewType(position)) {
            case HEAD_VIEW:

                break;
            case CONTENT_VIEW:
                adapter.onBindViewHolder(holder, getContentPosition(position));
                break;
            case FOOT_VIEW:

                break;
        }

    }

    @Override
    public int getItemCount() {
        return getItemSize();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headView != null) {
            return HEAD_VIEW;
        } else if (getItemCount()-1 == position && footView != null) {
            return FOOT_VIEW;
        } else {
            return CONTENT_VIEW;
        }

    }

    public void addHeaderView(View view) {
        this.headView = view;
    }

    public void addFootView(View view) {
        this.footView = view;
    }

    public int getItemSize() {
        int itemCount = adapter.getItemCount();
        if (headView != null) {
            itemCount += 1;
        }
        if (footView != null) {
            itemCount += 1;
        }
        return itemCount;
    }

    public int getContentPosition(int position) {
        if (headView != null) {
            return position - 1;
        }
        return position;
    }


    public boolean isHeadView(int position) {
        return headView != null && position - 1 == 0;
    }


    public boolean isFootView(int position) {
        return footView != null && position == getItemSize() -1 ;
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        adapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        adapter.unregisterAdapterDataObserver(observer);
    }


    static class simpleViewHolder extends RecyclerView.ViewHolder {

        public simpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
