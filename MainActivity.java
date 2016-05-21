package com.example.duang;

import java.io.IOException;

import com.example.duang.R;
import com.example.duang.LogInActivity.InternalStorage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity{
    private static Context context;
    private ImageView init_back;
    
    private static void post_toast(String message){
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
		super.setContentView(R.layout.init);
		init_back = (ImageView)findViewById(R.id.init_back);
		init_back.setVisibility(View.VISIBLE);

        context = getApplicationContext();
		if(global.mode == global.init_mode){
			try{
				global.mode = (int)InternalStorage.readObject(getApplicationContext(), "MODE");
			}
			catch(IOException e){
				global.mode = global.select_mode;
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
		    new Thread(new Runnable(){
		        @Override
				public void run(){
		        	try{
						Thread.sleep(2000);
					}
		        	catch(InterruptedException e){
		        		
					}
					try{
						InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
					}
					catch(IOException e1){
						post_toast("Write fail.");
					}
			        if(global.mode >= global.init_mode && global.mode <= global.profile_mode){
						Intent intent = new Intent(MainActivity.this, LogInActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						//intent.putExtra("EXIT", true);
						startActivity(intent);
						//overridePendingTransition(0, 0);
						finish();
			        }
			        else if(global.mode >= global.logged_in_mode){
						Intent intent = new Intent(MainActivity.this, NewsActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						//intent.putExtra("EXIT", true);
						startActivity(intent);
						//overridePendingTransition(0, 0);
						finish();
			        }
		        }
		    }).start();
		}
		else{
	        if(global.mode >= global.init_mode && global.mode <= global.profile_mode){
				Intent intent = new Intent(MainActivity.this, LogInActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//intent.putExtra("EXIT", true);
				startActivity(intent);
				//overridePendingTransition(0, 0);
				finish();
	        }
	        else if(global.mode >= global.logged_in_mode){
				Intent intent = new Intent(MainActivity.this, NewsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//intent.putExtra("EXIT", true);
				startActivity(intent);
				//overridePendingTransition(0, 0);
				finish();
	        }
		}
	}
}
