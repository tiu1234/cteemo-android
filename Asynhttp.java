package com.example.duang;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Asynhttp extends AsyncTask<String, String, Object>{
	private final int TIMEOUT_MILLISEC = 2000;
	private Context context;
	private boolean net_flag;
	private int command;
	private final String riot_key = "ada8d21b-db43-414e-a8b3-fe7718de6626";
	private final String challonge_key = "OzVqaaqFdjiuTGPbbeQfvpgHxnIcquz6yh5LSwep";
	private final String send_grid_user = "bintao";
	private final String send_grid_api_key = "ck80i539gz";
	//private HttpResponse blah;
	
	public Asynhttp(Context context, int command){
		this.context = context;
		this.command = command;
		net_flag = true;
	}
	
	@Override
	protected void onPreExecute(){
		if(context == null){
			return;
		}
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
	}
	
	@Override
	protected void onProgressUpdate(String...params){
		/*
		CharSequence text = params[0];
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		*/
		if(context != null){
	        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
	        .setTitleText("Oops...")
	        .setContentText(params[0])
	        .show();
		}
	}

	@Override
	protected Object doInBackground(String... params) {
		if(net_flag){
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
			        TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient httpclient = new DefaultHttpClient(httpParams);
			HttpPost httppost;
			HttpGet httpget;
			HttpDelete httpdelete;
			JSONObject json = null;
			JSONObject request = new JSONObject();
			StringEntity request_params;
			//InputStream is;
			//HttpResponse httpresponse;
			
			//HttpResponse http_response;
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = null;
			//HttpResponse blah = null;
			switch(command){
				case global.signup:
					httppost = new HttpPost("http://54.149.235.253:5000/create_user");
					//httppost = new HttpPost("http://floating-retreat-4846.herokuapp.com/create_user");
					try{
						/*
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("email", params[0]));
				        nameValuePairs.add(new BasicNameValuePair("username", params[1]));
				        nameValuePairs.add(new BasicNameValuePair("password", params[2]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        */
						request.put("email", params[0]);
						request.put("password", params[1]);
				        Log.e("Email/PW", params[0] + "/" + params[1]);
						httppost.addHeader("content-type", "application/json");
				        Log.e("request", request.toString());
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
				        Log.e("request_params", request_params.toString());
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
				        response = httpclient.execute(httppost, responseHandler);
				        
						json = new JSONObject(response);
						global.user_json = json;
						String status = json.getString("status");
						if(status.equals("success")){
							//publishProgress(json.getString("token"));
							httpclient.getConnectionManager().shutdown();
							return "success";
						}
						else{
							//publishProgress(json.getString("message"));
						}
					}
					catch(JSONException e){
						publishProgress("Signup JSONException.");
					}
					catch(IllegalStateException e){
						publishProgress("Signup IllegalStateException.");
					}
					catch(ClientProtocolException e){
						//publishProgress("Signup ClientProtocolException.");
						//Log.e("ClientProtocolException", e.getMessage());
					}
					catch(IOException e){
						//publishProgress("Signup IOException.");
						httpclient.getConnectionManager().shutdown();
						Log.e("response", response==null?"null":response);
						if(json == null)
							Log.e("json", "null");
						else
							Log.e("json", json.toString());
						return "success";
					}
					httpclient.getConnectionManager().shutdown();
					return "";
				case global.login:
					httppost = new HttpPost("http://54.149.235.253:5000/login");
					//httppost = new HttpPost("http://floating-retreat-4846.herokuapp.com/login");
					try{
						request.put("email", params[0]);
						request.put("password", params[1]);
						Log.e("Login email/pw", params[0] + "/" + params[1]);
						httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						//response = httpclient.execute(httppost, responseHandler);
				        //InputStream is = httpresponse.getEntity().getContent();
						HttpResponse httpresponse = httpclient.execute(httppost);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						//Log.e("response", receive_msg + " (login after ex)!");
						json = new JSONObject(receive_msg);
						global.user_json = json;
						String token = json.getString("token");
						if(token != null){
							//publishProgress(json.getString("token"));
							httpclient.getConnectionManager().shutdown();
							return token;
						}
						
					}
					catch(JSONException e){
						if(json != null){
							try{
								publishProgress(json.getString("message"));
							}
							catch(JSONException e1){
								publishProgress("Login JSONException.");
							}
						}
					}
					catch(IllegalStateException e){
						publishProgress("Login IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Login IOException.");
						httpclient.getConnectionManager().shutdown();
						Log.e("response", response + " Login IOException");
						return null;
					}
					httpclient.getConnectionManager().shutdown();
					return "";
				case global.fb_login:
					httppost = new HttpPost("http://54.149.235.253:5000/fb_login");
					//httppost = new HttpPost("http://floating-retreat-4846.herokuapp.com/fb_login");
					try{
						/*
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("fbid", params[0]));
				        nameValuePairs.add(new BasicNameValuePair("fbtoken", params[1]));
				        nameValuePairs.add(new BasicNameValuePair("fbemail", params[2]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        */
						request.put("fbtoken", params[1]);
						request.put("fbid", params[0]);
						request.put("fbemail", params[2]);
				        httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        response = httpclient.execute(httppost, responseHandler);
                        Log.e("response", response);
						json = new JSONObject(response);
						global.user_json = json;
						String token = json.getString("token");
						if(token != null){
							//publishProgress(json.getString("token"));
							httpclient.getConnectionManager().shutdown();
							return token;
						}
					}
					catch(JSONException e){
						if(json != null){
							try{
								publishProgress(json.getString("message"));
							}
							catch(JSONException e1){
								publishProgress("FBLogin JSONException.");
							}
						}
					}
					catch(IllegalStateException e){
						publishProgress("FBLogin IllegalStateException.");
					}
					catch(ClientProtocolException e){
						publishProgress("FBLogin ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("FBLogin IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return "";
				case global.forgot_pw:
					httppost = new HttpPost("http://54.149.235.253:5000/forget_password?email="+params[0]);
					//httppost = new HttpPost("http://floating-retreat-4846.herokuapp.com/login");
					//httppost.addHeader("token", params[0]);
					try{
						request.put("email", params[0]);
						request.put("username", params[1]);
						request.put("school", params[2]);
				        httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httppost, responseHandler);
						json = new JSONObject(response);
						global.user_json = json;
						String token = json.getString("token");
						if(token != null){
							//publishProgress(json.getString("token"));
							httpclient.getConnectionManager().shutdown();
							return token;
						}
						/*
						request.put("email", params[1]);
						request.put("username", params[2]);
						request.put("school", params[3]);
				        httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
				        response = httpclient.execute(httppost, responseHandler);
				        */
					}
					
					catch(JSONException e){
						if(json != null){
							try{
								publishProgress(json.getString("message"));
							}
							catch(JSONException e1){
								publishProgress("Forget JSONException.");
							}
						}
					}
					
					catch(IllegalStateException e){
						publishProgress("Forget IllegalStateException.");
					}
					catch(ClientProtocolException e){
						//Log.e("response", blah);
						//publishProgress("Forget ClientProtocolException.");
						publishProgress("Forget ClientProtocolException");
					}
					catch(IOException e){
						publishProgress("Forget IOException.");
					}
					//Log.e("response", blah.getStatusLine() + "!");
					httpclient.getConnectionManager().shutdown();
					return "";
				case global.profile:
					httppost = new HttpPost("http://54.149.235.253:5000/profile");
					//httppost = new HttpPost("http://floating-retreat-4846.herokuapp.com/profile");
					httppost.addHeader("token", params[0]);
					try{
						/*
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("school", params[1]));
				        nameValuePairs.add(new BasicNameValuePair("lolId", params[2]));
				        nameValuePairs.add(new BasicNameValuePair("dotaId", params[3]));
				        nameValuePairs.add(new BasicNameValuePair("hstoneId", params[4]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        */
						request.put("intro", params[1]);
						request.put("username", params[2]);
						request.put("school", params[3]);
						request.put("dotaID", params[4]);
						request.put("lolID", params[5]);
						request.put("hstoneID", params[6]);
				        httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
				        //HttpResponse temp = httpclient.execute(httppost);
						response = httpclient.execute(httppost, responseHandler);
						//InputStream is = response.getEntity().getContent();
						//byte[] buffer = new byte[1024];
						//is.read(buffer);
						//String receive_msg = new String(buffer, "UTF-8");
						json = new JSONObject(response);
						Log.e("profile_set", response);
						httpclient.getConnectionManager().shutdown();
						return json;
					}
					catch(IllegalStateException e){
						publishProgress("Profile IllegalStateException.");
					}
					catch(ClientProtocolException e){
						publishProgress("Profile ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("Profile IOException.");
					}
					catch(JSONException e){
						publishProgress("Profile JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.profile_icon:
					httppost = new HttpPost("http://54.149.235.253:5000/upload_profile_icon");
					//httppost = new HttpPost("http://floating-retreat-4846.herokuapp.com/upload_profile_icon"+params[0]);
					httppost.addHeader("token", params[0]);
					File icon = new File(params[1]);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
    			    int targetW = 300;
    			    int targetH = 300;

    			    // Get the dimensions of the bitmap
    			    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    			    bmOptions.inJustDecodeBounds = true;
    			    BitmapFactory.decodeFile(params[1], bmOptions);
    			    int photoW = bmOptions.outWidth;
    			    int photoH = bmOptions.outHeight;

    			    // Determine how much to scale down the image
    			    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

    			    // Decode the image file into a Bitmap sized to fill the View
    			    bmOptions.inJustDecodeBounds = false;
    			    bmOptions.inSampleSize = scaleFactor;
    			    bmOptions.inPurgeable = true;
					Bitmap bm = BitmapFactory.decodeFile(params[1], bmOptions);
					if(bm.getByteCount() > 20000*1024){
						bm.compress(CompressFormat.JPEG, 1, bos);
					}
					else if(bm.getByteCount() <= 200*1024){
						bm.compress(CompressFormat.JPEG, 20000*1024/bm.getByteCount(), bos);
					}
					else{
						bm.compress(CompressFormat.JPEG, 100, bos);
					}
					ContentBody mimePart = new ByteArrayBody(bos.toByteArray(), icon.getName());
		            HttpEntity reqEntity = MultipartEntityBuilder.create()
		                    .addPart("upload", mimePart)
		                    .build();
		            httppost.setEntity(reqEntity);
					try{
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httppost, responseHandler);
						json = new JSONObject(response);
						//String icon_url = json.getString("profile_icon");
						httpclient.getConnectionManager().shutdown();
						Log.e("response", response);
						return json;
					}
					catch(JSONException e){
						publishProgress("Icon upload JSONException.");
					}
					catch(UnsupportedEncodingException e){
						publishProgress("Icon upload UnsupportedEncodingException.");
					}
					catch(ClientProtocolException e){
						publishProgress("Icon upload ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("Icon upload IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.download_profile_icon:
					try{
				        java.net.URL url = new java.net.URL(params[0]);
				        HttpURLConnection connection = (HttpURLConnection) url
				                .openConnection();
				        connection.setDoInput(true);
				        connection.connect();
				        InputStream input = connection.getInputStream();
				        Bitmap myBitmap = BitmapFactory.decodeStream(input);
						httpclient.getConnectionManager().shutdown();
				        return myBitmap;
				    }
					catch(IOException e){
						publishProgress("Icon download IOException.");
				    }
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.get_profile:
					httpget = new HttpGet("http://54.149.235.253:5000/profile");
					httpget.addHeader("token", params[0]);
					try{
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						json = new JSONObject(response);
						httpclient.getConnectionManager().shutdown();
						return json;
					}
					catch(ClientProtocolException e){
						//publishProgress("Profile get ClientProtocolException.");
					}
					catch(IOException e){
						//publishProgress("Profile get IOException.");
					}
					catch(JSONException e){
						publishProgress("Profile get JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.get_team_profile:
					httpget = new HttpGet("http://54.149.235.253:5000/my_team/lol");
					httpget.addHeader("token", params[0]);
					try{
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						json = new JSONObject(response);
						httpclient.getConnectionManager().shutdown();
						return json;
					}
					catch(ClientProtocolException e){
						//publishProgress("Team profile get ClientProtocolException.");
					}
					catch(IOException e){
						//publishProgress("Team profile get IOException.");
					}
					catch(JSONException e){
						publishProgress("Team profile get JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.get_html:
					httpget = new HttpGet(params[0]);
					try{
 //                     ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						httpclient.getConnectionManager().shutdown();
						return response;
					}
					catch(ClientProtocolException e){
						publishProgress("Html get ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("Html get IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return "";
				case global.get_news_list:
					httpget = new HttpGet(params[0]);
					httpget.addHeader("token", params[1]);
					try{
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						JSONArray jsonarray = new JSONArray(response);
						httpclient.getConnectionManager().shutdown();
						return jsonarray;
					}
					catch(ClientProtocolException e){
						publishProgress("News list get ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("News list get IOException.");
					}
					catch(JSONException e){
						publishProgress("News list get JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.create_team:
					httppost = new HttpPost("http://54.149.235.253:5000/create_team/lol");
					httppost.addHeader("token", params[0]);
					try{
						/*
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("teamName", params[1]));
				        nameValuePairs.add(new BasicNameValuePair("isSchool", params[2]));
				        nameValuePairs.add(new BasicNameValuePair("teamIntro", params[3]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        */
						request.put("teamName", params[1]);
						request.put("isSchool", Boolean.valueOf(params[2]));
						request.put("teamIntro", params[3]);
						if(params[4] != null){
							request.put("school", params[4]);
						}
				        httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						//response = httpclient.execute(httppost, responseHandler);
						HttpResponse httpresponse = httpclient.execute(httppost);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							publishProgress("Team exits!");
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						httpclient.getConnectionManager().shutdown();
						return (String)result.get("status");
					}
					catch(IllegalStateException e){
						publishProgress("Profile IllegalStateException.");
					}
					catch(IOException e){
						publishProgress("Profile IOException.");
					}
					catch(JSONException e){
						publishProgress("Profile JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return "";
				case global.team_icon:
					httppost = new HttpPost("http://54.149.235.253:5000/upload_team_icon/lol");
					httppost.addHeader("token", params[0]);
					File icon1 = new File(params[1]);
					ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
					
					/*
					Uri uri = data.getData();
					Log.d(TAG, uri.toString());
					Toast.makeText(getApplicationContext(), uri.toString(),
							Toast.LENGTH_LONG).show();
					try {
						InputStream stream = getContentResolver().openInputStream(uri);
		
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
		
						BitmapFactory.decodeStream(stream, null, options);
						stream.close();
		
						int w = options.outWidth;
						int h = options.outHeight;
						Log.d(TAG, "Bitmap raw size:" + w + " x " + h);
		
						int displayW = getResources().getDisplayMetrics().widthPixels;
						int displayH = getResources().getDisplayMetrics().heightPixels;
		
						int sample = 1;
		
						while (w > displayW * sample || h > displayH * sample) {
							sample = sample * 2;
						}
						Log.d(TAG, "Sampling at " + sample);
		
						options.inJustDecodeBounds = false;
						options.inSampleSize = sample;
		
						stream = getContentResolver().openInputStream(uri);
						Bitmap bm = BitmapFactory.decodeStream(stream, null, options);
						stream.close();
						if (mBitmap != null) {
							mBitmap.recycle();
						}
						// Make a mutable bitmap...
						mBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
								Bitmap.Config.ARGB_8888);
		
						bm.recycle();
					} catch (Exception e) {
						Log.e(TAG, "Decoding Bitmap", e);
					} 
					 
					*/
					
    			    int targetW1 = 300;
    			    int targetH1 = 300;

    			    // Get the dimensions of the bitmap
    			    BitmapFactory.Options bmOptions1 = new BitmapFactory.Options();
    			    bmOptions1.inJustDecodeBounds = true;
    			    BitmapFactory.decodeFile(params[1], bmOptions1);
    			    int photoW1 = bmOptions1.outWidth;
    			    int photoH1 = bmOptions1.outHeight;
    			    // Determine how much to scale down the image
    			    int scaleFactor1 = Math.min(photoW1/targetW1, photoH1/targetH1);

    			    // Decode the image file into a Bitmap sized to fill the View
    			    bmOptions1.inJustDecodeBounds = false;
    			    bmOptions1.inSampleSize = scaleFactor1;
    			    bmOptions1.inPurgeable = true;
					Bitmap bm1 = BitmapFactory.decodeFile(params[1], bmOptions1);
					
					if(bm1.getByteCount() > 20000*1024){
						bm1.compress(CompressFormat.JPEG, 1, bos1);
					}
					//else if(bm1.getByteCount() <= 200*1024){
					//	bm1.compress(CompressFormat.JPEG, 20000*1024/bm1.getByteCount(), bos1);
					//}
					else{
						bm1.compress(CompressFormat.JPEG, 100, bos1);
					}
					
					ContentBody mimePart1 = new ByteArrayBody(bos1.toByteArray(), icon1.getName());
		            HttpEntity reqEntity1 = MultipartEntityBuilder.create()
		                    .addPart("upload", mimePart1)
		                    .build();
		            httppost.setEntity(reqEntity1);
					try{
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httppost, responseHandler);
						json = new JSONObject(response);
						//String icon_url = json.getString("teamIcon");
						httpclient.getConnectionManager().shutdown();
						return json;
					}
					catch(JSONException e){
						publishProgress("Icon upload JSONException.");
					}
					catch(UnsupportedEncodingException e){
						publishProgress("Icon upload UnsupportedEncodingException.");
					}
					catch(ClientProtocolException e){
						publishProgress("Icon upload ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("Icon upload IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.rename_team:
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.leave_team:
					httpdelete = new HttpDelete("http://54.149.235.253:5000/my_team/lol");
					httpdelete.addHeader("token", params[0]);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						//response = httpclient.execute(httpdelete, responseHandler);
						HttpResponse httpresponse = httpclient.execute(httpdelete);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							publishProgress((String)result.get("message"));
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						httpclient.getConnectionManager().shutdown();
						return (String)result.get("status");
					}
					catch(IOException e){
						httpclient.getConnectionManager().shutdown();
						return "success";
					}
					catch(JSONException e){
						publishProgress("Team leaving JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.dismiss_team:
					httpdelete = new HttpDelete("http://54.149.235.253:5000/create_team/lol");
					httpdelete.addHeader("token", params[0]);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httpdelete);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							publishProgress((String)result.get("message"));
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						httpclient.getConnectionManager().shutdown();
						return (String)result.get("status");
					}
					catch(IOException e){
						httpclient.getConnectionManager().shutdown();
						return "success";
					}
					catch(JSONException e){
						publishProgress("Team dismissing JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.join_team:
					httppost = new HttpPost("http://54.149.235.253:5000/my_team/lol");
					httppost.addHeader("token", params[0]);
					try{
						request.put("teamName", params[1]);
				        httppost.addHeader("content-type", "application/json");
				        request_params = new StringEntity(request.toString());
				        httppost.setEntity(request_params);
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httppost);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							publishProgress((String)result.get("message"));
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(JSONException e){
						publishProgress("Join team JSONException.");
					}
					catch(UnsupportedEncodingException e){
						publishProgress("Join team UnsupportedEncodingException.");
					}
					catch(IOException e){
						publishProgress("Join team IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.search_team:
					/*
					URIBuilder builder = new URIBuilder();
					builder.setScheme("http").setHost("54.149.235.253:5000").setPort(5000).setPath("/search_team/lol")
					.setParameter("teamName", params[1]);
					URI url;
					try{
						url = builder.build();
					}
					catch(URISyntaxException e1){
						publishProgress("Search team URISyntaxException.");
						return null;
					}
					httpget = new HttpGet(url);
					*/
					httpget = new HttpGet("http://54.149.235.253:5000/search_team/lol?teamName="+params[1].replaceAll(" ", "%20")+"&page="+params[2]);
					httpget.addHeader("token", params[0]);
					try{
						//httpget.addHeader("content-type", "application/json");
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        
				        String responses = httpclient.execute(httpget, responseHandler);
						Log.e("team", response+"");
				        //publishProgress(responses);
				        JSONArray jsonarray = new JSONArray(responses);
						httpclient.getConnectionManager().shutdown();
						return jsonarray;
					}
					catch(JSONException e){
						//publishProgress("Search team JSONException.");
					}
					catch(UnsupportedEncodingException e){
						//publishProgress("Search team UnsupportedEncodingException.");
					}
					catch(ClientProtocolException e){
						//publishProgress("Search team ClientProtocolException.");
					}
					catch(IOException e){
						//publishProgress("Search team IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.search_player:
					httpget = new HttpGet("http://54.149.235.253:5000/search_profile?username="+params[1].replaceAll(" ", "%20")+"&page="+params[2]);
					httpget.addHeader("token", params[0]);
					try{
						//httpget.addHeader("content-type", "application/json");
//                      ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        
				        String responses = httpclient.execute(httpget, responseHandler);
				        //publishProgress(responses);
				        JSONArray jsonarray = new JSONArray(responses);
						httpclient.getConnectionManager().shutdown();
						return jsonarray;
					}
					catch(JSONException e){
						publishProgress("Search player JSONException.");
					}
					catch(UnsupportedEncodingException e){
						publishProgress("Search player UnsupportedEncodingException.");
					}
					catch(ClientProtocolException e){
						publishProgress("Search player ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("Search player IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.add_player:
					//publishProgress("Not yet implemented!");
					httppost = new HttpPost("http://54.149.235.253:5000/manage_team/lol");
					httppost.addHeader("token", params[0]);
					try{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("profileID", params[1]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httppost, responseHandler);
				        JSONObject jsonarray = new JSONObject(response);
						httpclient.getConnectionManager().shutdown();
						return (String)jsonarray.get("status");
					}
					catch(IllegalStateException e){
						
					}
					catch(ClientProtocolException e){
						
					}
					catch(IOException e){
						
					}
					catch(JSONException e){
						
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.contact_player:
					//publishProgress("Not yet implemented!");
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.join_tournament:
					httppost = new HttpPost("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+"/participants.json");
					try{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("api_key", challonge_key));
				        nameValuePairs.add(new BasicNameValuePair("participant[name]", params[1]));
				        nameValuePairs.add(new BasicNameValuePair("participant[email]", params[2]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httppost);

				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							publishProgress(receive_msg);
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						Log.e("response", receive_msg);
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(IllegalStateException e){
						publishProgress("Join Tournament IllegalStateException.");
					}
					catch(IOException e){
						publishProgress("Join Tournament IOException.");
					}
					catch(JSONException e){
						publishProgress("Join Tournament JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.show_tournament:
					httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+".json?api_key="+challonge_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						Log.e("show_tournament response", response);
					}
					catch(IllegalStateException e){
						//publishProgress("Show Tournament IllegalStateException.");
					}
					catch(ClientProtocolException e){
						Log.e("response", response + "!");
						//publishProgress("Show Tournament ClientProtocolException.");
					}
					catch(IOException e){
						//publishProgress("Show Tournament IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.index_tournament:
					httpget = new HttpGet("https://api.challonge.com/v1/tournaments.json?api_key="+challonge_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						//publishProgress(response);
						Log.e("response", response);
						httpclient.getConnectionManager().shutdown();
						return response;
					}
					catch(IllegalStateException e){
						//publishProgress("Show Tournament IllegalStateException.");
					}
					catch(ClientProtocolException e){
						Log.e("response", response + "!");
						//publishProgress("Show Tournament ClientProtocolException.");
					}
					catch(IOException e){
						//publishProgress("Show Tournament IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.summoner_id:
					httpget = new HttpGet("https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/"+params[0].replaceAll(" ", "%20")+"?api_key="+riot_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						//Log.e("SummonerID", response);
						json = new JSONObject(response);
						//String icon_url = json.getString("teamIcon");
						httpclient.getConnectionManager().shutdown();
						return json;
					}
					catch(IllegalStateException e){
						publishProgress("SummonerID IllegalStateException.");
					}
					catch(ClientProtocolException e){
						publishProgress("SummonerID does not exist.");
					}
					catch(IOException e){
						publishProgress("SummonerID IOException.");
					}
					catch(JSONException e){
						publishProgress("SummonerID JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.summoner_entry:
					httpget = new HttpGet("https://na.api.pvp.net/api/lol/na/v2.5/league/by-summoner/"+params[0].replaceAll(" ", "%20")+"/entry?api_key="+riot_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						response = httpclient.execute(httpget, responseHandler);
						Log.e("Summoner entry", response);
						json = new JSONObject(response);
						//String icon_url = json.getString("teamIcon");
						httpclient.getConnectionManager().shutdown();
						return json;
					}
					catch(IllegalStateException e){
						publishProgress("Summoner entry IllegalStateException.");
					}
					catch(ClientProtocolException e){
						publishProgress("Summoner entry ClientProtocolException.");
					}
					catch(IOException e){
						publishProgress("Summoner entry IOException.");
					}
					catch(JSONException e){
						publishProgress("Summoner entry JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
					
				case global.search_tournaments:
					httpclient.getConnectionManager().shutdown();
					return null;
					
				case global.view_tournament:
					httpclient.getConnectionManager().shutdown();
					return null;
				
				case global.check_joined:
					httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+"/participants/"+Integer.valueOf(params[1])+".json?api_key="+challonge_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httpget);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						Log.e("check_joined", receive_msg);
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							//publishProgress(receive_msg);
							//Log.e("response", receive_msg);
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						//Log.e("response", receive_msg);
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Check Tournament IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Check Tournament IOException.");
					}
					catch(JSONException e){
						//publishProgress("Check Tournament JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
					
				case global.check_in:
					httppost = new HttpPost("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+"/participants/"+Integer.valueOf(params[1])+"/check_in.json");
					try{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("api_key", challonge_key));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httppost);

				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							//publishProgress(receive_msg);
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						Log.e("response", receive_msg);
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Check In IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Check In IOException.");
					}
					catch(JSONException e){
						//publishProgress("Check In JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
					
				case global.participants_tournament:
					httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+"/participants.json?api_key="+challonge_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httpget);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[102400];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						Log.e("response", receive_msg);
						JSONArray result = new JSONArray(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							//publishProgress(receive_msg);
							//Log.e("response", receive_msg);
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						httpclient.getConnectionManager().shutdown();
						//Log.e("response", receive_msg);
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Tournament Participants IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Tournament Participants IOException.");
					}
					catch(JSONException e){
						//publishProgress("Tournament Participants JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
					
				case global.send_tournamentcode:
					httppost = new HttpPost("https://api.sendgrid.com/api/mail.send.json");
					try{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("api_user", send_grid_user));
				        nameValuePairs.add(new BasicNameValuePair("api_key", send_grid_api_key));
				        nameValuePairs.add(new BasicNameValuePair("to", params[0]));
				        nameValuePairs.add(new BasicNameValuePair("toname", params[1]));
				        nameValuePairs.add(new BasicNameValuePair("subject", "Tournament code for "+params[2]));
				        nameValuePairs.add(new BasicNameValuePair("text", params[3]));
				        nameValuePairs.add(new BasicNameValuePair("from", "support@cteemo.com"));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httppost);

				        //InputStream is = httpresponse.getEntity().getContent();
				        
						//byte[] buffer = new byte[1024];
						//is.read(buffer);
						//String receive_msg = new String(buffer, "UTF-8");
						return httpresponse.getStatusLine().getStatusCode() ;
					}
					catch(IllegalStateException e){
						//publishProgress("Check In IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Check In IOException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
					
				case global.match_tournament:
					httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+"/matches.json?api_key="+challonge_key);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httpget);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[102400];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						Log.e("response", receive_msg);
						JSONArray result = new JSONArray(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							//publishProgress(receive_msg);
							//Log.e("response", receive_msg);
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Tournament Match IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Tournament Match IOException.");
					}
					catch(JSONException e){
						//publishProgress("Tournament Match JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.search_match:
					httpget = new HttpGet("https://api.challonge.com/v1/tournaments/"+params[0].replaceAll(" ", "%20")+"/matches.json?api_key="+challonge_key+"&participant_id="+params[1]);
					try{
//	                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
						HttpResponse httpresponse = httpclient.execute(httpget);
				        InputStream is = httpresponse.getEntity().getContent();
				        
						byte[] buffer = new byte[2048];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						Log.e("response", receive_msg);
						JSONArray result = new JSONArray(receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							//publishProgress(receive_msg);
							//Log.e("response", receive_msg);
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Tournament Match IllegalStateException.");
					}
					catch(IOException e){
						//publishProgress("Tournament Match IOException.");
					}
					catch(JSONException e){
						//publishProgress("Tournament Match JSONException.");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.current_game:
					try{
						httpget = new HttpGet("https://na.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/NA1/"+global.user_json.getString("hstoneID")+"?api_key="+riot_key);
						HttpResponse httpresponse = httpclient.execute(httpget);
						if(httpresponse.getStatusLine().getStatusCode() == 429){
							JSONObject result = new JSONObject();
							httpclient.getConnectionManager().shutdown();
							return result;
						}
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							
							httpclient.getConnectionManager().shutdown();
							return null;
						}
				        InputStream is = httpresponse.getEntity().getContent();
						byte[] buffer = new byte[2048];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						JSONObject result = new JSONObject(receive_msg);
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Current Game IllegalStateException");
					}
					catch(IOException e){
						//publishProgress("Current Game IOException");
					}
					catch(JSONException e){
						//publishProgress("Current Game JSONException");
					}
					JSONObject result2 = new JSONObject();
					httpclient.getConnectionManager().shutdown();
					return result2;
				case global.view_team:
					httpget = new HttpGet("http://54.149.235.253:5000/view_team/lol/"+params[0]);
					try{
						String responses = httpclient.execute(httpget, responseHandler);
						JSONObject result = new JSONObject(responses);
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(ClientProtocolException e){
						
					}
					catch(IOException e){
						
					}
					catch(JSONException e){
						
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.view_profile:
					httpget = new HttpGet("http://54.149.235.253:5000/view_profile/"+params[0]);
					try{
						String responses = httpclient.execute(httpget, responseHandler);
						JSONObject result = new JSONObject(responses);
						httpclient.getConnectionManager().shutdown();
						return result;
					}
					catch(ClientProtocolException e){
						
					}
					catch(IOException e){
						
					}
					catch(JSONException e){
						
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.recent_games:
					try{
						httpget = new HttpGet("https://na.api.pvp.net/api/lol/na/v1.3/game/by-summoner/"+global.user_json.getString("hstoneID")+"/recent?api_key="+riot_key);
						HttpResponse httpresponse = httpclient.execute(httpget);
						if(httpresponse.getStatusLine().getStatusCode() == 429){
							JSONObject result = new JSONObject();
							httpclient.getConnectionManager().shutdown();
							return result;
						}
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						HttpEntity resEntityGet = httpresponse.getEntity(); 

					    String receive_msg = EntityUtils.toString(resEntityGet);
						Log.e("recent", receive_msg);
						JSONObject result = new JSONObject(receive_msg);
						return result;
					}
					catch(IllegalStateException e){
						//publishProgress("Current Game IllegalStateException");
					}
					catch(IOException e){
						//publishProgress("Current Game IOException");
					}
					catch(JSONException e){
						//publishProgress("Current Game JSONException");
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.match_result:
					try{
						URI uri = new URI("https://api.challonge.com/v1/tournaments/"+params[0]+"/matches/"+params[1]+".json?api_key="+challonge_key);
						HttpPut put= new HttpPut(uri);
						List<NameValuePair> pairs = new ArrayList<NameValuePair>();
						pairs.add(new BasicNameValuePair("match[scores_csv]", params[3]));
						pairs.add(new BasicNameValuePair("match[winner_id]", params[2]));
						
						put.setEntity(new UrlEncodedFormEntity(pairs));
						HttpResponse httpresponse = httpclient.execute(put);
						HttpEntity resEntityGet = httpresponse.getEntity(); 
					    String receive_msg = EntityUtils.toString(resEntityGet);
						Log.e("match_result", receive_msg);
						if(httpresponse.getStatusLine().getStatusCode() != 200){
							httpclient.getConnectionManager().shutdown();
							return null;
						}
						JSONObject result = new JSONObject(receive_msg);
						return result;
					}
					catch(URISyntaxException | IOException | JSONException e){
						
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				case global.match_attachment:
					httppost = new HttpPost("https://api.challonge.com/v1/tournaments/"+params[0]+"/matches/"+params[1]+"/attachments.json?api_key="+challonge_key);
					try{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        nameValuePairs.add(new BasicNameValuePair("match_attachment[description]", params[2]));
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse httpresponse = httpclient.execute(httppost);
				        InputStream is = httpresponse.getEntity().getContent();
						byte[] buffer = new byte[1024];
						is.read(buffer);
						String receive_msg = new String(buffer, "UTF-8");
						Log.e("match_attachment", receive_msg);
						JSONObject result = new JSONObject(receive_msg);
						return result;
					}
					catch(IllegalStateException e){
						
					}
					catch(IOException e){
						
					}
					catch(JSONException e){
						
					}
					httpclient.getConnectionManager().shutdown();
					return null;
				default:
					break;
			}
		}
		return null;
	}
}
