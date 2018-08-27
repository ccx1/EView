package easywebview.example.com.ewebview;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.security.Key;

/**
 * Created by v_chicunxiang on 2018/3/8.
 *
 * @史上最帅无敌创建者 ccx
 * @创建时间 2018/3/8 10:31
 */

public class EWebView extends WebView implements View.OnKeyListener {

    private WebLifeCycle mWebLifeCycle;
    private WebSettings mWebSettings;

    public EWebView(Context context) {
        this(context,null);
    }

    public EWebView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public EWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public void init() {
        mWebLifeCycle = new DefaultWebLifeCycleImpl(this);

        initSettings();
        initClient();

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        float webContent = getContentHeight() * getScale();
        float webNow = getHeight() + getScrollY();
        if (Math.abs(webContent - webNow ) < 1){
            //在底部
            if (mOnScrollChangedListener != null) {
                mOnScrollChangedListener.onPageEnd(l,t,oldl,oldt);
            }
        }else if(getScrollY() == 0){
            //在顶部
            if (mOnScrollChangedListener != null) {
                mOnScrollChangedListener.onPageTop(l,t,oldl,oldt);
            }
        }else {
            //在滚动或者中间
            if (mOnScrollChangedListener != null) {
                mOnScrollChangedListener.onScrollChanged(l,t,oldl,oldt);
            }
        }
    }

    public WebLifeCycle getWebLifeCycle() {
        if (mWebLifeCycle == null) {
            mWebLifeCycle = new DefaultWebLifeCycleImpl(this);
        }
        return mWebLifeCycle;
    }

    private void initSettings() {
        mWebSettings = this.getSettings();

        //支持获取手势焦点，输入用户名、密码或其他
        this.requestFocusFromTouch();
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWebSettings.setJavaScriptEnabled(true);  //支持js

        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //webSettings.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
        //webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。
        //若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放。

        mWebSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        mWebSettings.supportMultipleWindows();  //多窗口
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        mWebSettings.setAllowFileAccess(true);  //设置可以访问文件
        mWebSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        mWebSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        this.setOnKeyListener(this);
    }


    public WebSettings getWebSettings() {
        return mWebSettings;
    }

    private void initClient() {
        setWebChromeClient(new EWebChromeClient(getContext().getApplicationContext()){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFileChooserListener != null) {
                    mFileChooserListener.fileChooser(webView, filePathCallback, fileChooserParams);
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);

            }

            @Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                super.openFileChooser(uploadMsg, acceptType, capture);
                if (mFileChooserListener != null) {
                    mFileChooserListener.fileChooser(uploadMsg, acceptType, capture);
                }
            }
        });
        EWebViewClient eWebViewClient = new EWebViewClient(getContext().getApplicationContext());
        eWebViewClient.addJavaScriptMethodAndCallBack("postStr()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {

            }
        });
        setWebViewClient(eWebViewClient);
    }


    /**
     * 将cookie同步到WebView
     * @param url WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     */
    public boolean syncCookie(String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this.getContext());
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        String newCookie = cookieManager.getCookie(url);
        return !TextUtils.isEmpty(newCookie);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (this.canGoBack()) {
                    this.goBack();
                    return true;
                }
                break;
        }
        return false;
    }

    public interface fileChooserListener{
        /**
         * 5.0 以上调用
         */
        void fileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);

        /**
         * 5.0 以下调用
         */
        void fileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture);
    }

    private fileChooserListener mFileChooserListener;


    public void setFileChooserListener(fileChooserListener fileChooserListener) {
        mFileChooserListener = fileChooserListener;
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public void addOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        mOnScrollChangedListener = onScrollChangedListener;
    }

    public interface OnScrollChangedListener{
        void onPageTop(int l, int t, int oldl, int oldt);
        void onPageEnd(int l, int t, int oldl, int oldt);
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
