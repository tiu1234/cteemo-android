package com.example.duang;

import cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duang.R;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.*;
import com.facebook.widget.LoginButton.ToolTipMode;

public class LogInActivity extends Activity {
    private final int SELECT_PICTURE = 1;
	private String[] school_name = {"UIUC", "Purdue", "Berkeley", "Harvard", "Yale"};
//	private RelativeLayout top_layout;
	//private LinearLayout init_layout;
//	private LinearLayout back_layout;
	private LinearLayout select_layout;
	private LinearLayout signup_layout;
	private LinearLayout forgot_password;
	private RelativeLayout login_layout;
	private LinearLayout profile_layout;
	private TextView signup;
	//private LinearLayout profile_layout2;
	//private Button login_button;
	//private Button signup_button;
	private ImageView left_arrow;
	private ImageView right_arrow;
	private ImageView pick;
	private EditText signup_email;
	//private EditText signup_username;
	private EditText signup_password;
	private EditText login_email;
	private EditText login_password;
	private EditText username;
	private TextView login_button;
	private TextView login_button2;
	private TextView submit_button;
	private EditText lol_id;
	//private EditText dota_id;
	//private EditText hh_stone_id;
	private AutoCompleteTextView school;
	//private TextView login_hint;
	//private ProgressBar spinner;
	

    //private UiLifecycleHelper uiHelper;
	public static LoginButton fb_button;
	//private GraphUser user;
    //private ProfilePictureView profilePictureView;
    private Asynhttp login;
    //private Asyninit init_thread;
    private Context context;
    private String selectedImagePath;
    private static Intent intent;
	//private int out = 0;
    private int last_click_l = 0;
    private int last_click_s = 0;
    
    private String signup_em;
    private String signup_pw;
    private long last_click_time_l = 0;
    private long last_click_time_s = 0;
    
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
    
    private String getPath(Uri uri){
            if(uri == null){
                return null;
            }
            String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if(cursor != null){
                int column_index = cursor.getColumnIndexOrThrow(projection[0]);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            return uri.getPath();
    }

    /*
    private void onSessionStateChange(Session session, SessionState state, Exception exception){
        updateUI();
    }

    private void updateUI(){
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if (enableButtons && user != null) {
            //profilePictureView.setProfileId(user.getId());
        } else {
            //profilePictureView.setProfileId(null);
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback(){
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
     */
    @Override
    protected void onResume(){
        super.onResume();
        //uiHelper.onResume();
        //updateUI();
        /*
        if(init_thread != null){
        	if(init_thread.getStatus() == AsyncTask.Status.PENDING){
        		init_thread.execute();
        	}
        }
        */
    }
    
    @Override
    public void onPause(){
        super.onPause();
        //uiHelper.onPause();
		
		forgot_password = (LinearLayout)findViewById(R.id.forgot_password);
		//init_layout = (LinearLayout)findViewById(R.id.init_layout);
//		back_layout = (LinearLayout)findViewById(R.id.back_layout);
//		top_layout = (RelativeLayout)findViewById(R.id.top_layout);
		select_layout = (LinearLayout)findViewById(R.id.select_layout);
		signup_layout = (LinearLayout)findViewById(R.id.signup_layout);
		login_layout = (RelativeLayout)findViewById(R.id.login_layout);
		profile_layout = (LinearLayout)findViewById(R.id.profile_layout);
		//profile_layout2 = (LinearLayout)findViewById(R.id.profile_layout2);
		//login_button = (Button)findViewById(R.id.login_button);
		//signup_button = (Button)findViewById(R.id.signup_button);
		signup = (TextView)findViewById(R.id.signup);
		left_arrow = (ImageView)findViewById(R.id.left_arrow);
		right_arrow = (ImageView)findViewById(R.id.right_arrow);
		pick = (ImageView)findViewById(R.id.pick);
		signup_email = (EditText)findViewById(R.id.signup_email);
		//signup_username = (EditText)findViewById(R.id.signup_username);
		signup_password = (EditText)findViewById(R.id.signup_password);
		login_email = (EditText)findViewById(R.id.login_email);
		username = (EditText)findViewById(R.id.username);
		login_password = (EditText)findViewById(R.id.login_password);
		lol_id = (EditText)findViewById(R.id.lol_id);
		//dota_id = (EditText)findViewById(R.id.dota_id);
		//hh_stone_id = (EditText)findViewById(R.id.hh_stone_id);
		school = (AutoCompleteTextView)findViewById(R.id.school);
		login_button = (TextView) findViewById(R.id.login);
		login_button2 = (TextView)findViewById(R.id.login2);
		submit_button = (TextView)findViewById(R.id.submit);
		//login_hint = (TextView)findViewById(R.id.login_hint);
		//spinner = (ProgressBar)findViewById(R.id.progressBar);
		//init_layout.setVisibility(View.VISIBLE);
//		back_layout.setVisibility(View.INVISIBLE);
//		top_layout.setVisibility(View.INVISIBLE);
		select_layout.setVisibility(View.INVISIBLE);
		signup_layout.setVisibility(View.INVISIBLE);
		login_layout.setVisibility(View.INVISIBLE);
		profile_layout.setVisibility(View.INVISIBLE);
		left_arrow.setVisibility(View.INVISIBLE);
		right_arrow.setVisibility(View.INVISIBLE);
		//Log.e("MODE", String.valueOf(global.mode));
		//profile_layout2.setVisibility(View.INVISIBLE);
		//spinner.setVisibility(View.GONE);
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, school_name);
		school.setThreshold(1);
		school.setAdapter(adapter);

		//Log.e("mode", "" + global.mode);
		try{
			global.fb_flag = (boolean)InternalStorage.readObject(getApplicationContext(), "FB_FLAG");
		}
		catch(ClassNotFoundException | IOException e2){
			
		}
		if(global.mode == global.init_mode){
			try{
				global.mode = (int)InternalStorage.readObject(getApplicationContext(), "MODE");
				global.token = (String)InternalStorage.readObject(getApplicationContext(), "TOKEN");
				global.icon_url = (String)InternalStorage.readObject(getApplicationContext(), "ICON_URL");
			}
			catch(IOException e){
				global.mode = global.select_mode;
				global.token = "";
				global.fb_flag = false;
				global.icon_url = "";
				try{
					InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
					InternalStorage.writeObject(getApplicationContext(), "FB_FLAG", global.fb_flag);
					InternalStorage.writeObject(getApplicationContext(), "ICON_URL", global.icon_url);
				}
				catch(IOException e1){
					post_toast("Write fail.");
				}
			}
			catch(ClassNotFoundException e){
				post_toast("Write fail.");
			}
			//init_thread = new Asyninit(global.mode, global.token, select_layout, signup_layout, login_layout, init_layout, top_layout, profile_layout1, profile_layout2, right_arrow);
		}
		else{
			//init_layout.setVisibility(View.INVISIBLE);
			switch(global.mode){
				case global.select_mode:
//					back_layout.setVisibility(View.INVISIBLE);
//					top_layout.setVisibility(View.INVISIBLE);
					
					select_layout.setVisibility(View.VISIBLE);
					signup_layout.setVisibility(View.INVISIBLE);
					login_layout.setVisibility(View.INVISIBLE);
					profile_layout.setVisibility(View.INVISIBLE);
					left_arrow.setVisibility(View.INVISIBLE);
					right_arrow.setVisibility(View.INVISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					break;
				case global.signup_mode:
//					back_layout.setVisibility(View.VISIBLE);
//					top_layout.setVisibility(View.VISIBLE);
					select_layout.setVisibility(View.INVISIBLE);
					signup_layout.setVisibility(View.VISIBLE);
					login_layout.setVisibility(View.INVISIBLE);
					profile_layout.setVisibility(View.INVISIBLE);
					left_arrow.setVisibility(View.VISIBLE);
					right_arrow.setVisibility(View.VISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					break;
				case global.login_mode:
//					back_layout.setVisibility(View.VISIBLE);
//					top_layout.setVisibility(View.VISIBLE);
					select_layout.setVisibility(View.INVISIBLE);
					signup_layout.setVisibility(View.INVISIBLE);
					login_layout.setVisibility(View.VISIBLE);
					profile_layout.setVisibility(View.INVISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					left_arrow.setVisibility(View.VISIBLE);
					right_arrow.setVisibility(View.INVISIBLE);
					break;
				case global.profile_mode:
//					back_layout.setVisibility(View.VISIBLE);
//					top_layout.setVisibility(View.VISIBLE);
					select_layout.setVisibility(View.INVISIBLE);
					signup_layout.setVisibility(View.INVISIBLE);
					login_layout.setVisibility(View.INVISIBLE);
					profile_layout.setVisibility(View.VISIBLE);
					if(global.fb_flag){
						pick.setVisibility(View.INVISIBLE);
					}
					//left_arrow.setVisibility(View.VISIBLE);
					//right_arrow.setVisibility(View.VISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					break;
			}
		}
    }

    @Override
    public void onStop(){
        super.onStop();
        //uiHelper.onDestroy();
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //uiHelper.onSaveInstanceState(outState);
    }
    
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //uiHelper.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(global.mode == global.profile_mode && requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                //post_toast(selectedImagePath);
			    //Log.e("here", selectedImagePath);
                if(selectedImagePath != null){
    			    // Get the dimensions of the View
//    			    int targetW = 300;
//   			    int targetH = 300;
                	int targetW = pick.getWidth();
    			    int targetH = pick.getHeight();

    			    // Get the dimensions of the bitmap
    			    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    			    bmOptions.inJustDecodeBounds = true;
    			    BitmapFactory.decodeFile(selectedImagePath, bmOptions);
    			    int photoW = bmOptions.outWidth;
    			    int photoH = bmOptions.outHeight;

    			    // Determine how much to scale down the image
    			    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

    			    // Decode the image file into a Bitmap sized to fill the View
    			    bmOptions.inJustDecodeBounds = false;
    			    bmOptions.inSampleSize = scaleFactor;
    			    bmOptions.inPurgeable = true;
    			    ExifInterface exif = null;
					int orientation = 1;
					try{
						exif = new ExifInterface(selectedImagePath);
					}
					catch(IOException e){
						post_toast("IOException");
					}
					if(exif != null){
						orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
						//post_toast(String.valueOf(orientation));
					}
    			    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, bmOptions);
    			    Matrix matrix = new Matrix();
    			    switch(orientation){
				    	case 3:
				    		matrix.postRotate(180);
			    		break;
    			    	case 6:
    			    		matrix.postRotate(90);
    			    		break;
    			    	case 8:
    			    		matrix.postRotate(270);
    			    		break;
    			    }    			    
    			    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    			    bitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
    			    bitmap = getCircularBitmapWithWhiteBorder(bitmap, 0);
    			    pick.setImageBitmap(bitmap);
    			    //bitmap.recycle();
                }
            }
        }
        //Log.e("activity result", "" + requestCode);
        if(requestCode == 64206){
        	//Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            if (Session.getActiveSession().isOpened()) {
                // Request user data and show the results
                Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Display the parsed user info
                            //Log.e("user", "UserID : " + user.getId());

                            //Log.e("token", "Token : " + Session.getActiveSession().getAccessToken());
        					if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
        						login = new Asynhttp(LogInActivity.this, global.fb_login);
        						login.execute(user.getId(), Session.getActiveSession().getAccessToken(), user.getProperty("email").toString());
        					}
        					try{
        						global.token = (String)login.get();
        						try{
        							InternalStorage.writeObject(getApplicationContext(), "EMAIL", user.getProperty("email").toString());
        						}
        						catch(IOException e1){
        							post_toast("Write fail.");
        							return;
        						}
		    					JSONObject json = (JSONObject)global.user_json.get("rongToken");
		    		            String token = (String)json.get("token");
		    					try{
		    						InternalStorage.writeObject(getApplicationContext(), "RONGTOKEN", token);
		    					}
		    					catch(IOException e1){
		    						post_toast("Write fail.");
		    						return;
		    					}
        						try{
        							InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
        						}
        						catch(IOException e1){
        							post_toast("Write fail.");
        							return;
        						}
        						if(!global.token.equals("")){
                                	global.fb_flag = true;
                					try{
										InternalStorage.writeObject(getApplicationContext(), "FB_FLAG", global.fb_flag);
									}
                					catch(IOException e){
                						post_toast("Write fail.");
									}
                                	fb_button.setEnabled(false);
        							//Log.e("token", global.token);
        							Asynhttp get_profile = new Asynhttp(LogInActivity.this, global.get_profile);
        							get_profile.execute(global.token);
        							global.user_json = (JSONObject)get_profile.get();
        							if(global.user_json == null || ((global.user_json.isNull("school") || global.user_json.get("school").equals("")) && (global.user_json.isNull("lolId") || global.user_json.get("lolId").equals("")) && (global.user_json.isNull("dotaId") || global.user_json.get("dotaId").equals("")) && (global.user_json.isNull("hstoneId") || global.user_json.get("hstoneId").equals("")))){
        		    					global.mode = global.profile_mode;
        		    					try{
        		    						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
        		    					}
        		    					catch(IOException e1){
        		    						post_toast("Write fail.");
        		    						return;
        		    					}
        		    					JSONObject json2 = (JSONObject)global.user_json.get("rongToken");
        		    		            String token2 = (String)json2.get("token");
        		    					try{
        		    						InternalStorage.writeObject(getApplicationContext(), "RONGTOKEN", token2);
        		    					}
        		    					catch(IOException e1){
        		    						post_toast("Write fail.");
        		    						return;
        		    					}
        		    					//signup_button.setEnabled(true);
        		    					//login_hint.setEnabled(true);
        		    					//spinner.setVisibility(View.GONE);
//        		    					back_layout.setVisibility(View.VISIBLE);
        		    					pick.setVisibility(View.INVISIBLE);
        		    					right_arrow.setVisibility(View.INVISIBLE);
        		    					left_arrow.setVisibility(View.INVISIBLE);
        		    					select_layout.setVisibility(View.INVISIBLE);
        		    					profile_layout.setVisibility(View.VISIBLE);
//        		    					top_layout.setVisibility(View.VISIBLE);
        								return;
        							}
        							else{
        								global.mode = global.logged_in_mode;
        								try{
        									InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
        								}
        								catch(IOException e1){
        									post_toast("Write fail.");
        									return;
        								}
        		    					JSONObject json2 = (JSONObject)global.user_json.get("rongToken");
        		    		            String token2 = (String)json2.get("token");
        		    					try{
        		    						InternalStorage.writeObject(getApplicationContext(), "RONGTOKEN", token2);
        		    					}
        		    					catch(IOException e1){
        		    						post_toast("Write fail.");
        		    						return;
        		    					}
        		    					//signup_button.setEnabled(true);
        		    					//login_hint.setEnabled(true);
        		    					//spinner.setVisibility(View.GONE);
        								//Intent intent = new Intent(LogInActivity.this, NewsActivity.class);
        								//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        								//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        								//intent.putExtra("EXIT", true);
        								startActivity(intent);
        								//overridePendingTransition(0, 0);
        								finish();
        							}
        						}
        					}
        					catch(InterruptedException | ExecutionException e){
        						post_toast("Fail to log in with fb.");
        						return;
        					}
        					catch(JSONException e){
        						post_toast("Fail to log in with fb.");
        						return;
        					}
                        }
                    }

                });
            }
        }
    }
    
    public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
            int borderWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        //uiHelper = new UiLifecycleHelper(this, callback);
        //uiHelper.onCreate(savedInstanceState);
        context = getApplicationContext();
        //init_thread = null;
        selectedImagePath = null;
        intent = new Intent(LogInActivity.this, NewsActivity.class);
        
		setContentView(R.layout.activity_login);
		
		forgot_password = (LinearLayout)findViewById(R.id.forgot_password);
		//init_layout = (LinearLayout)findViewById(R.id.init_layout);
//		back_layout = (LinearLayout)findViewById(R.id.back_layout);
//		top_layout = (RelativeLayout)findViewById(R.id.top_layout);
		select_layout = (LinearLayout)findViewById(R.id.select_layout);
		signup_layout = (LinearLayout)findViewById(R.id.signup_layout);
		login_layout = (RelativeLayout)findViewById(R.id.login_layout);
		profile_layout = (LinearLayout)findViewById(R.id.profile_layout);
		//profile_layout2 = (LinearLayout)findViewById(R.id.profile_layout2);
		//login_button = (Button)findViewById(R.id.login_button);
		//signup_button = (Button)findViewById(R.id.signup_button);
		signup = (TextView)findViewById(R.id.signup);
		left_arrow = (ImageView)findViewById(R.id.left_arrow);
		right_arrow = (ImageView)findViewById(R.id.right_arrow);
		pick = (ImageView)findViewById(R.id.pick);
		signup_email = (EditText)findViewById(R.id.signup_email);
		//signup_username = (EditText)findViewById(R.id.signup_username);
		signup_password = (EditText)findViewById(R.id.signup_password);
		login_email = (EditText)findViewById(R.id.login_email);
		username = (EditText)findViewById(R.id.username);
		login_password = (EditText)findViewById(R.id.login_password);
		lol_id = (EditText)findViewById(R.id.lol_id);
		//dota_id = (EditText)findViewById(R.id.dota_id);
		//hh_stone_id = (EditText)findViewById(R.id.hh_stone_id);
		school = (AutoCompleteTextView)findViewById(R.id.school);
		login_button = (TextView) findViewById(R.id.login);
		login_button2 = (TextView)findViewById(R.id.login2);
		submit_button = (TextView)findViewById(R.id.submit);
		//login_hint = (TextView)findViewById(R.id.login_hint);
		//spinner = (ProgressBar)findViewById(R.id.progressBar);
		//init_layout.setVisibility(View.VISIBLE);
//		back_layout.setVisibility(View.INVISIBLE);
//		top_layout.setVisibility(View.INVISIBLE);
		select_layout.setVisibility(View.INVISIBLE);
		signup_layout.setVisibility(View.INVISIBLE);
		login_layout.setVisibility(View.INVISIBLE);
		profile_layout.setVisibility(View.INVISIBLE);
		left_arrow.setVisibility(View.INVISIBLE);
		right_arrow.setVisibility(View.INVISIBLE);
		//Log.e("MODE", String.valueOf(global.mode));
		//profile_layout2.setVisibility(View.INVISIBLE);
		//spinner.setVisibility(View.GONE);
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, school_name);
		school.setThreshold(1);
		school.setAdapter(adapter);

		//Log.e("mode", "" + global.mode);
		try{
			global.fb_flag = (boolean)InternalStorage.readObject(getApplicationContext(), "FB_FLAG");
		}
		catch(ClassNotFoundException | IOException e2){
			
		}
		if(global.mode == global.init_mode){
			try{
				global.mode = (int)InternalStorage.readObject(getApplicationContext(), "MODE");
				global.token = (String)InternalStorage.readObject(getApplicationContext(), "TOKEN");
				global.icon_url = (String)InternalStorage.readObject(getApplicationContext(), "ICON_URL");
			}
			catch(IOException e){
				global.mode = global.select_mode;
				global.token = "";
				global.fb_flag = false;
				global.icon_url = "";
				try{
					InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
					InternalStorage.writeObject(getApplicationContext(), "FB_FLAG", global.fb_flag);
					InternalStorage.writeObject(getApplicationContext(), "ICON_URL", global.icon_url);
				}
				catch(IOException e1){
					post_toast("Write fail.");
				}
			}
			catch(ClassNotFoundException e){
				post_toast("Write fail.");
			}
			//init_thread = new Asyninit(global.mode, global.token, select_layout, signup_layout, login_layout, init_layout, top_layout, profile_layout1, profile_layout2, right_arrow);
		}
		else{
			//init_layout.setVisibility(View.INVISIBLE);
			switch(global.mode){
				case global.select_mode:
//					back_layout.setVisibility(View.INVISIBLE);
//					top_layout.setVisibility(View.INVISIBLE);
					
					select_layout.setVisibility(View.VISIBLE);
					signup_layout.setVisibility(View.INVISIBLE);
					login_layout.setVisibility(View.INVISIBLE);
					profile_layout.setVisibility(View.INVISIBLE);
					left_arrow.setVisibility(View.INVISIBLE);
					right_arrow.setVisibility(View.INVISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					break;
				case global.signup_mode:
//					back_layout.setVisibility(View.VISIBLE);
//					top_layout.setVisibility(View.VISIBLE);
					select_layout.setVisibility(View.INVISIBLE);
					signup_layout.setVisibility(View.VISIBLE);
					login_layout.setVisibility(View.INVISIBLE);
					profile_layout.setVisibility(View.INVISIBLE);
					left_arrow.setVisibility(View.VISIBLE);
					right_arrow.setVisibility(View.VISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					break;
				case global.login_mode:
//					back_layout.setVisibility(View.VISIBLE);
//					top_layout.setVisibility(View.VISIBLE);
					select_layout.setVisibility(View.INVISIBLE);
					signup_layout.setVisibility(View.INVISIBLE);
					login_layout.setVisibility(View.VISIBLE);
					profile_layout.setVisibility(View.INVISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					left_arrow.setVisibility(View.VISIBLE);
					right_arrow.setVisibility(View.INVISIBLE);
					break;
				case global.profile_mode:
//					back_layout.setVisibility(View.VISIBLE);
//					top_layout.setVisibility(View.VISIBLE);
					select_layout.setVisibility(View.INVISIBLE);
					signup_layout.setVisibility(View.INVISIBLE);
					login_layout.setVisibility(View.INVISIBLE);
					profile_layout.setVisibility(View.VISIBLE);
					if(global.fb_flag){
						pick.setVisibility(View.INVISIBLE);
					}
					//left_arrow.setVisibility(View.VISIBLE);
					//right_arrow.setVisibility(View.VISIBLE);
					//profile_layout2.setVisibility(View.INVISIBLE);
					break;
			}
		}

		Log.e("token", " "+String.valueOf(global.token));
		forgot_password.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(login_email.getText().toString().equals("")){
					return;
				}
				Asynhttp forgot = new Asynhttp(LogInActivity.this, global.forgot_pw);
				forgot.execute(global.token, login_email.getText().toString(), "", "");
			}
		});
		signup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				switch(global.mode){
					case global.select_mode:
						global.mode = global.signup_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
//						back_layout.setVisibility(View.VISIBLE);
//						top_layout.setVisibility(View.VISIBLE);
						left_arrow.setVisibility(View.VISIBLE);
						right_arrow.setVisibility(View.VISIBLE);
						select_layout.setVisibility(View.INVISIBLE);
						signup_layout.setVisibility(View.VISIBLE);
			        	break;
			        default:
			        	break;
				}
			}
        });
		login_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//Log.e("Mode: ", ""+global.mode);
				switch(global.mode){
					case global.select_mode:
						global.mode = global.login_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
//						back_layout.setVisibility(View.VISIBLE);
//						top_layout.setVisibility(View.VISIBLE);
						left_arrow.setVisibility(View.VISIBLE);
						right_arrow.setVisibility(View.INVISIBLE);
						select_layout.setVisibility(View.INVISIBLE);
						login_layout.setVisibility(View.VISIBLE);
			        	break;
			        default:
			        	break;
				}
			}
        });
		
		/*
		signup_button.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				switch(global.mode){
					case global.select_mode:
						global.mode = global.signup_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						top_layout.setVisibility(View.VISIBLE);
						select_layout.setVisibility(View.INVISIBLE);
						signup_layout.setVisibility(View.VISIBLE);
			        	break;
			        default:
			        	break;
				}
			}
        });
        */
		left_arrow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				switch(global.mode){
					case global.profile_mode:
						profile_layout.setVisibility(View.INVISIBLE);
						if(global.fb_flag){
							Session.getActiveSession().closeAndClearTokenInformation();
							global.token = "";
							try{
								InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
							}
							catch(IOException e1){
								post_toast("Write fail.");
								return;
							}
							global.mode = global.select_mode;
//							back_layout.setVisibility(View.INVISIBLE);
//							top_layout.setVisibility(View.INVISIBLE);
							select_layout.setVisibility(View.VISIBLE);
							left_arrow.setVisibility(View.INVISIBLE);
							right_arrow.setVisibility(View.INVISIBLE);
							global.fb_flag = false;
							try{
								InternalStorage.writeObject(getApplicationContext(), "FB_FLAG", global.fb_flag);
							}
							catch(IOException e1){
								post_toast("Write fail.");
							}
						}
						else{
							global.mode = global.signup_mode;
							signup_layout.setVisibility(View.VISIBLE);
						}
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						break;
					case global.signup_mode:
						global.mode = global.select_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
//						back_layout.setVisibility(View.INVISIBLE);
//						top_layout.setVisibility(View.INVISIBLE);
						left_arrow.setVisibility(View.INVISIBLE);
						right_arrow.setVisibility(View.INVISIBLE);
						select_layout.setVisibility(View.VISIBLE);
						signup_layout.setVisibility(View.INVISIBLE);
						login_layout.setVisibility(View.INVISIBLE);
						break;
				        
					case global.login_mode:
						global.mode = global.select_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						left_arrow.setVisibility(View.INVISIBLE);
						right_arrow.setVisibility(View.INVISIBLE);
//						top_layout.setVisibility(View.INVISIBLE);
						select_layout.setVisibility(View.VISIBLE);
						login_layout.setVisibility(View.INVISIBLE);
						signup_layout.setVisibility(View.INVISIBLE);
			        	break;
					/*
					case global.profile_mode2:
						global.mode = global.profile_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						profile_layout.setVisibility(View.VISIBLE);
						//profile_layout2.setVisibility(View.INVISIBLE);
						break;
					
			        default:
			        	break;
					*/
				}
			}
        });
		right_arrow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				switch(global.mode){
					case global.signup_mode:
						if(signup_email.getText().toString().equals("") || signup_password.getText().toString().equals("")){
							return;
						}
						if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
							login = new Asynhttp(LogInActivity.this, global.signup);
							signup_em = signup_email.getText().toString();
							signup_pw = signup_password.getText().toString();
							login.execute(signup_em, signup_pw);
							try{
								String result = (String)login.get();
		    					try{
		    						InternalStorage.writeObject(getApplicationContext(), "EMAIL", signup_em);
		    					}
		    					catch(IOException e1){
		    						post_toast("Write fail.");
		    						return;
		    					}
		    					JSONObject json = (JSONObject)global.user_json.get("rongToken");
		    		            String token = (String)json.get("token");
		    					try{
		    						InternalStorage.writeObject(getApplicationContext(), "RONGTOKEN", token);
		    					}
		    					catch(IOException e1){
		    						post_toast("Write fail.");
		    						return;
		    					}
								if(result == null){
					                new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
					                .setTitleText("Oops...")
					                .setContentText("Server busy!")
					                .show();
									return;
								}
								if(result.equals("")){
					                new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
					                .setTitleText("Oops...")
					                .setContentText("Email registered!")
					                .show();
									return;
								}
								verification();
								if(global.token == null || global.token.equals("")){
									return;
								}
								Log.e("tokenasd", global.token);
								global.mode = global.profile_mode;
								try{
									InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
								}
								catch(IOException e1){
									post_toast("Write fail.");
									return;
								}
//									back_layout.setVisibility(View.VISIBLE);
								right_arrow.setVisibility(View.INVISIBLE);
								left_arrow.setVisibility(View.INVISIBLE);
								signup_layout.setVisibility(View.INVISIBLE);
								profile_layout.setVisibility(View.VISIBLE);
							}
							catch(InterruptedException | ExecutionException | JSONException e){
				                new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
				                .setTitleText("Oops...")
				                .setContentText("Fail to signup!")
				                .show();
								return;
							}
						}
						/*
						global.mode = global.profile_mode;
						signup_layout.setVisibility(View.INVISIBLE);
						profile_layout.setVisibility(View.VISIBLE);
						left_arrow.setVisibility(View.INVISIBLE);
						right_arrow.setVisibility(View.INVISIBLE);
						*/
						break;
					/*
					case global.profile_mode1:
						if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
							login = new Asynhttp(LogInActivity.this, global.profile);
							login.execute(global.token, school.getText().toString(), lol_id.getText().toString());
							try{
								if(!global.fb_flag && login.get().equals("Succeed")){
									global.mode = global.profile_mode2;
									try{
										InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
									}
									catch(IOException e1){
										post_toast("Write fail.");
										return;
									}
									profile_layout.setVisibility(View.INVISIBLE);
									//profile_layout2.setVisibility(View.VISIBLE);
								}
								else{
									global.mode = global.logged_in_mode;
									try{
										InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
									}
									catch(IOException e1){
										post_toast("Write fail.");
										return;
									}
									Intent intent = new Intent(LogInActivity.this, NewsActivity.class);
									//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
									//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									//intent.putExtra("EXIT", true);
									startActivity(intent);
									overridePendingTransition(0, 0);
								}
							}
							catch(InterruptedException | ExecutionException e){
								post_toast("Fail to set profile.");
								return;
							}
						}
						break;
					case global.profile_mode2:
						if(selectedImagePath != null && !selectedImagePath.equals("")){
							if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
								login = new Asynhttp(LogInActivity.this, global.profile_icon);
								login.execute(global.token, selectedImagePath);
								try{
									global.user_json = (JSONObject)login.get();
									if(global.user_json == null){
										post_toast("Fail to transmit icon.");
										return;
									}
									global.icon_url = (String)global.user_json.get("profile_icon");
									try{
										InternalStorage.writeObject(getApplicationContext(), "ICON_URL", global.icon_url);
									}
									catch(IOException e1){
										post_toast("Write fail.");
										return;
									}
									if(!global.icon_url.equals("")){
										//Asynhttp icon = new Asynhttp(LogInActivity.this, global.download_profile_icon);
										//icon.execute(icon_url);
										//Bitmap profile_icon = (Bitmap)icon.get();
										global.mode = global.logged_in_mode;
										try{
											InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
										}
										catch(IOException e1){
											post_toast("Write fail.");
											return;
										}
										Intent intent = new Intent(LogInActivity.this, NewsActivity.class);
										//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
										//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										//intent.putExtra("EXIT", true);
										startActivity(intent);
										overridePendingTransition(0, 0);
									}
								}
								catch(InterruptedException e){
									post_toast("Fail to transmit icon.");
									return;
								}
								catch(ExecutionException e){
									post_toast("Fail to transmit icon.");
									return;
								}
								catch(JSONException e){
									post_toast("Fail to transmit icon.");
									return;
								}
							}
						}
						else{
							post_toast("Select an image as your icon to proceed.");
						}
						break;
					*/
					default:
						break;
				}
			}
        });
		login_button2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				/*(
				global.width = select_layout.getWidth();
				global.height = select_layout.getHeight();
				Log.e("width", Integer.toString(global.width));
				Log.e("height", Integer.toString(global.height));
				*/
				if(last_click_l == 1){
					return;
				}
				last_click_l = 1;
				if(last_click_time_l == 0)
					last_click_time_l = SystemClock.elapsedRealtime();
				else if(SystemClock.elapsedRealtime() - last_click_time_l < 1000){
					last_click_l = 0;
					return;
				}
				else
					last_click_time_l = SystemClock.elapsedRealtime();
				
				//login_button2.setEnabled(false);
				if(login_email.getText().toString().equals("") || login_password.getText().toString().equals("")){
					//login_button2.setEnabled(true);
					last_click_l = 0;
					return;
				}
				if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
					login = new Asynhttp(LogInActivity.this, global.login);
					login.execute(login_email.getText().toString(), login_password.getText().toString());
				}
				try{
					global.token = (String)login.get();
					try{
						InternalStorage.writeObject(getApplicationContext(), "EMAIL", login_email.getText().toString());
					}
					catch(IOException e1){
						post_toast("Write fail.");
						return;
					}
					JSONObject json = (JSONObject)global.user_json.get("rongToken");
		            String token = (String)json.get("token");
					try{
						InternalStorage.writeObject(getApplicationContext(), "RONGTOKEN", token);
					}
					catch(IOException e1){
						post_toast("Write fail.");
						return;
					}
					try{
						InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
					}
					catch(IOException e1){
						post_toast("Write fail.");
						//login_button2.setEnabled(true);
						last_click_l = 0;
						return;
					}
					if(global.token == null){
		                new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
		                .setTitleText("Oops...")
		                .setContentText("Server busy!")
		                .show();
						//login_button2.setEnabled(true);
						last_click_l = 0;
		                return;
					}
					Log.e("summoner", global.token);
					if(global.token != null && !global.token.equals("")){
						Asynhttp get_profile = new Asynhttp(LogInActivity.this, global.get_profile);
						get_profile.execute(global.token);
						global.user_json = (JSONObject)get_profile.get();
						if(global.user_json == null){
			                new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
			                .setTitleText("Oops...")
			                .setContentText("Server busy!")
			                .show();
							//login_button2.setEnabled(true);
							last_click_l = 0;
			                return;
						}
						if(global.user_json == null || ((global.user_json.isNull("school") || global.user_json.get("school").equals("")) || (global.user_json.isNull("lolID") || global.user_json.get("lolID").equals("")) || (global.user_json.isNull("dotaID") || global.user_json.get("dotaID").equals("")) || (global.user_json.isNull("hstoneID") || global.user_json.get("hstoneID").equals("")))){
	    					global.mode = global.profile_mode;
	    					try{
	    						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
	    					}
	    					catch(IOException e1){
	    						post_toast("Write fail.");
	    						//login_button2.setEnabled(true);
	    						last_click_l = 0;
	    						return;
	    					}
	    					//signup_button.setEnabled(true);
	    					//login_hint.setEnabled(true);
	    					//spinner.setVisibility(View.GONE);
//	    					back_layout.setVisibility(View.VISIBLE);
	    					if(global.fb_flag){
	    						pick.setVisibility(View.INVISIBLE);
	    					}
	    					else{
	    						pick.setVisibility(View.VISIBLE);
	    					}
	    					left_arrow.setVisibility(View.INVISIBLE);
	    					right_arrow.setVisibility(View.INVISIBLE);
	    					select_layout.setVisibility(View.INVISIBLE);
	    					profile_layout.setVisibility(View.VISIBLE);
	    					login_layout.setVisibility(View.INVISIBLE);
//	    					top_layout.setVisibility(View.VISIBLE);
	    					//login_button2.setEnabled(true);
	    					last_click_l = 0;
							return;
						}
						else{
	    					Log.e("summoner", global.user_json.toString());
	    					global.summonerid = (String)global.user_json.getString("hstoneID");
	    					global.summonerlevel = String.valueOf(30);
	    					global.summonername = (String)global.user_json.getString("lolID");
	    					global.school = (String)global.user_json.getString("school");
	    					global.solo_tier = (String)global.user_json.getString("dotaID");
							
							global.mode = global.logged_in_mode;
							try{
								InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
							}
							catch(IOException e1){
								post_toast("Write fail.");
								//login_button2.setEnabled(true);
								last_click_l = 0;
								return;
							}

	    					//login_loading = 7;
	                        //pDialog.dismiss();
	    					//signup_button.setEnabled(true);
	    					//login_hint.setEnabled(true);
	    					//spinner.setVisibility(View.GONE);
							Intent intent = new Intent(LogInActivity.this, NewsActivity.class);
							//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							//intent.putExtra("EXIT", true);
							startActivity(intent);
							//overridePendingTransition(0, 0);
							finish();
							//login_button2.setEnabled(true);
							return;
						}
					}
				}
				catch(InterruptedException | ExecutionException | JSONException e){
					//post_toast("Fail to log in.");
					//login_button2.setEnabled(true);
					last_click_l = 0;
					return;
				}
				/*
                new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Email and password combination does not exist!")
                .show();
                */
				//login_button2.setEnabled(true);
				last_click_l = 0;
			}
        });
		submit_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(last_click_s == 1){
					return;
				}
				last_click_s = 1;
				if(last_click_time_s == 0)
					last_click_time_s = SystemClock.elapsedRealtime();
				else if(SystemClock.elapsedRealtime() - last_click_time_s < 2500){
					last_click_s = 0;
					return;
				}
				else
					last_click_time_s = SystemClock.elapsedRealtime();
				
				////submit_button.setEnabled(false);
				global.width = select_layout.getWidth();
				global.height = select_layout.getHeight();
				Log.e("token", " "+String.valueOf(global.token));
				if(!global.fb_flag && selectedImagePath == null){
					post_toast("Select an image as your icon to proceed.");
					//submit_button.setEnabled(true);
					last_click_s = 0;
					return;
				}
				if(school.getText().toString().equals("") || lol_id.getText().toString().equals("") || username.getText().toString().equals("")){
				//if(school.getText().toString().equals("") || lol_id.getText().toString().equals("")){
					post_toast("Filling the blanks to proceed.");
					//submit_button.setEnabled(true);
					last_click_s = 0;
					return;
				}
				Asynhttp summoner_id = new Asynhttp(LogInActivity.this, global.summoner_id);
				summoner_id.execute(lol_id.getText().toString());
				try{
					JSONObject summoner = (JSONObject)summoner_id.get();
					if(summoner == null){
						////submit_button.setEnabled(true);
						last_click_s = 0;
						return;
					}
					try{
						JSONObject real_summoner = summoner.getJSONObject(lol_id.getText().toString());
						global.summonerid = ((Integer)real_summoner.get("id")).toString();
						global.summonerlevel = ((Integer)real_summoner.get("summonerLevel")).toString();
    					global.summonername = lol_id.getText().toString();
						//Log.e("summoner", global.summonerid);
						Asynhttp summoner_entry = new Asynhttp(LogInActivity.this, global.summoner_entry);
						summoner_entry.execute(global.summonerid);
						JSONObject summonerentry = (JSONObject)summoner_entry.get();
						//Log.e("summoner", summonerentry.toString());
						JSONArray read_summonerentry = summonerentry.getJSONArray(global.summonerid);
						int l = read_summonerentry.length();
						global.solo_tier = "noRank";
						for(int i = 0; i < l; i++){
							JSONObject temp = (JSONObject)read_summonerentry.getJSONObject(i);
							String queue = (String)temp.get("queue");
							if(queue.equals("RANKED_SOLO_5x5")){
								//Log.e("here", queue);
								global.solo_tier = (String)temp.get("tier");
								global.solo_tier += " " + (String)((JSONObject)((JSONArray)temp.getJSONArray("entries")).get(0)).get("division");
								//Log.e("here", global.solo_tier);
								break;
							}
						}
					}
					catch(JSONException e){
						post_toast("Summoner JSONException.");
						////submit_button.setEnabled(true);
						last_click_s = 0;
						return;
					}
				}
				catch(InterruptedException | ExecutionException e){
					post_toast("Summoner fatal error.");
					////submit_button.setEnabled(true);
					last_click_s = 0;
					return;
				}
				global.school = school.getText().toString();
				Asynhttp newlogin = new Asynhttp(LogInActivity.this, global.profile);
				newlogin.execute(global.token, "Male", username.getText().toString(), school.getText().toString(), global.solo_tier, lol_id.getText().toString(), global.summonerid);
				try{
					if(global.fb_flag){
						global.user_json = (JSONObject)newlogin.get();
						if(global.user_json == null){
							post_toast("Fail to transmit icon.");
							//////submit_button.setEnabled(true);
							////submit_button.setEnabled(true);
							last_click_s = 0;
							return;
						}
						/*
						global.mode = global.logged_in_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						*/
						Intent intent = new Intent(LogInActivity.this, SummonerInfo.class);
						//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						//intent.putExtra("EXIT", true);
						startActivity(intent);
						//overridePendingTransition(0, 0);
						finish();
						//////submit_button.setEnabled(true);
						return;
					}
				}
				catch(InterruptedException | ExecutionException e){
					post_toast("Fail to set profile.");
					////submit_button.setEnabled(true);
					last_click_s = 0;
					return;
				}
				if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
					login = new Asynhttp(LogInActivity.this, global.profile_icon);
					login.execute(global.token, selectedImagePath);
					Log.e("icon", "here");
					try{
						global.user_json = (JSONObject)login.get();
						if(global.user_json == null){
							post_toast("Fail to transmit icon.");
							////submit_button.setEnabled(true);
							last_click_s = 0;
							return;
						}
						global.icon_url = (String)global.user_json.get("profile_icon");
						try{
							InternalStorage.writeObject(getApplicationContext(), "ICON_URL", global.icon_url);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							////submit_button.setEnabled(true);
							last_click_s = 0;
							return;
						}
						if(!global.icon_url.equals("")){
							//Asynhttp icon = new Asynhttp(LogInActivity.this, global.download_profile_icon);
							//icon.execute(icon_url);
							//Bitmap profile_icon = (Bitmap)icon.get();
							/*
							global.mode = global.logged_in_mode;
							try{
								InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
							}
							catch(IOException e1){
								post_toast("Write fail.");
								return;
							}
							*/
							Intent intent = new Intent(LogInActivity.this, SummonerInfo.class);
							//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							//intent.putExtra("EXIT", true);
							startActivity(intent);
							//overridePendingTransition(0, 0);
							finish();
							////submit_button.setEnabled(true);
						}
					}
					catch(InterruptedException e){
						post_toast("Fail to transmit icon.");
						////submit_button.setEnabled(true);
						last_click_s = 0;
						return;
					}
					catch(ExecutionException e){
						post_toast("Fail to transmit icon.");
						last_click_s = 0;
						////submit_button.setEnabled(true);
						return;
					}
					catch(JSONException e){
						post_toast("Fail to transmit icon.");
						last_click_s = 0;
						//submit_button.setEnabled(true);
						return;
					}
					last_click_s = 0;
					//submit_button.setEnabled(true);
				}
			}
        });
		/*
		login_button.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(login_email.getText().toString().equals("") || login_password.getText().toString().equals("")){
					return;
				}
				if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
					login = new Asynhttp(LogInActivity.this, global.login);
					login.execute(login_email.getText().toString(), login_password.getText().toString());
				}
				try{
					global.token = (String)login.get();
					try{
						InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
					}
					catch(IOException e1){
						post_toast("Write fail.");
						return;
					}
					if(!global.token.equals("")){
						global.mode = global.logged_in_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						Intent intent = new Intent(LogInActivity.this, NewsActivity.class);
						//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						//intent.putExtra("EXIT", true);
						startActivity(intent);
						overridePendingTransition(0, 0);
					}
				}
				catch(InterruptedException | ExecutionException e){
					post_toast("Fail to log in.");
					return;
				}
			}
        });
		login_hint.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				switch(global.mode){
					case global.select_mode:
						global.mode = global.login_mode;
						try{
							InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
						}
						catch(IOException e1){
							post_toast("Write fail.");
							return;
						}
						top_layout.setVisibility(View.VISIBLE);
						right_arrow.setVisibility(View.INVISIBLE);
						select_layout.setVisibility(View.INVISIBLE);
						login_layout.setVisibility(View.VISIBLE);
						break;
				}
			}
		});
		*/
		pick.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent();
                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
				Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
			}
		});
		fb_button = (LoginButton) findViewById(R.id.fb_button);
		fb_button.setToolTipMode(ToolTipMode.NEVER_DISPLAY);
		fb_button.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		fb_button.setReadPermissions(Arrays.asList("public_profile","user_friends","email"));
		//profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
		//Log.e("mode", global.mode + "");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
	    if (keyCode == KeyEvent.KEYCODE_BACK){
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	private int signup_flag = 0;
	SweetAlertDialog signup_alert;
	private void verification() throws InterruptedException, ExecutionException {
		
		Asynhttp login = new Asynhttp(LogInActivity.this, global.login);
		Log.e("verification em/pw", signup_em + "/" + signup_pw);
		login.execute(signup_em, signup_pw);
		global.token = (String)login.get();
		try{
			InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
		}
		catch(IOException e1){
			post_toast("Write fail.");
			return;
		}
		if(global.token == null){
			new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.ERROR_TYPE)
			.setTitleText("Oops...")
		    .setContentText("Server busy!")
		    .show();
			return;
		}
		if(global.token.equals("")){
			if(signup_alert != null){
				signup_alert.dismiss();
			}
			signup_alert = new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.WARNING_TYPE);
			signup_alert.setTitleText("Have you verify your email?");
			signup_alert.setContentText("Please check your verification email.");
		    signup_alert.setCancelText("No, I use another one.");
		    signup_alert.setConfirmText("Yes.");
		    signup_alert.showCancelButton(true);
		    signup_alert.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
		                @Override
		                public void onClick(SweetAlertDialog sDialog) {
		                	signup_alert.dismiss();
		                }
		            })
		            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
		                @Override
		                public void onClick(SweetAlertDialog sDialog) {
		                	try {
								verification();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                	if(signup_flag == 0 && global.token != null && !global.token.equals("")){
		                		signup_flag = 1;
								global.mode = global.profile_mode;
								try{
									InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
								}
								catch(IOException e1){
									post_toast("Write fail.");
									return;
								}
	//								back_layout.setVisibility(View.VISIBLE);
								right_arrow.setVisibility(View.INVISIBLE);
								left_arrow.setVisibility(View.INVISIBLE);
								signup_layout.setVisibility(View.INVISIBLE);
								profile_layout.setVisibility(View.VISIBLE);
			                	signup_alert.dismiss();
		                	}
		                }
		            })
		            .show();
		}
	}
}
