package com.example.duang;

import io.rong.imkit.RongIM;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.example.duang.R;
import com.example.duang.LogInActivity.InternalStorage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Team extends Activity{
    private static final int SELECT_PICTURE = 1;
	private static Context context;
	private static Context this_context;
	private static ImageView in_team_icon;
	private ImageView exit_team;
	private ImageView options;
	private TextView team_name;
	private TextView rename_team;
	private TextView leave_team;
	private TextView dismiss_team;
	private LinearLayout options_layout;
	private LinearLayout group_chat;
	private boolean expanded = false;
    private String selectedImagePath;
    private Asynhttp upload_team_icon;
    private final int[] members_layout = {R.id.member1_layout, R.id.member2_layout, R.id.member3_layout, R.id.member4_layout};
    private final int[] members_icon = {R.id.member1_icon, R.id.member2_icon, R.id.member3_icon, R.id.member4_icon};
    private final int[] members_name = {R.id.member1_name, R.id.member2_name, R.id.member3_name, R.id.member4_name};
    private final int[] members_role = {R.id.member1_role, R.id.member2_role, R.id.member3_role, R.id.member4_role};
    
	public static void update_team_icon(){
    	Asynhttp get_team_icon = new Asynhttp(this_context, global.download_profile_icon);
    	get_team_icon.execute(global.team_icon_url);
    	try{
			in_team_icon.setImageBitmap((Bitmap)get_team_icon.get());
		}
    	catch(InterruptedException e){
			post_toast("Downloading team icon failure.");
		}
    	catch(ExecutionException e){
			post_toast("Downloading team icon failure.");
		}
	}
	
    public static void post_toast(String message){
    	if(this_context != null){
            new SweetAlertDialog(this_context, SweetAlertDialog.SUCCESS_TYPE)
            .setContentText(message)
            .show();
			return;
    	}
    }
	
    public static void expand(final View v, final int targetHeight){
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                v.getLayoutParams().height = interpolatedTime == 1
                        ? android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v, final int initialHeight){
        Animation a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(2*initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    
	private void update_icon(String url, ImageView icon){
    	Asynhttp get_team_icon = new Asynhttp(Team.this, global.download_profile_icon);
    	get_team_icon.execute(url);
    	try{
    		icon.setImageBitmap((Bitmap)get_team_icon.get());
		}
    	catch(InterruptedException e){
			post_toast("Downloading icon failure.");
		}
    	catch(ExecutionException e){
			post_toast("Downloading icon failure.");
		}
	}

    private String getPath(Uri uri){
        if(uri == null){
            return null;
        }
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
            .getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                if(selectedImagePath != null){
    			    // Get the dimensions of the View
    			    int targetW = 300;
    			    int targetH = 300;

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
    			    //bitmap.recycle();

					if(upload_team_icon == null || upload_team_icon.getStatus() == AsyncTask.Status.PENDING || upload_team_icon.getStatus() == AsyncTask.Status.FINISHED){
    					upload_team_icon = new Asynhttp(Team.this, global.team_icon);
    					upload_team_icon.execute(global.token, selectedImagePath);
    					try{
    						global.team_json = (JSONObject)upload_team_icon.get();
    						if(global.team_json == null){
        						post_toast("Icon upload failure.");
        						return;
    						}
    						global.team_icon_url = (String)global.team_json.get("teamIcon");
    					}
    					catch(InterruptedException | ExecutionException | JSONException e){
    						post_toast("Icon upload failure.");
    						return;
    					}
    					try{
    						InternalStorage.writeObject(getApplicationContext(), "TEAM_ICON_URL", global.team_icon_url);
    					}
    					catch(IOException e1){
    						post_toast("Write fail.");
    						return;
    					}
    					if(global.team_icon_url != null && !global.team_icon_url.equals("") && !global.team_icon_url.equals("None")){
            			    in_team_icon.setImageBitmap(bitmap);
    					}
					}
                }
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.team);
    	context = getApplicationContext();
    	this_context = Team.this;
    	
    	options_layout = (LinearLayout)findViewById(R.id.options_layout);
    	options = (ImageView)findViewById(R.id.options);
    	exit_team = (ImageView)findViewById(R.id.exit_team);
    	in_team_icon = (ImageView)findViewById(R.id.in_team_icon);
    	team_name = (TextView)findViewById(R.id.team_name);
    	rename_team = (TextView)findViewById(R.id.rename_team);
    	leave_team = (TextView)findViewById(R.id.leave_team);
    	dismiss_team = (TextView)findViewById(R.id.dismiss_team);
    	group_chat = (LinearLayout)findViewById(R.id.group_chat);
    	
    	options_layout.setVisibility(View.GONE);
    	options.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				if(expanded){
					collapse(options_layout, 150);
					expanded = false;
				}
				else{
					expand(options_layout, 150);
					expanded = true;
				}
			}
    	});
    	exit_team.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
	        	Asynhttp get_team_profile = new Asynhttp(Team.this, global.get_team_profile);
	        	get_team_profile.execute(global.token);
	        	try{
	        		global.team_json = (JSONObject)get_team_profile.get();
	        		if(global.team_json != null){
		        		global.team_icon_url = (String)global.team_json.get("teamIcon");
	        		}
				}
	        	catch(InterruptedException e){
					post_toast("Getting team profile failure.");
				}
	        	catch(ExecutionException e){
					post_toast("Getting team profile failure.");
				}
	        	catch(JSONException e){
					post_toast("Getting team profile failure.");
				}
				finish();
			}
    	});
        if(global.team_json == null){
        	Asynhttp get_team_profile = new Asynhttp(Team.this, global.get_team_profile);
        	get_team_profile.execute(global.token);
        	try{
        		global.team_json = (JSONObject)get_team_profile.get();
        		if(global.team_json != null){
	        		global.team_icon_url = (String)global.team_json.get("teamIcon");
        		}
        		else{
        			global.team_icon_url = "";
        		}
			}
        	catch(InterruptedException e){
				post_toast("Getting team profile failure.");
			}
        	catch(ExecutionException e){
				post_toast("Getting team profile failure.");
			}
        	catch(JSONException e){
				post_toast("Getting team profile failure.");
			}
        }
        if(global.team_icon_url != null && !global.team_icon_url.equals("") && !global.team_icon_url.equals("None")){
        	update_team_icon();
        }
        in_team_icon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
			}
        });
        for(int i = 0; i < 4; i++){
        	((LinearLayout)findViewById(members_layout[i])).setVisibility(View.INVISIBLE);
        }
        if(global.team_json != null){
        	try{
				team_name.setText((String)global.team_json.get("teamName"));
				JSONArray captain_array = (JSONArray)global.team_json.get("captain");
				JSONObject captain = (JSONObject)captain_array.get(0);
				String icon_url = null;
				if(!captain.isNull("profile_icon")){
					icon_url = (String)captain.get("profile_icon");
					if(icon_url != null && !icon_url.equals("")){
						update_icon(icon_url, (ImageView)findViewById(R.id.captain_icon));
					}
				}
				String name = null;
				if(!captain.isNull("username")){
					name = (String)captain.get("username");
					if(name != null && !name.equals("")){
						((TextView)findViewById(R.id.captain_name)).setText(name);
					}
				}
				JSONArray memebers = (JSONArray)global.team_json.get("members");
				for(int i = 0; i < memebers.length(); i++){
					((LinearLayout)findViewById(members_layout[i])).setVisibility(View.VISIBLE);
					JSONObject member = (JSONObject)memebers.get(i);
					if(!member.isNull("profile_icon")){
						icon_url = (String)member.get("profile_icon");
						if(icon_url != null && !icon_url.equals("")){
							update_icon(icon_url, (ImageView)findViewById(members_icon[i]));
						}
					}
					if(!member.isNull("username")){
						name = (String)member.get("username");
						if(name != null && !name.equals("")){
							((TextView)findViewById(members_name[i])).setText(name);
						}
					}
				}
			}
        	catch(JSONException e){
				post_toast("Team profile JSONException.");
			}
        }
        leave_team.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	        	Asynhttp leave_team = new Asynhttp(Team.this, global.leave_team);
	        	leave_team.execute(global.token);
	        	String message = "";
	        	try{
	        		message = (String)leave_team.get();
					if(message == null){
						return;
					}
				}
	        	catch(InterruptedException | ExecutionException e){
	        		return;
				}
	        	NewsActivity.alert_dialog("Leave team "+message);
				global.team_icon_url = "";
				global.team_json = null;
				finish();
			}
        });
        dismiss_team.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	        	Asynhttp dismiss_team = new Asynhttp(Team.this, global.dismiss_team);
	        	dismiss_team.execute(global.token);
	        	String message = "";
	        	try{
	        		message = (String)dismiss_team.get();
					if(message == null){
						return;
					}
				}
	        	catch(InterruptedException | ExecutionException e){
	        		return;
				}
	        	NewsActivity.alert_dialog("Dismiss team "+message);
				global.team_icon_url = "";
				global.team_json = null;
				finish();
			}
        });

        group_chat.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				//RongIM.getInstance().startConversationList(Team.this);
				try{
					RongIM.getInstance().startGroupChat(Team.this, global.team_json.getString("id"), global.team_json.getString("teamName"));
				}
				catch(JSONException e){
					
				}
			}
        });
    }
}
