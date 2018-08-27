### 封装的一个简易WebView

主要是发现Android的webview产生的坑太多了,client需要自己进行封装.这里继续进行了一系列的封装,并且参考了一个比较好的博客.


2018.6.25新增
方法，页面加载完成和页面开始加载方法，以及一个调用js的方法，可以不断的add进行添加
页面加载和开始加载，统一在EWebViewClient中

        @Override
        public void onPageFinished(WebView view, String url) {
           // 页面加载完成
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // 页面开始加载
        }

调用js的方法,这个callback使用范型定义

        EWebViewClient eWebViewClient = new EWebViewClient(getContext().getApplicationContext());
        eWebViewClient.addJavaScriptMethodAndCallBack("postStr()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

            }
        });
        setWebViewClient(eWebViewClient);

主要是WebChromeClient 内部有些坑吧,其中5.0以下的系统版本调用的是 openFileChooser

        // 3.0以下调用
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        // android 3.0以上，android4.0以下：
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, "", "");
        }

        // android 4.0 - android 4.3
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            // 逻辑处理
            Log.i("XWebChromeClient", "openFileChooser");
        }


这里我对逻辑进行了处理,而5.0以上会调用onShowFileChooser方法

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            Log.i("XWebChromeClient", fileChooserParams.getAcceptTypes()[0]);

            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }

这里我做了一次接口的回调,直接通过setFileChooserListener(new EWebView.fileChooserListener() )接口进行参数返回,直接对上述的4个方法进行了回调.
逻辑可以直接写在这里

        @Override
        public void fileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivity(Intent.createChooser(i, "File Chooser"));
        }

        @Override
        public void fileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

        }


而可以根据参数配置获取fileChooserParams.getAcceptTypes()[0] 来获取accept属性的参数

根据activity的生命周期对cpu的释放

        @Override
        protected void onPause() {
            wv.getWebLifeCycle().onPause();
            super.onPause();
        }

        @Override
        protected void onResume() {
            wv.getWebLifeCycle().onResume();
            super.onResume();
        }

        @Override
        protected void onDestroy() {
            wv.getWebLifeCycle().onDestroy();
            super.onDestroy();
        }

内部封装了滚动监听

     this.wv.addOnScrollChangedListener(new EWebView.OnScrollChangedListener() {
         // 当滚动到顶部时
         @Override
         public void onPageTop(int l, int t, int oldl, int oldt) {

         }
         // 当滚动到底部
         @Override
         public void onPageEnd(int l, int t, int oldl, int oldt) {

         }
         // 当在滚动时
         @Override
         public void onScrollChanged(int l, int t, int oldl, int oldt) {

         }
     });

内部已经封装了WebSettings的处理

1. 支持获取手势焦点，输入用户名、密码或其他
2. 支持js
3. 自适应屏幕
4. 支持内容重新布局
5. 多窗口
6. 关闭webview中缓存
7. 设置可以访问文件
8. 当webview调用requestFocus时为webview设置节点
9. 支持通过JS打开新窗口
10. 支持自动加载图片
11. 编码格式utf-8


如果需要对上述以外的内容获取支持.可以选择 getWebSettings() 来获取WebSettings进行重新的配置


[参考的webview](https://github.com/Justson/AgentWeb)