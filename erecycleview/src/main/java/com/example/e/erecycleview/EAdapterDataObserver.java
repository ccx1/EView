package com.example.e.erecycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by v_chicunxiang on 2018/6/15.
 */

public class EAdapterDataObserver extends RecyclerView.AdapterDataObserver {


    private final EAdapter mWrapAdapter;
    public EAdapterDataObserver(EAdapter eAdapter) {
        this.mWrapAdapter = eAdapter;
    }

    /**
     * 监听notifyDataSetChanged
     */
    @Override
    public void onChanged() {
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
    }

}
