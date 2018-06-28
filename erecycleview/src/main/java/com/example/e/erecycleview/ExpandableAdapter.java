package com.example.e.erecycleview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * Created by v_chicunxiang on 2018/6/28.
 */

public abstract class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.sampleViewholder> {

    @Override
    public sampleViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_item, parent, false);
        return new sampleViewholder(inflate);
    }

    @Override
    public void onBindViewHolder(sampleViewholder holder, final int position) {
        FrameLayout groupParent = (FrameLayout) holder.itemView.findViewById(R.id.base_fl);
        View        grouItem    = onBindGroupView(holder, position);
        groupParent.removeAllViews();
        groupParent.addView(grouItem);

        FrameLayout itemParent = (FrameLayout) holder.itemView.findViewById(R.id.base_fl1);
        View        childItem  = onBindItemView(holder, position);
        itemParent.removeAllViews();
        itemParent.addView(childItem);
        itemParent.setVisibility(View.GONE);
        groupParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup parent  = (ViewGroup) v.getParent();
                View      childAt = parent.getChildAt(1);
                if (childAt.getVisibility() == View.GONE) {
                    expandableGroup(childAt, position);
                } else {
                    shrinkGroup(childAt, position);
                }

            }
        });

    }

    private void shrinkGroup(final View childAt, int position) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(childAt, "alpha", 1, 0.1f).setDuration(200);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                childAt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    private void expandableGroup(View childAt, int position) {
        childAt.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(childAt, "alpha", 0, 1).setDuration(200).start();
    }

    public abstract View onBindItemView(RecyclerView.ViewHolder holder, int position);

    public abstract View onBindGroupView(RecyclerView.ViewHolder holder, int position);

    @Override
    public abstract int getItemCount();


    static class sampleViewholder extends RecyclerView.ViewHolder {

        sampleViewholder(View itemView) {
            super(itemView);
        }
    }


}
