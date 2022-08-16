package com.b2bapp.onn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadWebviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_close;
    WebView webView;
    public static String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_webview);

        img_close = (ImageView) findViewById(R.id.img_close);
        img_close.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_close:
                startActivity(new Intent(LoadWebviewActivity.this, SchemeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
        }
    }
}