package com.example.duang;

import com.example.duang.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class News extends Activity{
	private WebView webView;
	private Context context;
	
	private void post_toast(String message){
    	if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		context = getApplicationContext();
	    Intent intent = getIntent();
	    Bundle bundle = intent.getExtras();
	    String news_url = null;
	    if(bundle != null){
	    	news_url = bundle.getString("NEWS_URL");
	    }
		webView = (WebView) findViewById(R.id.news);
		webView.getSettings().setJavaScriptEnabled(true);
		if(news_url != null){
			webView.loadUrl(news_url);
		}
		else{
			post_toast("Invalid url.");
		}
	}
}
