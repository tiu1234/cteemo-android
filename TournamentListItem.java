package com.example.duang;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TournamentListItem extends LinearLayout {

	private Context context;
	private Context this_context;
//	private ImageView icon;
	private LinearLayout info;
	private TournamentTableLayout table;
	private TextView title;
	private Button signup;
	private JSONObject tournament;
	public String url;
	public String image_url;
	public String name;
//	private View bar;
	
	public static final int LEAGUE = 0;
	public static final int HEARTHSTONE = 1;
	public static final int DOTA = 2;
	
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
	
	public TournamentListItem(Context context, JSONObject tournament, Context this_context) {
		super(context);
		this.context = context;
		this.tournament = tournament;
		this.this_context = this_context;
		init();
	}

	public TournamentListItem(Context context, AttributeSet attrs, JSONObject tournament, Context this_context) {
		super(context, attrs);
		this.context = context;
		this.tournament = tournament;
		this.this_context = this_context;
		init();
	}
	
	@SuppressLint("NewApi")
	private void init()
	{
		image_url = "";
		url = "";
		name = "Tournament Name";
		String date = "NO DATE";
		String time = "NO TIME";
		String type = "MODE";
		int curr = 0;
		int max = 0;
		if(tournament != null){
			JSONObject tour = null;
			try{
				tour = (JSONObject)tournament.getJSONObject("tournament");
				Log.e("tournament", tour.toString());
				if(!tour.isNull("url")){
					Log.e("tournament", url);
					url = (String)tour.get("url");
					Log.e("tournament", url);
				}
				if(!tour.isNull("name")){
					name = (String)tour.get("name");
				}
				if(!tour.isNull("tournament_type")){
					type = (String)tour.get("tournament_type");
				}
				if(!tour.isNull("participants_count")){
					curr = (int)tour.get("participants_count");
				}
				max = 256;
				if(!tour.isNull("start_at")){
					String date_and_time = "";
					date_and_time = (String)tour.get("start_at");
					String[] dateandtime = date_and_time.split("T", 2);
					date = dateandtime[0];
					time = dateandtime[1];
					String zone;
					Log.e("time", time);
					if(time.contains("+")){
						String[] time_zone = time.split("\\+", 2);
						String real_time = time_zone[0];
						if(real_time.contains(".")){
							real_time = real_time.split("\\.", 2)[0];
						}
						zone = time_zone[1];
						zone = zone.split("\\:", 2)[0];
						int hour = Integer.valueOf(real_time.split("\\:", 2)[0]);
						hour += 24-Integer.valueOf(zone)-5;
						hour %= 24;
						String rest = real_time.split("\\:", 2)[1];
						time = Integer.toString(hour)+":"+rest;
					}
					else if(time.contains("-")){
						String[] time_zone = time.split("\\-", 2);
						String real_time = time_zone[0];
						if(real_time.contains(".")){
							real_time = real_time.split("\\.", 2)[0];
						}
						zone = time_zone[1];
						zone = zone.split("\\:", 2)[0];
						int hour = Integer.valueOf(real_time.split("\\:", 2)[0]);
						hour += 24-(5-Integer.valueOf(zone));
						hour %= 24;
						String rest = real_time.split("\\:", 2)[1];
						time = Integer.toString(hour)+":"+rest;
					}
				}
				else{
					date = "NO DATE";
					time = "NO TIME";
				}
				image_url = "http://challonge.com/"+url+"/module?theme=100&multiplier=0.9&match_width_multiplier=1.2.png";
			}
			catch(JSONException e){
			}
		}

//		icon = new ImageView(context);
		info = new LinearLayout(context);
		table = new TournamentTableLayout(context, url, date, time, type, curr, max);
		title = new TextView(context);
		signup = new Button(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		params.gravity = Gravity.BOTTOM;
		
		title.setTextColor(Color.BLACK);
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		signup.setTextColor(Color.WHITE);
		signup.setGravity(Gravity.CENTER);
		signup.setPadding(8, 8, 8, 8);
		
//		setIcon(LEAGUE);
		setTitle(name);
		setFee("FREE");
		
		info.setOrientation(LinearLayout.VERTICAL);
		info.addView(title);
		info.addView(table);
		
		
		int sdk = Build.VERSION.SDK_INT;
		if(sdk < Build.VERSION_CODES.JELLY_BEAN)
			signup.setBackgroundDrawable(getResources().getDrawable(R.drawable.beveled_red));
		else
			signup.setBackground(getResources().getDrawable(R.drawable.beveled_red));		
		
//		addView(icon, params);
		addView(info, params);
		if(curr < max)
		{
			params.setMargins(32, 8, 32, 0);
			addView(signup, params);
		}
	}
	
/*
	public void setIcon(int game){
		if(game == LEAGUE)
			icon.setImageDrawable(getResources().getDrawable(R.drawable.lol));
	}
*/
	
	public void setTitle(String title){
		if(title == null)
			return;
		this.title.setText(title);
	}
	
	public void setFee(String fee){
		if(fee == null)
			return;
		if(fee.length() > 5)
		{
			float scale_factor = 5 / (float)fee.length();
			signup.setTextScaleX(scale_factor);
		}
		signup.setText(fee);
	}
	
	public View getSignup(){
		return signup;
	}

	public TournamentTableLayout getTable()
	{
		return table;
	}
}
