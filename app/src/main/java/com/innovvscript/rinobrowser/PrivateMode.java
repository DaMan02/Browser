package com.innovvscript.rinobrowser;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class PrivateMode {

    private CookieManager cookieManager;
    private WebView webView;

    public PrivateMode(WebView webView) {
        this.cookieManager = CookieManager.getInstance();
        this.webView = webView;

    }

    public void privateModeSettings() {

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setAppCacheEnabled(false);
        cookieManager.setAcceptCookie(false);
    }

    public void clearAll() {
        webView.clearCache(true);
        webView.clearHistory();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();
    }
}
