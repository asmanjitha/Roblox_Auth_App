package com.example.robolex;


import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    String cookie;
    String username;
    Intent intent;
    String profile_pic_url;
    boolean statusPassed;

    String url = "https://www.roblox.com/NewLogin";
//  final String filename= URLUtil.guessFileName(URLUtil.guessUrl(url));

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusPassed = false;

        intent = new Intent(this, APICallPage.class);

        getSupportActionBar().hide();

        webView = findViewById(R.id.web);
        progressBar = findViewById(R.id.progress);
        swipeRefreshLayout = findViewById(R.id.swipe);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new myWebViewClient());
        if (!DetectConnection.checkInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
        } else {
            webView.loadUrl(url);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        webView.loadUrl(url);
                    }
                }, 3000);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)
        );
    }


    public class myWebViewClient extends WebViewClient  {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.cancel();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Toast.makeText(getApplicationContext(), "Not Supporting", Toast.LENGTH_LONG);
            } else {
                webView.evaluateJavascript("(function() { return (document.getElementsByClassName('thumbnail-2d-container avatar-card-image')[0].firstChild.src+' '+document.getElementsByClassName('thumbnail-2d-container avatar-card-image')[0].firstChild.alt) ; })();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        if(!html.equals("null")){
                            String[] lis = html.split(" ");
                            profile_pic_url = lis[0].substring(1, lis[0].length());
                            username = lis[1].substring(0, lis[1].length()-1);
                            if(username != null && profile_pic_url != null && !statusPassed){
                                statusPassed = true;
                                openActivityAPICallPage();
                            }
                        }
                    }
                });
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            String cookies = CookieManager.getInstance().getCookie(url);
            String[] cookies_ = cookies.split(";");
            for (int i = 0; i < cookies_.length; i++) {
                String[] temp = cookies_[i].split("=");
                if (temp[0].trim().equals(".ROBLOSECURITY")) {
                    cookie = cookies_[i].replace(".ROBLOSECURITY=", "").trim();
                }
            }
        }
    }

    private void openActivityAPICallPage() {

        intent.putExtra("username", username);
        intent.putExtra("cookie", cookie);
        intent.putExtra("profile_pic_url", profile_pic_url);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}