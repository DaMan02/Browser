package com.innovvscript.rinobrowser.web;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.innovvscript.rinobrowser.MainActivity;

public class MyWebChromeClient extends WebChromeClient {

    private Context context;
    private String TAG = "WebChromeClient";

    public MyWebChromeClient(Context context){
        this.context = context;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

//        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        view.getSettings().setSupportMultipleWindows(true);

        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }


    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        ((MainActivity)context).setProgressbar(newProgress);
        super.onProgressChanged(view, newProgress);
    }
}
