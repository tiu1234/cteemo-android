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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class SearchTeam extends Activity {
	private static Context context;
	private ListView search_team_list;
	private EditText search_team_name;
	private JSONArray jsonarray;
	private SearchTeamAdapter adapter;
	private Asynhttp search_team;
	
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
	    setContentView(R.layout.search_team);
	    context = getApplicationContext();
	    
	    ImageView join_back = (ImageView)findViewById(R.id.join_back);
	    join_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	    search_team_list = (ListView)findViewById(R.id.search_team_list);
	    search_team_name = (EditText)findViewById(R.id.search_team_name);
	    //search_team_name.setImeActionLabel("Send", KeyEvent.KEYCODE_ENTER);
	    search_team_name.setOnEditorActionListener(new OnEditorActionListener(){
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
	        	if (actionId == EditorInfo.IME_ACTION_SEARCH ||
	                    actionId == EditorInfo.IME_ACTION_DONE){
	        		if(search_team != null && search_team.getStatus() != AsyncTask.Status.PENDING && search_team.getStatus() != AsyncTask.Status.FINISHED){
	        			return false;
	        		}
		        	search_team = new Asynhttp(SearchTeam.this, global.search_team);
		        	search_team.execute(global.token, search_team_name.getText().toString(), Integer.toString(0));
	        	    new Thread(new Runnable(){
	        	        @Override
						public void run(){
				        	jsonarray = null;
				        	try{
				        		jsonarray = (JSONArray)search_team.get();
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
												post_toast("Search team JSONException.");
											}
						        		};
						        		mainHandler.post(myRunnable);
									}
				        		}
				        		Handler mainHandler = new Handler(context.getMainLooper());
				        		Runnable myRunnable = new Runnable(){
									@Override
									public void run(){
					        		    adapter = new SearchTeamAdapter(context, jsons, SearchTeam.this);
					        			search_team_list.setAdapter(adapter);
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
