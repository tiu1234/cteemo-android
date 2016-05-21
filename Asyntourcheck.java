package com.example.duang;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


public class Asyntourcheck extends AsyncTask<Void, String, Void>{
	private final String riot_key = "ada8d21b-db43-414e-a8b3-fe7718de6626";
	private final String challonge_key = "OzVqaaqFdjiuTGPbbeQfvpgHxnIcquz6yh5LSwep";
	private final String send_grid_user = "bintao";
	private final String send_grid_api_key = "ck80i539gz";
	private final int TIMEOUT_MILLISEC = 2000;
	private Context context;
	private Context this_context;
	private String tour_url;
	private String p_id;
	private String tour_name;
	//private long last_time;
	//private long init_time;
	private boolean net_flag;
	private int gameId;
	private int type;

	
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
	
	Asyntourcheck(Context context, Context this_context, String tour_url, String p_id, String tour_name, int type){
		this.context = context;
		this.this_context = this_context;
		this.tour_url = tour_url;
		this.p_id = p_id;
		this.tour_name = tour_name;
		//last_time = 0;
		net_flag = true;
		this.type = type;
	}
	
	@Override
	protected void onPreExecute(){
		ConnectivityManager check = (ConnectivityManager)this.context.
		getSystemService(Context.CONNECTIVITY_SERVICE);
		if(check != null){
			NetworkInfo[] info = check.getAllNetworkInfo();
			if(info != null){
				for(int i = 0; i <info.length; i++){
					if(info[i].getState() == NetworkInfo.State.CONNECTED){
						return;
					}
				}
				Toast.makeText(context, "Not conencted to internet.", Toast.LENGTH_SHORT).show();
				net_flag = false;
			}
		}
		else{
			Toast.makeText(context, "Not conencted to internet.", Toast.LENGTH_SHORT).show();
			net_flag = false;
		}
		//init_time = System.currentTimeMillis();
		Log.e("other_captain_id", global.other_captain_id);
	}
	
	@Override
	protected void onProgressUpdate(String...params){
		/*
    	if(this_context != null){
	        new SweetAlertDialog(this_context, SweetAlertDialog.ERROR_TYPE)
	        .setTitleText("Oops...")
	        .setContentText(params[0])
	        .show();
    	}
    	*/
    	if(context != null){
			CharSequence text = params[0];
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
	}
	
	@SuppressWarnings("unused")
	private void send_tournament_code(){
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
		        TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost;
		HttpGet httpget;
		JSONObject json = null;
        JSONArray result = null;

		httppost = new HttpPost("https://api.challonge.com/v1/tournaments/"+tour_url.replaceAll(" ", "%20")+"/participants/"+Integer.valueOf(p_id)+"/check_in.json");
		try{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("api_key", challonge_key));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpresponse = httpclient.execute(httppost);
			if(httpresponse.getStatusLine().getStatusCode() != 200){
				httpclient.getConnectionManager().shutdown();
				return;
			}
		}
		catch(IllegalStateException e){
			httpclient.getConnectionManager().shutdown();
			return;
		}
		catch(IOException e){
			httpclient.getConnectionManager().shutdown();
			return;
		}
		httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+tour_url.replaceAll(" ", "%20")+"/matches.json?api_key="+challonge_key+"&participant_id="+p_id);
		try{
			HttpResponse httpresponse = httpclient.execute(httpget);
	        InputStream is = httpresponse.getEntity().getContent();
			byte[] buffer = new byte[2048];
			is.read(buffer);
			String receive_msg = new String(buffer, "UTF-8");
			result = new JSONArray(receive_msg);
			if(httpresponse.getStatusLine().getStatusCode() != 200){
				httpclient.getConnectionManager().shutdown();
				return;
			}
		}
		catch(IllegalStateException e){
			httpclient.getConnectionManager().shutdown();
			return;
		}
		catch(IOException e){
			httpclient.getConnectionManager().shutdown();
			return;
		}
		catch(JSONException e){
			httpclient.getConnectionManager().shutdown();
			return;
		}
        if(result == null){
			httpclient.getConnectionManager().shutdown();
        	return;
        }
        
        String tournament_code = "pvpnet://lol/customgame/joinorcreate/map11/pick6/team5/specALL/";
        String match_id = "";
        for(int i = 0; i < result.length(); i++){
        	try{
				json = result.getJSONObject(i).getJSONObject("match");
				if(Integer.toString(json.getInt("player1_id")).equals(p_id)){
					match_id = Integer.toString(json.getInt("id"));
					global.other_participant_id = json.getInt("player2_id");
					break;
				}
				else if(Integer.toString(json.getInt("player2_id")).equals(p_id)){
					match_id = Integer.toString(json.getInt("id"));
					global.other_participant_id = json.getInt("player1_id");
					break;
				}
			}
        	catch(JSONException e){
				
			}
        }
        if(match_id.equals("")){
			httpclient.getConnectionManager().shutdown();
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
			String email = (String)InternalStorage.readObject(context, "EMAIL");
			httppost = new HttpPost("https://api.sendgrid.com/api/mail.send.json");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("api_user", send_grid_user));
	        nameValuePairs.add(new BasicNameValuePair("api_key", send_grid_api_key));
	        nameValuePairs.add(new BasicNameValuePair("to", email));
	        nameValuePairs.add(new BasicNameValuePair("toname", global.user_json.getString("username")));
	        nameValuePairs.add(new BasicNameValuePair("subject", "Tournament code for "+tour_name));
	        nameValuePairs.add(new BasicNameValuePair("text", tournament_code));
	        nameValuePairs.add(new BasicNameValuePair("from", "support@cteemo.com"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			while(result_code > 399 && result_code <= 599){
				try{
					HttpResponse httpresponse = httpclient.execute(httppost);
					result_code =  httpresponse.getStatusLine().getStatusCode() ;
				}
				catch(IllegalStateException e){
					httpclient.getConnectionManager().shutdown();
					return;
				}
				catch(IOException e){
					httpclient.getConnectionManager().shutdown();
					return;
				}
				httpclient.getConnectionManager().shutdown();
			}
            global.tournament_code_sent = true;
		}
		catch(ClassNotFoundException | IOException | JSONException e) {
			
		}
	}
	
	public void check_current_game(){
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
		        TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpGet httpget;
		JSONObject json = null;
        try{
			httpget = new HttpGet("https://na.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/NA1/"+global.user_json.getString("hstoneID")+"?api_ley="+riot_key);
		}
        catch(JSONException e){
			httpclient.getConnectionManager().shutdown();
			return;
		}
		Log.e("other_captain_id", global.other_captain_id);
		HttpResponse httpresponse;
		try{
			httpresponse = httpclient.execute(httpget);
			int result_code =  httpresponse.getStatusLine().getStatusCode();
			if(result_code != 200){
				if(global.check_status == 0){
					httpclient.getConnectionManager().shutdown();
					return;
				}
				if(global.check_status == 1){
					global.out_loop = true;
					global.start_game = 0;
					global.other_captain_id = "";
					//TODO send result
					httpclient.getConnectionManager().shutdown();
					return;
				}
			}
			InputStream is = httpresponse.getEntity().getContent();
			byte[] buffer = new byte[102400];
			is.read(buffer);
			String receive_msg = new String(buffer, "UTF-8");
			json = new JSONObject(receive_msg);
			if(global.check_status == 1){
				if(gameId != json.getInt("gameId")){
					global.out_loop = true;
					global.start_game = 0;
					global.other_captain_id = "";
				}
				httpclient.getConnectionManager().shutdown();
				return;
			}
			JSONArray participants = json.getJSONArray("participants");
			
			if(global.other_captain_id.equals("")){
				httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+tour_url+"/participants/"+global.other_participant_id+".json?api_key="+challonge_key);
				httpresponse = httpclient.execute(httpget);
				if(result_code != 200){
					httpclient.getConnectionManager().shutdown();
					return;
				}
				is = httpresponse.getEntity().getContent();
				buffer = new byte[2048];
				is.read(buffer);
				receive_msg = new String(buffer, "UTF-8");
				JSONObject other_participant = new JSONObject(receive_msg);
				String other_team_name = other_participant.getString("name");
				int page = 0;
				int out = 0;
				while(out == 0){
					httpget = new HttpGet("http://54.149.235.253:5000/search_team/lol?teamName="+other_team_name.replaceAll(" ", "%20")+"&page="+page);
					httpget.addHeader("token", global.token);
					try{
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
				        String responses = httpclient.execute(httpget, responseHandler);
				        JSONArray jsonarray = new JSONArray(responses);
				        if(jsonarray.length() == 0){
							httpclient.getConnectionManager().shutdown();
							return;
				        }
						for(int i = 0; i < jsonarray.length(); i++){
							JSONObject other_team = jsonarray.getJSONObject(i);
							if(other_team.getString("teamName").equals(other_team_name)){
								String other_team_id = other_team.getString("teamID");
								httpget = new HttpGet("http://54.149.235.253:5000/view_team/lol/"+other_team_id);
								responses = httpclient.execute(httpget, responseHandler);
								JSONObject other_team_full = new JSONObject(responses);
								httpget = new HttpGet("http://54.149.235.253:5000/view_profile/"+other_team_full.getJSONArray("captain").getJSONObject(0).getString("profile_id"));
								responses = httpclient.execute(httpget, responseHandler);
								JSONObject other_captain = new JSONObject(responses);
								global.other_captain_id = other_captain.getString("hstoneID");
								out = 1;
								break;
							}
						}
					}
					catch(JSONException e){
						httpclient.getConnectionManager().shutdown();
						return;
					}
					catch(UnsupportedEncodingException e){
						httpclient.getConnectionManager().shutdown();
						return;
					}
					catch(ClientProtocolException e){
						httpclient.getConnectionManager().shutdown();
						return;
					}
					catch(IOException e){
						httpclient.getConnectionManager().shutdown();
						return;
					}
					page++;
				}
			}
			//other_captain_id = httpresponse;
			Log.e("other_captain_id", global.other_captain_id);
			for(int i = 0; i < participants.length(); i++){
				if(Integer.toString(participants.getJSONObject(i).getInt("summonerId")).equals(global.other_captain_id)){
					global.start_game = 1;
					global.check_status = 1;
					gameId = json.getInt("gameId");
					httpclient.getConnectionManager().shutdown();
					return;
				}
			}
			publishProgress("Your current match is not the right one.");
		}
		catch(IOException | JSONException e){
			
		}
		httpclient.getConnectionManager().shutdown();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		if(!net_flag){
			return null;
		}
		if(type == 0){
			send_tournament_code();
		}
		else{
			check_current_game();
		}
		/*
		while(!global.out_loop){
			long time = System.currentTimeMillis();
			if(time-last_time > 2000 && !global.tournament_code_sent){
				last_time = time;
				send_tournament_code();
			}
			else if(time-last_time > 5*1000 && global.start_game != 1){
				last_time = time;
				check_current_game();
			}
			else if(time-last_time > 20*1000 && global.start_game == 1){
				global.start_game = 2;
				last_time = time;
				check_current_game();
			}
			if(time-init_time > 120*60*1000){
				other_captain_id = "";
				global.out_loop = true;
			}
		}
		*/
		return null;
	}
}
