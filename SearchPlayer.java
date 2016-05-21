package com.example.duang;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;

public class SearchPlayer extends Activity {
	private static Context context;
	private ListView search_player_list;
	private EditText search_player_name;
	private JSONArray jsonarray;
	private SearchPlayerAdapter adapter;
	private Asynhttp search_player;
	
	
    public static void post_toast(String message){
    	if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search_player);
	    context = getApplicationContext();
	    
	    ImageView backButton = (ImageView)findViewById(R.id.search_player_back_button);
	    backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	    search_player_list = (ListView)findViewById(R.id.search_player_list);
	    search_player_name = (EditText)findViewById(R.id.search_player_name);
	    //search_team_name.setImeActionLabel("Send", KeyEvent.KEYCODE_ENTER);
	    search_player_name.setOnEditorActionListener(new OnEditorActionListener(){
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
	        	if (actionId == EditorInfo.IME_ACTION_SEARCH ||
	                    actionId == EditorInfo.IME_ACTION_DONE){
	        		if(search_player != null && search_player.getStatus() != AsyncTask.Status.PENDING && search_player.getStatus() != AsyncTask.Status.FINISHED){
	        			return false;
	        		}
		        	search_player = new Asynhttp(SearchPlayer.this, global.search_player);
		        	search_player.execute(global.token, search_player_name.getText().toString(), Integer.toString(0));
	        	    new Thread(new Runnable(){
	        	        @Override
						public void run(){
				        	jsonarray = null;
				        	try{
				        		jsonarray = (JSONArray)search_player.get();
							}
				        	catch(InterruptedException | ExecutionException e){
				        		Handler mainHandler = new Handler(context.getMainLooper());
				        		Runnable myRunnable = new Runnable(){
									@Override
									public void run(){
										post_toast("Search failure.");
									}
				        		};
				        		mainHandler.post(myRunnable);
							}
				        	if(jsonarray != null && jsonarray.length() > 0){
				        		int len = jsonarray.length();
				        		Log.e("len", Integer.toString(len));
				        		final JSONObject[] jsons = new JSONObject[len];
				        		for(int i = 0; i < len; i++){
				        			try{
										jsons[i] = jsonarray.getJSONObject(i);
									}
				        			catch(JSONException e){
						        		Handler mainHandler = new Handler(context.getMainLooper());
						        		Runnable myRunnable = new Runnable(){
											@Override
											public void run(){
												post_toast("Search player JSONException.");
											}
						        		};
						        		mainHandler.post(myRunnable);
									}
				        		}
				        		Handler mainHandler = new Handler(context.getMainLooper());
				        		Runnable myRunnable = new Runnable(){
									@Override
									public void run(){
					        		    adapter = new SearchPlayerAdapter(context, jsons, SearchPlayer.this);
					        			search_player_list.setAdapter(adapter);
									}
				        		};
				        		mainHandler.post(myRunnable);
				        	}
			        	}
		        	}).start();
	        	}
		        return false;
	        }
	    });
	}
}
