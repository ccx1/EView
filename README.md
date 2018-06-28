# ERecycleView

新增了一个可扩展的adapter，直接需要使用扩展的recyclerview的，直接adapter继承ExpandableAdapter即可使用

--------------------------

一个下拉刷新和加载更多，避免了加载第三方的时候项目过于臃肿

只用依赖上erecycleview即可进行使用

详细布局


    <com.example.e.erecycleview.ERecycleView
        android:id="@+id/erv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>



其他方面跟普通的recycleview一样，也可以当作普通的使用

     mRecycleView = (ERecycleView) findViewById(R.id.erv);
     mRecycleView.setLayoutManager(new LinearLayoutManager(this));
     for (int i = 0; i < 100; i++) {
         list.add("aaaaa" + i);
     }
     mMainAdapter = new MainAdapter(list);
     mRecycleView.setAdapter(mMainAdapter);


其中做了两个监听一个是下拉刷新和加载更多的逻辑。另一个是进行刷新状态的时候，需要做什么动作的监听


分别是：

    public void addStateChangeListener(stateChangeListener stateChangeListener)

    public void setLoadingListener(LoadingListener listener)


有四种状态，

     /**
      * @param currState RELEASE_REFRESH刷新控件已经完全暴露，直接刷新。
      *                  PULL_DOWN_REFRESH有一部分暴露，也执行刷新，REFRESHING正在刷新，
      *                  FREE_STATE空闲状态
      */

如果需要头视图，也就是下拉刷新的视图的话，完全支持自定义，可以将一切设置完毕进行view的填入


      TextView textView = new TextView(this);
      textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      textView.setTextSize(18);
      textView.setText("我是头部局");
      mRecycleView.setHeaderView(textView);

反之脚布局也是如此，


另外增加了一个头布局的停止动作，将一切规整

     mRecycleView.stopRefresh(true);

### 注意点

1. 脚布局和头布局，必须在adapter设置完毕后进行填入

### 日常修复

1. 修复了一开始进来脚布局获取不到的bug
