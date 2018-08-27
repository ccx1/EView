package easywebview.example.com.ewebview;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.File;

/**
 * Created by v_chicunxiang on 2018/3/8.
 *
 * @史上最帅无敌创建者 ccx
 * @创建时间 2018/3/8 13:59
 */

public class WebUtils {


    public static void clearWebView(WebView webView) {
        if (webView == null) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }
        webView.loadUrl("about:blank");
        webView.stopLoading();
        if (webView.getHandler() != null) {
            webView.getHandler().removeCallbacksAndMessages(null);
        }
        webView.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) webView.getParent())) != null) {
            mViewGroup.removeView(webView);
        }
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.setTag(null);
        webView.clearHistory();
        webView.destroy();
        webView = null;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriFromFileForN(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private static Uri getUriFromFileForN(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".ewebview", file);
    }
}
