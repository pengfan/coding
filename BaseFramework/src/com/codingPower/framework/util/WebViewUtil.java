package com.codingPower.framework.util;

import android.view.View;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * 对webView进行初始化
 * @author pengfan
 *
 */
public class WebViewUtil {

    public static void initWebView(WebView webView, ProgressBar progressBar) {
        webView.setHorizontalScrollBarEnabled(true);
        webView.setVerticalScrollBarEnabled(false);

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginsEnabled(true);
        webSettings.setAllowFileAccess(true);

        webView.setClickable(true);
        webView.setLongClickable(true);
        ChromeClient client = new ChromeClient(progressBar);
        webView.setWebChromeClient(client);
    }

    public static class ChromeClient extends WebChromeClient {

        private ProgressBar progressBar;

        public ChromeClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            callback.invoke(origin, true, false);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (progressBar != null) {
                if (progressBar.getVisibility() != View.VISIBLE && newProgress != 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }

            }
        }

    }
}
