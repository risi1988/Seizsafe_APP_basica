package com.example.root.Seizsafe;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.root.myapplication.R;

import java.io.File;

public class Manual extends Activity {

    private String idioma="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView mWebView=new WebView(Manual.this);
        idioma = getIntent().getExtras().getString("idioma");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=http://seizsafe.encore-lab.com/manual"+idioma+".pdf");
        setContentView(mWebView);

    }
}
