package easywebview.example.com.ewebview;

import android.os.Build;
import android.webkit.WebView;

/**
 * Created by v_chicunxiang on 2018/3/8.
 *
 * @史上最帅无敌创建者 ccx
 * @创建时间 2018/3/8 13:57
 */

public class DefaultWebLifeCycleImpl implements WebLifeCycle {
    private WebView mWebView;

    DefaultWebLifeCycleImpl(WebView webView) {
        this.mWebView = webView;
    }

    @Override
    public void onResume() {
        if (this.mWebView != null) {

            if (Build.VERSION.SDK_INT >= 11){
                this.mWebView.onResume();
            }
            this.mWebView.resumeTimers();
        }


    }

    @Override
    public void onPause() {

        if (this.mWebView != null) {

            if (Build.VERSION.SDK_INT >= 11){
                this.mWebView.onPause();
            }
            this.mWebView.pauseTimers();
        }
    }

    @Override
    public void onDestroy() {

        if(this.mWebView!=null){
            this.mWebView.resumeTimers();
        }
        WebUtils.clearWebView(this.mWebView);

    }
}
