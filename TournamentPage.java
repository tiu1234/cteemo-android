package com.example.duang;

import io.rong.imkit.RongIM;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class TournamentPage extends Activity {
	private Context context;
	private Context this_context;
	private ImageView play_next_match;
	private ImageView bracket;
	private TextView check_in;
	private TextView chat_room;
	private String url;
	private String tour_url;
	private String p_id;
	private String tour_name;
	private long last_time;
	private long init_time;
	private final String riot_key = "ada8d21b-db43-414e-a8b3-fe7718de6626";
	private final String challonge_key = "OzVqaaqFdjiuTGPbbeQfvpgHxnIcquz6yh5LSwep";
	private final String send_grid_user = "bintao";
	private final String send_grid_api_key = "ck80i539gz";
	private int gameId;
	private int matchId = -1;
	private int play_flag = 2;
	
    private void post_toast(String message){
    	if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }
    
    public void report_result(){
	    runOnUiThread(new Runnable(){
	        @Override
	        public void run(){
	        	//post_toast("Result!");
	        }
	    });
    	Asynhttp recent_games_task = new Asynhttp(null, global.recent_games);
    	recent_games_task.execute();
    	try{
			JSONObject recent_games = (JSONObject)recent_games_task.get();
			JSONArray games = recent_games.getJSONArray("games");
			for(int i = 0; i < games.length(); i++){
				JSONObject game = games.getJSONObject(i);
				if(game.getInt("gameId") == gameId){
					if(game.getJSONObject("stats").getBoolean("win")){
				    	Asynhttp match_result = new Asynhttp(null, global.match_result);
				    	//Log.e("p_id", p_id);
				    	if(play_flag == 1){
				    		match_result.execute(tour_url, Integer.toString(matchId), p_id, "1-0");
				    	}
				    	if(play_flag == 2){
				    		match_result.execute(tour_url, Integer.toString(matchId), p_id, "0-1");
				    	}
				    	Asynhttp match_attachment = new Asynhttp(null, global.match_attachment);
				    	match_attachment.execute(tour_url, Integer.toString(matchId), Integer.toString(gameId));
			            runOnUiThread(new Runnable(){
			                @Override
			                public void run(){
			                	post_toast("You won the game!");
			                }
			            });
					}
					else{
			            runOnUiThread(new Runnable(){
			                @Override
			                public void run(){
			                	post_toast("You lost the game:(");
			                }
			            });
					}
					global.out_loop = true;
					global.start_game = 0;
					global.check_status = 0;
					global.other_captain_id = "";
					global.tournament_code_sent = false;
				}
			}
		}
    	catch(InterruptedException | ExecutionException | JSONException e){
			
		}
    }
    
	public void check_current_game(){
    	Asynhttp current_game_task = new Asynhttp(null, global.current_game);
    	current_game_task.execute();
    	try{
			JSONObject result = (JSONObject)current_game_task.get();
			if(result == null){
				if(global.check_status == 0){
		            runOnUiThread(new Runnable(){
		                @Override
		                public void run(){
		                	post_toast("Not in a game.");
		                }
		            });
		            return;
				}
				if(global.check_status == 1){
					report_result();
					return;
				}
			}
			if(result.length() == 0){
				last_time -= 5*60*1000;
				return;
			}
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
        			//post_toast(Integer.toString(global.check_status));
                }
            });
			if(global.check_status == 1){
				if(gameId != result.getInt("gameId")){
					report_result();
				}
				return;
			}
			JSONArray participants = result.getJSONArray("participants");
			//Log.e("participants", participants.toString());
			//Log.e("other_captain_id1", ""+global.other_captain_id);
			if(global.other_captain_id.equals("")){
				//Log.e("other_captain_id1", ""+global.other_captain_id);
		    	Asynhttp check_joined = new Asynhttp(null, global.check_joined);
		    	check_joined.execute(tour_url, Integer.toString(global.other_participant_id));
		    	JSONObject other_participant = (JSONObject)check_joined.get();
		    	JSONObject real_other_participant = other_participant.getJSONObject("participant");
		    	String other_team_name = real_other_participant.getString("name");
				//Log.e("other_participant_team_name", other_team_name);
				int page = 0;
				int out = 0;
				//Log.e("other_captain_id1", global.other_captain_id);
				while(out == 0){
			    	Asynhttp search_team = new Asynhttp(null, global.search_team);
			    	search_team.execute(global.token, other_team_name, Integer.toString(page));
			    	JSONArray teams = (JSONArray)search_team.get();
			    	//Log.e("teams", teams.toString());
					for(int i = 0; i < teams.length(); i++){
						JSONObject other_team = teams.getJSONObject(i);
				    	//Log.e("other_team", other_team.toString());
						if(other_team.getString("teamName").equals(other_team_name)){
							String other_team_id = other_team.getString("teamID");
					    	Asynhttp other_team_task = new Asynhttp(null, global.view_team);
					    	other_team_task.execute(other_team_id);
					    	JSONObject other_team_full = (JSONObject)other_team_task.get();
					    	//Log.e("other_team_full", other_team_full.toString());
					    	//Log.e("captain_id", other_team_full.getJSONArray("captain").getJSONObject(0).getString("profile_id"));
					    	Asynhttp other_profile_task = new Asynhttp(null, global.view_profile);
					    	other_profile_task.execute(other_team_full.getJSONArray("captain").getJSONObject(0).getString("profile_id"));
					    	JSONObject other_captain = (JSONObject)other_profile_task.get();
							global.other_captain_id = other_captain.getString("hstoneID");
					    	//Log.e("other_captain_id", global.other_captain_id);
							out = 1;
							break;
						}
					}
					page++;
				}
				//Log.e("other_captain_id2", global.other_captain_id);
			}
			//Log.e("other_captain_id3", global.other_captain_id);
			for(int i = 0; i < participants.length(); i++){
				if(Integer.toString(participants.getJSONObject(i).getInt("summonerId")).equals(global.other_captain_id)){
					global.start_game = 1;
					global.check_status = 1;
					gameId = result.getInt("gameId");
					//Log.e("gameId", Integer.toString(gameId));
					return;
				}
			}
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
        			post_toast("Your current match is not the right one.");
                }
            });
		}
    	catch(InterruptedException | ExecutionException | JSONException e){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
        			post_toast("JSONException.");
                }
            });
		}
	}
	
	private void send_tournament_code(){
    	Asynhttp check_in = new Asynhttp(null, global.check_in);
		check_in.execute(tour_url, p_id);

        String tournament_code = "pvpnet://lol/customgame/joinorcreate/map11/pick6/team5/specALL/";
        String match_id = "";
        Asynhttp matches = new Asynhttp(this_context, global.search_match);
        matches.execute(tour_url, p_id);
        JSONArray result = null;
        try{
			result = (JSONArray)matches.get();
		}
        catch(InterruptedException | ExecutionException e){
			return;
		}
        if(result == null){
        	return;
        }
        for(int i = 0; i < result.length(); i++){
        	try{
				JSONObject json = result.getJSONObject(i).getJSONObject("match");
				if(Integer.toString(json.getInt("player1_id")).equals(p_id)){
					matchId = json.getInt("id");
					play_flag = 1;
					match_id = Integer.toString(matchId);
					global.other_participant_id = json.getInt("player2_id");
					break;
				}
				else if(Integer.toString(json.getInt("player2_id")).equals(p_id)){
					matchId = json.getInt("id");
					play_flag = 2;
					match_id = Integer.toString(matchId);
					global.other_participant_id = json.getInt("player1_id");
					break;
				}
			}
        	catch(JSONException e){
				
			}
        }
        if(match_id.equals("")){
        	return;
        }
        String name = tour_url+match_id;
		String encode = "{\"name\":\""+name+"\",\"extra\":\""+name+"\",\"password\":\"123\",\"report\":\"\"}";
		byte[] input;
		input = encode.getBytes();
		encode = Base64.encodeToString(input, Base64.NO_WRAP);
		tournament_code += encode;
		
		try{
			int result_code = 500;
			while(result_code > 399 && result_code <= 599){
	            Asynhttp tournamentcode = new Asynhttp(this_context, global.send_tournamentcode);
				String email = (String)InternalStorage.readObject(context, "EMAIL");
	            tournamentcode.execute(email, global.user_json.getString("username"), tour_name, tournament_code);
	            result_code = (int)tournamentcode.get();
			}
			runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    new SweetAlertDialog(this_context, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Hurrah!")
                    .setContentText("Tournament code sent:)")
                    .show();
                }
            });
            global.tournament_code_sent = true;
		}
		catch(ClassNotFoundException | IOException | JSONException | InterruptedException | ExecutionException e) {
			
		}
	}
    
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tournament_page);
	    context = getApplicationContext();
	    this_context = TournamentPage.this;

	    Intent intent = getIntent();
	    Bundle bundle = intent.getExtras();
	    url = "";
	    tour_url = "";
	    p_id = "";
	    if(bundle != null){
	    	url = bundle.getString("NEWS_URL");
	    	tour_url = bundle.getString("TOUR_URL");
	    	tour_name = bundle.getString("TOUR_NAME");
	    	p_id = bundle.getString("PID");
	    }
	    
	    play_next_match = (ImageView)findViewById(R.id.play_next_match_button);
	    bracket = (ImageView)findViewById(R.id.bracket_button);
	    check_in = (TextView)findViewById(R.id.check_in);
	    chat_room = (TextView)findViewById(R.id.tournament_chat);
	    
	    play_next_match.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				/*
				Intent intent = new Intent(TournamentPage.this, StartMatch.class);
				startActivity(intent);
				*/
				
	            String tournament_code = "pvpnet://lol/customgame/joinorcreate/map11/pick6/team5/specALL/";
	            String match_id = "";
	            Asynhttp matches = new Asynhttp(this_context, global.search_match);
	            matches.execute(tour_url, p_id);
	            JSONArray result = null;
	            try{
					result = (JSONArray)matches.get();
				}
	            catch(InterruptedException | ExecutionException e){
					return;
				}
	            if(result == null){
	            	return;
	            }
	            for(int i = 0; i < result.length(); i++){
	            	try{
						JSONObject json = result.getJSONObject(i).getJSONObject("match");
						if(Integer.toString(json.getInt("player1_id")).equals(p_id)){
							matchId = json.getInt("id");
							play_flag = 1;
							match_id = Integer.toString(matchId);
							global.other_participant_id = json.getInt("player2_id");
							break;
						}
						else if(Integer.toString(json.getInt("player2_id")).equals(p_id)){
							matchId = json.getInt("id");
							play_flag = 2;
							match_id = Integer.toString(matchId);
							global.other_participant_id = json.getInt("player1_id");
							break;
						}
					}
	            	catch(JSONException e){
						
					}
	            }
	            if(match_id.equals("")){
	            	return;
	            }
	            String name = tour_url+match_id;
	    		String encode = "{\"name\":\""+name+"\",\"extra\":\""+name+"\",\"password\":\"123\",\"report\":\"\"}";
	    		byte[] input;
	    		input = encode.getBytes();
	    		encode = Base64.encodeToString(input, Base64.NO_WRAP);
	    		tournament_code += encode;
	    		Log.e("tournamentcode", tournament_code);
	    		
				try{
					int result_code = 500;
					while(result_code > 399 && result_code <= 599){
			            Asynhttp tournamentcode = new Asynhttp(this_context, global.send_tournamentcode);
						String email = (String)InternalStorage.readObject(context, "EMAIL");
			            tournamentcode.execute(email, global.user_json.getString("username"), tour_name, tournament_code);
			            result_code = (int)tournamentcode.get();
					}
		            new SweetAlertDialog(this_context, SweetAlertDialog.SUCCESS_TYPE)
		            .setTitleText("Hurrah!")
		            .setContentText("Tournament code sent:)")
		            .show();
		            global.tournament_code_sent = true;
				}
				catch(ClassNotFoundException | IOException | JSONException | InterruptedException | ExecutionException e) {
					
				}
			}
		});
	    bracket.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(TournamentPage.this, News.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				//intent.putExtra("EXIT", true);
				intent.putExtra("NEWS_URL", url);
				TournamentPage.this.startActivity(intent);
			}
		});
	    check_in.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				if(global.team_json == null){
			        new SweetAlertDialog(TournamentPage.this, SweetAlertDialog.ERROR_TYPE)
			        .setTitleText("Oops...")
			        .setContentText("You do not have a team:(")
			        .show();
					return;
				}
				try{
					if(!global.team_json.getJSONArray("captain").getJSONObject(0).getString("profile_id").equals(global.user_json.getString("id"))){
					    new SweetAlertDialog(TournamentPage.this, SweetAlertDialog.ERROR_TYPE)
					    .setTitleText("Oops...")
					    .setContentText("Only the captain can check in:(")
					    .show();
						return;
					}
				}
				catch(JSONException e1){
					
				}
		    	Asynhttp check_in = new Asynhttp(null, global.check_in);
				check_in.execute(tour_url, p_id);
				try{
					JSONObject result = (JSONObject)check_in.get();
					if(result == null){
				        new SweetAlertDialog(TournamentPage.this, SweetAlertDialog.ERROR_TYPE)
				        .setTitleText("Oops...")
				        .setContentText("Not the time for Check in.")
				        .show();
					}
					else{
			            new SweetAlertDialog(TournamentPage.this, SweetAlertDialog.SUCCESS_TYPE)
			            .setTitleText("Hurrah!")
			            .setContentText("Check in succeeded.")
			            .show();
					}
				}
				catch(InterruptedException | ExecutionException e){
			        new SweetAlertDialog(TournamentPage.this, SweetAlertDialog.ERROR_TYPE)
			        .setTitleText("Oops...")
			        .setContentText("Not the time for Check in.")
			        .show();
				}
			}
		});
	    chat_room.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				RongIM.getInstance().startChatroom(TournamentPage.this, tour_url, tour_name);
			}
	    });
	    int cap_flag = 0;
	    try{
			if(global.team_json.getJSONArray("captain").getJSONObject(0).getString("profile_id").equals(global.user_json.getString("id"))){
				cap_flag = 1;
			}
		}
	    catch(JSONException e){
			
		}
	    if(cap_flag == 1 && global.out_loop){
	    	new Thread(new Runnable(){
		        @Override
				public void run(){
					global.out_loop = false;
		    		global.start_game = 0;
		    		global.other_captain_id = "";
		        	init_time = System.currentTimeMillis();
		    		while(!global.out_loop){
		    			long time = System.currentTimeMillis();
		    			if(time-last_time > 1*60*1000 && !global.tournament_code_sent){
		    				last_time = time;
		                    runOnUiThread(new Runnable(){
		                        @Override
		                        public void run(){
		                        	//post_toast("here1");
		                        }
		                    });
		                    send_tournament_code();
		    			}
		    			if(matchId != -1 && time-last_time > 9*1000 && global.start_game != 1){
		    				last_time = time;
		                    runOnUiThread(new Runnable(){
		                        @Override
		                        public void run(){
		                        	//post_toast("here2"+global.other_captain_id);
		                        }
		                    });
		                    check_current_game();
		    			}
		    			if(matchId != -1 && time-last_time > 9*1000 && global.start_game == 1){
		    				global.start_game = 2;
		    				last_time = time;
		                    runOnUiThread(new Runnable(){
		                        @Override
		                        public void run(){
		                        	//post_toast("here3"+global.other_captain_id);
		                        }
		                    });
		                    check_current_game();
		    			}
		    			if(time-init_time > 120*60*1000){
		    				global.other_captain_id = "";
		    				global.out_loop = true;
		    			}
		    		}
		        }
		    }).start();
	    }
	}
}
