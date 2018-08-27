package easywebview.example.com.ewebview;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by v_chicunxiang on 2018/3/8.
 *
 * @史上最帅无敌创建者 ccx
 * @创建时间 2018/3/8 10:50
 */

public class EWebViewClient extends WebViewClient {


    private Context context;

    private Map<String,ValueCallback> JsTask;

    public EWebViewClient() {
        super();
    }

    public EWebViewClient(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // 页面加载完成
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          /*  // 调用js
            view.evaluateJavascript("javascript:postStr()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                }
            });*/
            for (Map.Entry<String, ValueCallback> stringValueCallbackEntry : JsTask.entrySet()) {
                // 调用js
                view.evaluateJavascript(stringValueCallbackEntry.getKey(), stringValueCallbackEntry.getValue());
            }

        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // 页面开始加载
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url == null)
            return false;

        try {

            if (url.startsWith("phone://")) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + url.substring(8, url.length())));
                context.startActivity(intent);

                return true;

            } else if (url.startsWith("email://")) {
                try {
                    Intent data = new Intent(Intent.ACTION_SENDTO);
                    data.setData(Uri.parse("mailto:" + url.substring(8, url.length())));
                    context.startActivity(data);
                } catch (Exception e) {
                    if (e instanceof ActivityNotFoundException) {
                        Toast.makeText(context,"手机中没有安装邮件app",Toast.LENGTH_SHORT).show();
                    }
                }
                return true;

            } else if (url.startsWith("local://")) {
                /*String substring = url.substring(8, url.length());

                String[] array  = substring.split(",");
                String   type   = array[0];
                String   obj_id = array[1];
                String   count  = array[2];

                Intent intent = new Intent(context, MapDataActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("obj_id", obj_id);

                context.startActivity(intent);*/

                return true;

            } else if (url.startsWith("share://")) {
                String substring = url.substring(8, url.length());

                String[] array  = substring.split(",");
                String   type   = array[0];
                String   obj_id = array[1];

//                postData(type, obj_id);


                return true;

            } else if (url.startsWith("similarity://")) {
                String substring = url.substring(13, url.length());

                String[] array  = substring.split(",");
                String   obj_id = array[0];
                String   type   = array[1];

//                String newURL = Constants.LOCAL_URL + extrainter + "/detail" + "?token=" + token + "&id=" + type + "&user_id=" + user_id;

                // System.out.println("newURL   "+newURL);
//                view.loadUrl(newURL);
//                length.add(newURL);
                return true;

            } else if (url.startsWith("web://")) {
                String substring = url.substring(6, url.length());
                //// TODO: 2017/10/17
                // view.loadUrl(substring);
                Uri    uri    = Uri.parse("http://" + substring);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                //System.out.println("substring "+substring);
                return true;

            } else if (url.startsWith("trip://")) {
                String   substring = url.substring(7, url.length());
                String[] array     = substring.split(",");
                String   type      = array[0];
                String   obj_id    = array[1];
                //                    Intent intent = new Intent(WebViewActivity.this, CalendarActivity.class);
                //                    intent.putExtra("type",type);
                //                    intent.putExtra("obj_id",obj_id);
                //                    startActivity(intent);
//                showPopupMenu(token, user_id, type, obj_id);


                return true;
            } else if (url.startsWith("copy://")) {
                String substring = url.substring(7, url.length());
                ClipboardManager cmb = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(substring);
                Toast.makeText(context,"已复制到剪贴板",Toast.LENGTH_LONG).show();

                return true;
            } else if (url.startsWith("store://")) {
                String   substring = url.substring(8, url.length());
                String[] array     = substring.split(",");
                String   type      = array[0];
                String   obj_id    = array[1];

              /*  Intent intent = new Intent(WebViewActivity.this, MoreStoreActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("obj_id", obj_id);
                intent.putExtra("extrainter", extrainter);

                context.startActivity(intent);*/
                return true;
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                return true;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
            return false;
        }

    }

    public <T> void addJavaScriptMethodAndCallBack(String methodName, ValueCallback<T> valueCallback) {
        if (JsTask == null) {
            JsTask = new HashMap<>();
        }

        JsTask.put(methodName,valueCallback);

    }
}
