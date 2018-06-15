package com.example.e.erecycleview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static com.example.e.erecycleview.ERecycleView.currState.*;

/**
 * Created by v_chicunxiang on 2018/6/14.
 */

public class ERecycleView extends RecyclerView {

    private EAdapter            mEAdapter;
    private LoadingListener     mLoadingListener;
    private stateChangeListener     mStateChangeListener;
    private float               rawY;
    private AdapterDataObserver observer;
    private View                headView;
    private int                 mPulldownHeight;
    private currState mCurrState = FREE_STATE;
    private boolean isStop;
    private View    footView;

    public ERecycleView(Context context) {
        this(context, null);
    }

    public ERecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ERecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mEAdapter = new EAdapter(adapter);
        super.setAdapter(mEAdapter);
        observer = new EAdapterDataObserver(mEAdapter);
        adapter.registerAdapterDataObserver(observer);
        observer.onChanged();
    }


    public void setHeaderView(View view) {
        if (mEAdapter == null) {
            throw new RuntimeException("需要先设置adapter");
        }
        mEAdapter.addHeaderView(view);
        this.headView = view;
        initHeaderView(view);
        mEAdapter.notifyDataSetChanged();

    }

    private void initHeaderView(View view) {
        if (view != null) {
            view.measure(0, 0);
            mPulldownHeight = view.getMeasuredHeight();
            view.setPadding(0, -mPulldownHeight, 0, 0);
        }
    }


    public void setFootView(View view) {
        if (mEAdapter == null) {
            throw new RuntimeException("需要先设置adapter");
        }
        mEAdapter.addFootView(view);
        this.footView = view;
        mEAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            LayoutManager layoutManager           = getLayoutManager();
            int           lastVisibleItemPosition = 0;
            int           lastFootViewPosition    = -1;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (footView != null) {
                lastFootViewPosition = layoutManager.getPosition(footView);
            }
            // 判断条件：
            // 1、显示的是不是最后一个
            // 2、刷新是不是处于空闲
            // 3、最后一个是不是脚布局
            if (mEAdapter.getItemCount() - 1 == lastVisibleItemPosition
                    && mCurrState == FREE_STATE
                    && lastFootViewPosition == lastVisibleItemPosition) {
                if (mLoadingListener != null) {
                    mLoadingListener.onLoadMore();
                }
            }
        }
    }

    public void stopRefresh(boolean isStop) {
        this.isStop = isStop;
        if (isStop) {
            headView.setPadding(0, -mPulldownHeight, 0, 0);
            mCurrState = FREE_STATE;
            judgeState(mCurrState);
        }
    }



    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 起始点
                rawY = e.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                //防止ACTION_DOWN事件被抢占，没有执行
                if (rawY == -1) {
                    rawY = e.getY();
                }
                float endY = e.getY();
                float dY = endY - rawY;
                //判断当前是否正在刷新中
                if (mCurrState == REFRESHING) {
                    //如果当前是正在刷新，不执行下拉刷新了，直接break;
                    break;
                }
                // 如果是下拉
                if (dY > 0 && headView != null) {
                    int paddingTop = (int) (dY - mPulldownHeight);
                    // 这边逻辑是RELEASE_REFRESH才会执行刷新
                    if (paddingTop > 0 && mCurrState != RELEASE_REFRESH) {
                        //完全显示下拉刷新控件，进入松开刷新状态
                        mCurrState = RELEASE_REFRESH;
                    } else if (paddingTop < 0 && mCurrState != PULL_DOWN_REFRESH) {
                        mCurrState = PULL_DOWN_REFRESH;
                    }
                    judgeState(mCurrState);
                    headView.setPadding(0, paddingTop, 0, 0);
                }
                break;

            case MotionEvent.ACTION_UP:
                rawY = -1;
                this.isStop = false;
                if (mCurrState == PULL_DOWN_REFRESH && headView != null) {
                    //设置默认隐藏,如果处在半显示，则隐藏
                    mCurrState = FREE_STATE;
                    headView.setPadding(0, -mPulldownHeight, 0, 0);
                } else if (mCurrState == RELEASE_REFRESH && headView != null) {
                    //当前是释放刷新，进入到正在刷新状态，完全显示
                    mCurrState = REFRESHING;
                    judgeState(mCurrState);
                    headView.setPadding(0, 0, 0, 0);
                    //调用用户的回调事件，刷新页面数据
                    if (mLoadingListener != null) {
                        mLoadingListener.onRefresh();
                    }
                }
                break;

        }
        return super.onTouchEvent(e);
    }

    /**
     * @param currState RELEASE_REFRESH刷新控件已经完全暴露，直接刷新。
     *                  PULL_DOWN_REFRESH有一部分暴露，也执行刷新，REFRESHING正在刷新，
     *                  FREE_STATE空闲状态
     */
    private void judgeState(currState currState) {
        // 判断状态进行动画或者文本切换
        if (mStateChangeListener != null) {
            mStateChangeListener.onRefreshStateChange(currState);
        }
    }

    public void addStateChangeListener(stateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }

    public interface stateChangeListener {

        void onRefreshStateChange(currState currState);
    }


    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    enum currState {
        RELEASE_REFRESH,
        PULL_DOWN_REFRESH,
        REFRESHING,
        FREE_STATE
    }

}
