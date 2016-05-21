package com.example.duang;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class TournamentList extends Activity {
	private Context context;	
	private LinearLayout duelup_tournaments;
	private String tournaments;
	
    public static class InternalStorage{
 	   public static void writeObject(Context context, String key, Object object)throws IOException{
 	      FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
 	      ObjectOutputStream oos = new ObjectOutputStream(fos);
 	      oos.writeObject(object);
 	      oos.close();
 	      fos.close();
 	   }
 	   public static Object readObject(Context context, String key)throws IOException, ClassNotFoundException{
  		  FileInputStream fis = context.openFileInput(key);
 	      ObjectInputStream ois = new ObjectInputStream(fis);
 	      Object object = ois.readObject();
 	      return object;
 	   }
    }
	
    private void post_toast(String message){
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
	    setContentView(R.layout.tournament_list);
	    context = getApplicationContext();
	    
	    Asynhttp index_tournament = new Asynhttp(TournamentList.this, global.index_tournament);
	    index_tournament.execute();

	    String result = null;
	    JSONArray tournament_json = null;
		try{
			result = (String)index_tournament.get();
		    if(result != null){
		    	try{
					InternalStorage.writeObject(getApplicationContext(), "TOURNAMENT", result);
				}
		    	catch(IOException e){
					post_toast("Write fail.");
					return;
				}
		    	tournament_json = new JSONArray(result);
		    }
		}
		catch(InterruptedException | ExecutionException | JSONException e1){
			post_toast("Fail to get tournament information.");
			return;
		}
	    /*
	    join_tourny_button = (TextView)findViewById(R.id.join_tourny_button);
	    join_tourny_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TournamentList.this, TournamentPage.class);
				startActivity(intent);
			}
		});
		*/
	    
	    duelup_tournaments = (LinearLayout)findViewById(R.id.duelup_tournaments);
	    for(int i = 0; i < tournament_json.length(); i++){
	    	/*
	    	TournamentTableLayout table = new TournamentTableLayout(context);
	    	TournamentTableLayout.LayoutParams params = new android.widget.TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	params.setMargins(8, 8, 8, 8);
	    	table.setStretchAllColumns(true);
	    	duelup_tournaments.addView(table, params);
	    	*/
	    	final TournamentListItem item;
			try{
				item = new TournamentListItem(context, (JSONObject)tournament_json.get(i), TournamentList.this);
				item.getSignup().setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						if(global.team_json == null){
							new SweetAlertDialog(TournamentList.this, SweetAlertDialog.ERROR_TYPE)
						    .setTitleText("Oops...")
						    .setContentText("You do not have team:(")
						    .show();
							return;
						}
						try{
							String captain_id = global.team_json.getJSONArray("captain").getJSONObject(0).getString("profile_id");
							if(!captain_id.equals(global.user_json.getString("id"))){
								Asynhttp check_part = new Asynhttp(TournamentList.this, global.participants_tournament);
								check_part.execute(item.url);
								JSONArray result = (JSONArray)check_part.get();
								if(result == null){
									new SweetAlertDialog(TournamentList.this, SweetAlertDialog.ERROR_TYPE)
								    .setTitleText("Oops...")
								    .setContentText("Server busy.")
								    .show();
									return;
								}
								for(int i = 0; i < result.length(); i++){
									JSONObject participant = (JSONObject)result.getJSONObject(i).getJSONObject("participant");
									if(participant.getString("name").equals(global.team_json.getString("teamName"))){
										Intent intent = new Intent(TournamentList.this, TournamentPage.class);
										intent.putExtra("NEWS_URL", item.image_url);
										intent.putExtra("TOUR_URL", item.url);
										intent.putExtra("TOUR_NAME", item.name);
										intent.putExtra("PID", participant.getString("id"));
										startActivity(intent);
										return;
									}
								}
								new SweetAlertDialog(TournamentList.this, SweetAlertDialog.ERROR_TYPE)
							    .setTitleText("Oops...")
							    .setContentText("Your captain haven't registered this tournament yet.")
							    .show();
								return;
							}
						}
						catch(JSONException | InterruptedException | ExecutionException e1){
							
						}
			        	//Asynhttp show_tournament = new Asynhttp(NewsActivity.this, global.show_tournament);
			        	//show_tournament.execute();
						//Log.e("signup", "here");
						Asynhttp check_join = new Asynhttp(TournamentList.this, global.check_joined);
				    	try{
							String email = (String)InternalStorage.readObject(context, "EMAIL");
							String p_id = (String)InternalStorage.readObject(context, "PARTI_ID"+item.url+email);
							check_join.execute(item.url, p_id);
							JSONObject result = (JSONObject) check_join.get();
							if(result != null){
								Intent intent = new Intent(TournamentList.this, TournamentPage.class);
								intent.putExtra("NEWS_URL", item.image_url);
								intent.putExtra("TOUR_URL", item.url);
								intent.putExtra("TOUR_NAME", item.name);
								intent.putExtra("PID", p_id);
								startActivity(intent);
								return;
							}
						}
				    	catch(ClassNotFoundException | IOException | InterruptedException | ExecutionException e){
				    		
						}
				    	Asynhttp join_tournament = new Asynhttp(TournamentList.this, global.join_tournament);
				    	try{
							String email = (String)InternalStorage.readObject(context, "EMAIL");
							join_tournament.execute(item.url, global.team_json.getString("teamName"), email);
							JSONObject result = (JSONObject) join_tournament.get();
							if(result != null){
								String p_id = Integer.toString(result.getJSONObject("participant").getInt("id"));
								InternalStorage.writeObject(context, "PARTI_ID"+item.url+email, p_id);
								Intent intent = new Intent(TournamentList.this, TournamentPage.class);
								intent.putExtra("NEWS_URL", item.image_url);
								intent.putExtra("TOUR_URL", item.url);
								intent.putExtra("TOUR_NAME", item.name);
								intent.putExtra("PID", p_id);
								startActivity(intent);
								return;
							}
						}
				    	catch(ClassNotFoundException | IOException | JSONException | InterruptedException | ExecutionException e2){
				    		
						}
					}
				});
		    	//setOnClickListener(item.getSignup(), new Intent(TournamentList.this, TournamentPage.class), "Couldn't sign up :/");
		    	TournamentListItem.LayoutParams params = new TournamentListItem.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		    	params.setMargins(0, 32, 0, 32);
		    	duelup_tournaments.addView(item, params);
		    	
		    	/*
		    	for(int j=0; j<10; j++)
		    	{
		    		TournamentListItem test = new TournamentListItem(context, null, TournamentList.this);
		    		duelup_tournaments.addView(test, params);
		    	}
		    	*/
			}
			catch(JSONException e){
				
			}
	    }
	}
	
	private void setOnClickListener(View v, final Intent intent, final String error_text){
		if(v == null)
			return;
		v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(intent == null)
					Toast.makeText(context, error_text, Toast.LENGTH_LONG).show();
				else
					startActivity(intent);
			}
		});
	}
}
