package com.example.e.sample;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import easywebview.example.com.ewebview.EWebView;

public class WebActivity extends AppCompatActivity {

    private EWebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        this.wv = (EWebView) findViewById(R.id.wv);
        wv.loadUrl("https://www.baidu.com");
        wv.setFileChooserListener(new EWebView.fileChooserListener() {
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
        });
        this.wv.addOnScrollChangedListener(new EWebView.OnScrollChangedListener() {
            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

            }
        });

    }


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
}
