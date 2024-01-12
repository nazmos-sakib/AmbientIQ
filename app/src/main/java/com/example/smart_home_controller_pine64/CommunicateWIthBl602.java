package com.example.smart_home_controller_pine64;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.smart_home_controller_pine64.Utill.Callback;

import java.net.URL;
import java.util.concurrent.ExecutionException;

public class CommunicateWIthBl602 {
    private static final String TAG = "CommunicateWIthBl602";

    public static String getLedStatus(URL url) throws ExecutionException, InterruptedException {
        Log.d(TAG, "getLedStatus: ----------------------------------------"+url.toString());

        return new NetworkTask().execute(url).get();
    }


    public static void toggleBl602Led(String str_url, String ledName, boolean isOn, Context ctx, Callback callback){

        String  url = str_url + "?led=" + ledName + "&state="+
                (isOn?"1":"0");
        Log.d(TAG, "toggleBl602Led: -------------------"+url);

        WebView webView = new WebView(ctx);
        webView.loadUrl(url);
        webView.setWebViewClient(
            new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    callback.onCallback();
                }
            }
        );

/*

        new ChangeLedTask().doInBackground(()->{
            try {
                URL url = new URL(str_url + "?led=" + ledName + "&state="+
                        (isOn?"1":"0")
                );
                Log.d(TAG, "toggleBl602Led: -------------------"+url.toString());
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000 * 30); // Timeout is in seconds
                urlc.connect();

                callback.onCallback();
*/
/*

                if (urlc.getResponseCode() == 200) {
                    callback.onCallback();
                }
*//*

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
*/


    }

}
