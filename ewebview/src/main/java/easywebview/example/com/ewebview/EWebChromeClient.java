package easywebview.example.com.ewebview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by v_chicunxiang on 2018/3/8.
 *
 * @史上最帅无敌创建者 ccx
 * @创建时间 2018/3/8 10:37
 */

public class EWebChromeClient extends WebChromeClient {

    private Context context;

    public EWebChromeClient() {
        super();
    }

    public EWebChromeClient(Context context) {
        super();
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        Log.i("XWebChromeClient", fileChooserParams.getAcceptTypes()[0]);

        if (mFileChooserListener != null) {
            mFileChooserListener.fileChooser(webView, filePathCallback, fileChooserParams);
        }


        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

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
        if (mFileChooserListener != null) {
            mFileChooserListener.fileChooser(uploadMsg, acceptType, capture);
        }
    }

    // 当弹出alert对话框的时候
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return true;
    }

    // 当弹出Confirm
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return true;
    }

    //当弹出Prompt
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return true;
    }

    //获得网页的加载进度，显示在右上角的TextView控件中
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress < 100) {
            String progress = newProgress + "%";
        }
    }

    //获取Web页中的title用来设置自己界面中的title
    //当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为 找不到该网页,
    //因此建议当触发onReceiveError时，不要使用获取到的title
    @Override
    public void onReceivedTitle(WebView view, String title) {
        //MainActivity.this.setTitle(title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        // 设置Icon
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        // 创建窗口
        return true;
    }

    @Override
    public void onCloseWindow(WebView view) {
        // 关闭窗口
    }


    public interface fileChooserListener{
        void fileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);

        /**
         * 5.0 以下调用
         * @param uploadMsg
         * @param acceptType
         * @param capture
         */
        void fileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture);
    }

    private fileChooserListener mFileChooserListener;


    public void setFileChooserListener(fileChooserListener fileChooserListener) {
        mFileChooserListener = fileChooserListener;
    }
}
