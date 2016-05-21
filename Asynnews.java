package com.example.duang;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Asynnews extends AsyncTask<Void, String, Void>{
	private final int TIMEOUT_MILLISEC = 2000;
	private final ViewGroup news_container;
	private final ViewGroup newView; 
	private Context context;
	private Context this_context;
	private JSONObject json;
	
	public Asynnews(Context newcontext, JSONObject newjson, Context newthis_context){
		context = newcontext;
		this_context = newthis_context;
		json = newjson;
		news_container = (ViewGroup)NewsActivity.slides[0].news_page.findViewById(R.id.news_container);
		newView = (ViewGroup)LayoutInflater.from(NewsActivity.slides[0].news_page.getContext()).inflate(
	            R.layout.news_shortcut, NewsActivity.slides[0].news_page, false);
	}
	
	@Override
	protected void onProgressUpdate(String...params){
		CharSequence text = params[0];
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	@Override
	protected Void doInBackground(Void... arg0){
		return null;
	}
	
	@Override
	protected void onPostExecute(Void params){
	    news_container.addView(newView, 0);
	}
}
