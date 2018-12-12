package com.gj.arcoredraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val settings = UI_WebView.settings
        settings.javaScriptEnabled=true
        val url = intent.getStringExtra("url")
        UI_WebView.loadUrl(url)
    }
}
