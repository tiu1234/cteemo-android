package com.example.duang;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.OperationCallback;
import io.rong.imlib.RongIMClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.example.duang.R;
import com.facebook.Session;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityHelper;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class NewsActivity extends FragmentActivity implements SlidingActivityBase{
    private final int SELECT_PICTURE = 1;
	public static ScreenSlidePageFragment[] slides;
	private final static int MAX_NEWS_NUM = 20;
    public static JSONArray news_list;
	private static Context context;
	private static Context this_context;
	private static Resources resource;
	//private static Asynhttp news;
	private static ImageView team_icon;
	private static boolean news_update_flag = false;
	private final int NUM_PAGES = 1;
//	private ImageView notfication;
	private TextView logout;
	private TextView title;
    private ViewPager slide_screen;
    private PagerAdapter slide_screen_adapter;
	private SlidingActivityHelper mHelper;
	private SlidingMenu sliding_menu;
	private RelativeLayout news_view;
	private LinearLayout team_view;
	private RelativeLayout find_teammate_button;
	private ImageView dog;
	private Bitmap tempBitmap = null;
	
	private static RongIMClient.Group group = null;

	private static Intent intent;

	private TextView bar[];
	private int currSelected = -1;
	private int[][] icon_ids =
		{ {R.drawable.news_icon_pressed, R.drawable.news_icon},
		{R.drawable.tourny_icon_selected, R.drawable.tourny_icon},
		{R.drawable.team_icon_selected, R.drawable.team_icon},
		{R.drawable.me_icon_selected, R.drawable.me_icon} };
	private LinearLayout me_view;
	private LinearLayout tournament_view;
	
	private static final int NEWS = 0;
	private static final int TOURNAMENT = 1;
	private static final int TEAM = 2;
	private static final int ME = 3;
	
    private static class InternalStorage{
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPath(selectedImageUri);
            if(selectedImagePath != null){

            	int targetW = dog.getWidth();
			    int targetH = dog.getHeight();
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
			    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, bmOptions);
			    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			    bitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
				tempBitmap = bitmap;
			    dog.setImageBitmap(bitmap);
            }
        }
    }

    public static void post_toast(String message){
    	if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
        	if(slides[position] == null){
        		slides[position] = ScreenSlidePageFragment.create(news_list);
        	}
            return slides[position];
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }
    }
    
	public static void add_news(JSONObject json){
		Asynnews news_update = new Asynnews(context, json, this_context);
		news_update.execute();
	}
	
	public static void update_news(){
		if(news_update_flag){
			return;
		}
		else{
			news_update_flag = true;
		}
		if(news_list == null){
			return;
		}
		final Asynhttp news = new Asynhttp(this_context, global.get_news_list);
		news.execute("http://54.149.235.253:4000/news_list/all/0", global.token);
	    new Thread(new Runnable(){
	        @Override
			public void run(){
				try{
					JSONArray json_array = (JSONArray)news.get();
					if(json_array != null){
						for(int j = json_array.length()-1; j >= 0; j--){
							boolean add_flag = true;
							JSONObject json = null;
							try{
								json = (JSONObject)json_array.get(j);
							}
							catch(JSONException e1){
								post_toast("Fail to get news list.");
							}
							for(int i = news_list.length()-1; i >= 0; i--){
								try{
									if(!news_list.isNull(i)){
										if(((String)news_list.getJSONObject(i).get("title")).equals(json.get("title"))){
											add_flag = false;
											break;
										}
									}
								}
								catch(JSONException e){
									post_toast("Fail to get news list.");
								}
							}
							if(add_flag){
								if(news_list.length() >= MAX_NEWS_NUM){
									news_list = json_array;
								}
								else{
									try{
										if(json != null){
											add_news(json);
											news_list.put(news_list.length(), json);
										}
									}
									catch(JSONException e){
										post_toast("Fail to get news list.");
									}
								}
							}
						}
					}
				}
				catch(InterruptedException e){
					post_toast("Fail to get news list.");
				}
				catch(ExecutionException e){
					post_toast("Fail to get news list.");
				}
				try{
					InternalStorage.writeObject(context, "NEWS_LIST", news_list.toString());
				}
				catch(IOException e){
					post_toast("Write fail.");
				}
				news_update_flag = false;
		    }
		}).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		intent = new Intent(NewsActivity.this, LogInActivity.class);
		
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		context = getApplicationContext();
		resource = getResources();
		this_context = NewsActivity.this;
		setContentView(R.layout.activity_news);
		setBehindContentView(R.layout.sliding_menu);
		sliding_menu = getSlidingMenu();
		sliding_menu.setBehindOffset(500);
		sliding_menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//		setSlidingActionBarEnabled(true);
//		Sliding menu disabled for now.
		setSlidingActionBarEnabled(false);
		sliding_menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		if(slides == null){
			slides = new ScreenSlidePageFragment[NUM_PAGES];
		}
		if(news_list == null){
			try{
				news_list = new JSONArray((String)InternalStorage.readObject(getApplicationContext(), "NEWS_LIST"));
				global.token = (String)InternalStorage.readObject(getApplicationContext(), "TOKEN");
				global.fb_flag = (boolean)InternalStorage.readObject(getApplicationContext(), "FB_FLAG");
				//global.icon_url = (String)InternalStorage.readObject(getApplicationContext(), "ICON_URL");
				//global.team_icon_url = (String)InternalStorage.readObject(getApplicationContext(), "TEAM_ICON_URL");
			}
			catch(IOException e1){
				news_list = new JSONArray();
			}
			catch(ClassNotFoundException e){
				post_toast("Fatal error");
			}
			catch(JSONException e){
				post_toast("Fatal error");
			}
		}

		title = (TextView)findViewById(R.id.title);
		find_teammate_button = (RelativeLayout)findViewById(R.id.find_teammates_button);
		news_view = (RelativeLayout)findViewById(R.id.news_view);
		team_view = (LinearLayout)findViewById(R.id.team_view);
		me_view = (LinearLayout)findViewById(R.id.me_view);
		tournament_view = (LinearLayout)findViewById(R.id.tournament_view);
		dog = (ImageView)findViewById(R.id.dog);
//		notfication = (ImageView)findViewById(R.id.notification);
		logout = (TextView)findViewById(R.id.logout);

		bar = new TextView[4];
		bar[NEWS] = (TextView)findViewById(R.id.news_mode);
		bar[TOURNAMENT] = (TextView)findViewById(R.id.tournament_mode);
		bar[TEAM] = (TextView)findViewById(R.id.team_mode);
		bar[ME] = (TextView)findViewById(R.id.me_mode);
		
		
        //ScreenSlidePageFragment.page = 0;
		slide_screen = (ViewPager)findViewById(R.id.slide_screen);
		slide_screen_adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        slide_screen.setAdapter(slide_screen_adapter);
        if(global.user_json == null){
        	Asynhttp get_user_profile = new Asynhttp(NewsActivity.this, global.get_profile);
        	get_user_profile.execute(global.token);
        	try{
        		global.user_json = (JSONObject)get_user_profile.get();
			}
        	catch(InterruptedException e){
				post_toast("Getting user profile failure.");
			}
        	catch(ExecutionException e){
				post_toast("Getting user profile failure.");
			}
        }
        if(global.team_json == null){
        	Asynhttp get_team_profile = new Asynhttp(NewsActivity.this, global.get_team_profile);
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
				try{
					post_toast((String)global.team_json.get("message"));
					global.team_json = null;
				}
				catch(JSONException e1){
        			global.team_icon_url = "";
				}
			}
        }
        else{
			try{
				global.team_icon_url = (String)global.team_json.get("teamIcon");
			}
			catch(JSONException e){
				global.team_icon_url = null;
			}
        }

        RongIM.init(this, "pkfcgjstfmcl8", R.drawable.ic_launcher);
        if(global.team_json != null){
        	String token = "";
			try{
				token = (String)InternalStorage.readObject(getApplicationContext(), "RONGTOKEN");
			}
			catch(IOException | ClassNotFoundException e1){
				post_toast("Read fail.");
				return;
			}
			Log.e("rong", token);
			//token = "TI8gMxDCg3jn8j93Jg54eLiSPvCjR9Lj4In18RCEVuzUyGwaZ5oc/oLBy7hyHkG04AeBXxP4o0E=";
			RongIM.setGetUserInfoProvider(new RongIM.GetUserInfoProvider(){
				@Override
			    public RongIMClient.UserInfo getUserInfo(String userId){
					RongIMClient.UserInfo user = null;
					try{
						if(userId.equals(global.user_json.getString("id"))){
							try{
								user = new RongIMClient.UserInfo(global.user_json.getString("id"), global.user_json.getString("username"), global.user_json.getString("profile_icon"));
							}
							catch(JSONException e){
								
							}
						}
					}
					catch(JSONException e){
						
					}
			        return user;
			    }
			}, false);
		    if(global.team_icon_url == null){
		    	try{
					group = new RongIMClient.Group(global.team_json.getString("id"), global.team_json.getString("teamName"), "http://rongcloud-web.qiniudn.com/docs_demo_rongcloud_logo.png");
				}
		    	catch(JSONException e){
		    		
				}
		    }
		    else{
		    	try{
					group = new RongIMClient.Group(global.team_json.getString("id"), global.team_json.getString("teamName"), global.team_icon_url);
				}
		    	catch(JSONException e){
					
				}
		    }
			RongIM.GetGroupInfoProvider group_info = new RongIM.GetGroupInfoProvider(){
			    @Override
			    public RongIMClient.Group getGroupInfo(String groupId){
					try{
						if(groupId.equals(global.team_json.getString("id"))){
							return group;
						}
					}
					catch(JSONException e){
						
					}
			        return null;
			    }
			};
			RongIM.setGetGroupInfoProvider(group_info);
	        try{
	        	RongIM.connect(token, new RongIMClient.ConnectCallback(){
				    @Override
				    public void onSuccess(String s){
				    	//post_toast(s);
				        List<RongIMClient.Group> groups = new ArrayList<RongIMClient.Group>();
				        groups.add(group);
				        RongIM.getInstance().syncGroup(groups, new OperationCallback(){
							@Override
							public void onError(ErrorCode arg0) {
								post_toast(arg0.getMessage());
							}
							@Override
							public void onSuccess() {
								//post_toast("Success.");
							}
				        });
				    }
				    @Override
				    public void onError(ErrorCode errorCode){
				    	post_toast("Fail to connect to the server.");
				    }
				});
			}
	        catch(Exception e){
		    	post_toast("Exception failure.");
			}
        }
        if(RongIM.getInstance() != null)
        {
	        RongIM.getInstance().setConnectionStatusListener(new RongIM.ConnectionStatusListener(){
				@Override
				public void onChanged(ConnectionStatus arg0) {
					if(arg0 == RongIM.ConnectionStatusListener.ConnectionStatus.DISCONNECTED){
						String token = "";
						try{
							token = (String)InternalStorage.readObject(getApplicationContext(), "RONGTOKEN");
						}
						catch(IOException | ClassNotFoundException e1){
							post_toast("Read fail.");
							return;
						}
			        	try{
							RongIM.connect(token, new RongIMClient.ConnectCallback(){
							    @Override
							    public void onSuccess(String s){
							    	//post_toast(s);
							        List<RongIMClient.Group> groups = new ArrayList<RongIMClient.Group>();
							        groups.add(group);
							        RongIM.getInstance().syncGroup(groups, new OperationCallback(){
										@Override
										public void onError(ErrorCode arg0) {
											post_toast(arg0.getMessage());
										}
										@Override
										public void onSuccess() {
											//post_toast("Success.");
										}
							        });
							    }
							    @Override
							    public void onError(ErrorCode errorCode){
							    	post_toast("Fail to connect to the server.");
							    }
							});
						}
			        	catch(Exception e){
							
						}
					}
				}
	        });
        }

		try{
			global.mode = (int)InternalStorage.readObject(getApplicationContext(), "MODE");
		}
		catch(IOException e){
			global.mode = global.logged_in_mode;
			try{
				InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
			}
			catch(IOException e1){
				post_toast("Write failure.");
			}
		}
		catch(ClassNotFoundException e){
			post_toast("Fatal error.");
		}
		switch(global.mode){
			case global.logged_in_mode:
				title.setText("HOME");
				news_view.setVisibility(View.VISIBLE);
				tournament_view.setVisibility(View.INVISIBLE);
				team_view.setVisibility(View.INVISIBLE);
				me_view.setVisibility(View.INVISIBLE);
				if(currSelected != 0)
					selectIcon(0);
				break;
				
			case global.tournament_mode:
				title.setText("ADD");
				news_view.setVisibility(View.INVISIBLE);
				tournament_view.setVisibility(View.VISIBLE);
				team_view.setVisibility(View.INVISIBLE);
				me_view.setVisibility(View.INVISIBLE);
				if(currSelected != 1)
					selectIcon(1);
				break;
				
			case global.team_mode:
				title.setText("SEARCH");
				news_view.setVisibility(View.INVISIBLE);
				tournament_view.setVisibility(View.INVISIBLE);
				team_view.setVisibility(View.VISIBLE);
				me_view.setVisibility(View.INVISIBLE);
				if(currSelected != 2)
					selectIcon(2);
				break;
				
			case global.me_mode:
				title.setText("ME");
				news_view.setVisibility(View.INVISIBLE);
				tournament_view.setVisibility(View.INVISIBLE);
				team_view.setVisibility(View.INVISIBLE);
				me_view.setVisibility(View.VISIBLE);
				if(currSelected != 3)
					selectIcon(3);
				break;
				
			default:
				break;
		}

		
		bar[NEWS].setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				if(global.mode != global.logged_in_mode){
					global.mode = global.logged_in_mode;
					try{
						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					}
					catch(IOException e1){
						post_toast("Write failure.");
					}
					title.setText("HOME");
					news_view.setVisibility(View.VISIBLE);
					tournament_view.setVisibility(View.INVISIBLE);
					team_view.setVisibility(View.INVISIBLE);
					me_view.setVisibility(View.INVISIBLE);
					if(currSelected != NEWS)
						selectIcon(NEWS);
				}
			}
		});
		
		bar[TOURNAMENT].setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				if(global.mode != global.tournament_mode){
					global.mode = global.tournament_mode;
					try{
						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					}
					catch(IOException e1){
						post_toast("Write failure.");
					}
					title.setText("ADD");
					news_view.setVisibility(View.INVISIBLE);
					tournament_view.setVisibility(View.VISIBLE);
					team_view.setVisibility(View.INVISIBLE);
					me_view.setVisibility(View.INVISIBLE);
					if(currSelected != TOURNAMENT)
						selectIcon(TOURNAMENT);
				}
			}
		});
		
		bar[TEAM].setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				if(global.mode != global.team_mode){
					global.mode = global.team_mode;
					try{
						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					}
					catch(IOException e1){
						post_toast("Write failure.");
					}
					title.setText("SEARCH");
					news_view.setVisibility(View.INVISIBLE);
					tournament_view.setVisibility(View.INVISIBLE);
					team_view.setVisibility(View.VISIBLE);
					me_view.setVisibility(View.INVISIBLE);
					if(currSelected != TEAM)
						selectIcon(TEAM);
				}
			}
		});
		bar[ME].setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				if(global.mode != global.me_mode){
					global.mode = global.me_mode;
					try{
						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					}
					catch(IOException e1){
						post_toast("Write failure.");
					}
					title.setText("ME");
					news_view.setVisibility(View.INVISIBLE);
					tournament_view.setVisibility(View.INVISIBLE);
					team_view.setVisibility(View.INVISIBLE);
					me_view.setVisibility(View.VISIBLE);
					if(currSelected != ME)
						selectIcon(ME);
				}
			}
		});
		
		((Button)findViewById(R.id.edit_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewsActivity.this, MyProfile.class);
				startActivity(intent);
			}
	    });

		
        slide_screen.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position){
                invalidateOptionsMenu();
            }
        });
		logout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				if(global.fb_flag && Session.getActiveSession() != null){
					Session.getActiveSession().closeAndClearTokenInformation();
                	LogInActivity.fb_button.setClickable(true);
				}
				global.mode = global.select_mode;
				try{
					InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
				}
				catch(IOException e1){
					post_toast("Write fail.");
					return;
				}
				global.token = "";
				try{
					InternalStorage.writeObject(getApplicationContext(), "TOKEN", global.token);
				}
				catch(IOException e1){
					post_toast("Write fail.");
					return;
				}
				global.icon_url = "";
				try{
					InternalStorage.writeObject(getApplicationContext(), "ICON_URL", global.icon_url);
				}
				catch(IOException e1){
					post_toast("Write fail.");
					return;
				}
				global.team_icon_url = "";
				global.user_json = null;
				global.team_json = null;
				global.out_loop = true;
				global.other_captain_id = "";
				global.start_game = 0;
				global.tournament_code_sent = false;
				global.check_status = 0;
				//news_list = null;
				//slides = null;
				//news_view.removeAllViews();
				//Intent intent = new Intent(NewsActivity.this, LogInActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//intent.putExtra("EXIT", true);
				startActivity(intent);
				//overridePendingTransition(0, 0);
				finish();
				Log.e("token", global.token + "!");
			}
		});
/*
		notfication.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				sliding_menu.toggle();
			}
		});
*/

		find_teammate_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(NewsActivity.this, FindTeammate.class);
				startActivity(intent);
			}
		});
		dog.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent e) {
				if(tempBitmap == null){
					dog.buildDrawingCache();
					tempBitmap = dog.getDrawingCache();
					tempBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
				}
				//Bitmap tempBitmap = Bitmap.createBitmap(dog.getWidth(), dog.getHeight(), Bitmap.Config.RGB_565);
				//e.getAction()
				Canvas tempCanvas = new Canvas(tempBitmap);

				//Draw the image bitmap into the cavas
				//tempCanvas.drawBitmap(tempBitmap, 0, 0, null);
				Paint paint = new Paint();

				paint.setColor(Color.CYAN);
				paint.setStyle(Paint.Style.FILL);
				//Draw everything else you want into the canvas, in this example a rectangle with rounded edges
				tempCanvas.drawRoundRect(new RectF(e.getX(), e.getY(), e.getX()+50, e.getY()+50), 25, 25, paint);
				//Log.e("here", mutableBitmap.getWidth()+" "+mutableBitmap.getHeight());
				//Log.e("here", e.getX()+" "+e.getY());
				//Attach the canvas to the ImageView
				dog.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
				return true;
			}
		});
		((TextView)findViewById(R.id.change)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
			}
		});
	}
	
	public void selectIcon(int idx)
	{
		if(idx<0 || idx>=bar.length)
		{
			post_toast("Button bar index is out of range!");
			return;
		}
		switchIcon(idx, 0);
		if(currSelected != -1)
			switchIcon(currSelected, 1);
		currSelected = idx;
	}
	
	public static void alert_dialog(String message){
    	if(this_context != null){
            new SweetAlertDialog(this_context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Hurrah!")
            .setContentText(message)
            .show();
			return;
    	}
	}
	
	@SuppressLint("NewApi")
	private void switchIcon(int idx, int selected)
	{
		bar[idx].setCompoundDrawablesWithIntrinsicBounds(0, icon_ids[idx][selected], 0, 0);

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
	    if(keyCode == KeyEvent.KEYCODE_BACK){
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setContentView(int id){
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(View v){
		setContentView(v, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		mHelper.registerAboveContentView(v, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}
	
	@Override
	public View findViewById(int id){
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public void setBehindContentView(View view,
			android.view.ViewGroup.LayoutParams layoutParams){
		mHelper.setBehindContentView(view, layoutParams);
	}

	@Override
	public void setBehindContentView(View view){
		setBehindContentView(view, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setBehindContentView(int layoutResID){
		setBehindContentView(getLayoutInflater().inflate(layoutResID, null));
	}

	@Override
	public SlidingMenu getSlidingMenu(){
		return mHelper.getSlidingMenu();
	}

	@Override
	public void toggle(){
		mHelper.toggle();
	}

	@Override
	public void showContent(){
		mHelper.showContent();
	}

	@Override
	public void showMenu(){
		mHelper.showMenu();
	}

	@Override
	public void showSecondaryMenu(){
		mHelper.showSecondaryMenu();
	}

	@Override
	public void setSlidingActionBarEnabled(boolean slidingActionBarEnabled){
		mHelper.setSlidingActionBarEnabled(slidingActionBarEnabled);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b) return b;
		return super.onKeyUp(keyCode, event);
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
