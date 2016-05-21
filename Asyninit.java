package com.example.duang;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class Asyninit extends AsyncTask<Void, Void, Void>{
	private LinearLayout init_layout;
	private RelativeLayout top_layout;
	private LinearLayout select_layout;
	private LinearLayout signup_layout;
	private LinearLayout login_layout;
	private LinearLayout profile_layout1;
	private LinearLayout profile_layout2;
	private ImageView right_arrow;
	private int mode;
	
	Asyninit(int mode, String token, LinearLayout select_layout, LinearLayout signup_layout, LinearLayout login_layout, LinearLayout init_layout, RelativeLayout top_layout, LinearLayout profile_layout1, LinearLayout profile_layout2, ImageView right_arrow){
		this.init_layout = init_layout;
		this.top_layout = top_layout;
		this.select_layout = select_layout;
		this.signup_layout = signup_layout;
		this.login_layout = login_layout;
		this.profile_layout1 = profile_layout1;
		this.profile_layout2 = profile_layout2;
		this.right_arrow = right_arrow;
		this.mode = mode;
	}
	
	@Override
	protected void onProgressUpdate(Void...params){
		init_layout.setVisibility(View.INVISIBLE);
		switch(mode){
			case global.select_mode:
				top_layout.setVisibility(View.INVISIBLE);
				select_layout.setVisibility(View.VISIBLE);
				signup_layout.setVisibility(View.INVISIBLE);
				login_layout.setVisibility(View.INVISIBLE);
				profile_layout1.setVisibility(View.INVISIBLE);
				profile_layout2.setVisibility(View.INVISIBLE);
				break;
			case global.signup_mode:
				top_layout.setVisibility(View.VISIBLE);
				select_layout.setVisibility(View.INVISIBLE);
				signup_layout.setVisibility(View.VISIBLE);
				login_layout.setVisibility(View.INVISIBLE);
				profile_layout1.setVisibility(View.INVISIBLE);
				profile_layout2.setVisibility(View.INVISIBLE);
				break;
			case global.logged_in_mode:	//case global.login_mode:
				top_layout.setVisibility(View.VISIBLE);
				right_arrow.setVisibility(View.INVISIBLE);
				select_layout.setVisibility(View.INVISIBLE);
				signup_layout.setVisibility(View.INVISIBLE);
				login_layout.setVisibility(View.VISIBLE);
				profile_layout1.setVisibility(View.INVISIBLE);
				profile_layout2.setVisibility(View.INVISIBLE);
				break;
			case global.profile_mode:
				top_layout.setVisibility(View.VISIBLE);
				select_layout.setVisibility(View.INVISIBLE);
				signup_layout.setVisibility(View.INVISIBLE);
				login_layout.setVisibility(View.INVISIBLE);
				profile_layout1.setVisibility(View.VISIBLE);
				profile_layout2.setVisibility(View.INVISIBLE);
				break;
			/*
			case global.profile_mode1:
				top_layout.setVisibility(View.VISIBLE);
				select_layout.setVisibility(View.INVISIBLE);
				signup_layout.setVisibility(View.INVISIBLE);
				login_layout.setVisibility(View.INVISIBLE);
				profile_layout1.setVisibility(View.VISIBLE);
				profile_layout2.setVisibility(View.INVISIBLE);
				break;
			case global.profile_mode2:
				top_layout.setVisibility(View.VISIBLE);
				select_layout.setVisibility(View.INVISIBLE);
				signup_layout.setVisibility(View.INVISIBLE);
				login_layout.setVisibility(View.INVISIBLE);
				profile_layout1.setVisibility(View.INVISIBLE);
				profile_layout2.setVisibility(View.VISIBLE);
				break;
			*/
		}
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try{
			Thread.sleep(2000);
		}
		catch(InterruptedException e){
			
		}
		publishProgress();
		return null;
	}

}
