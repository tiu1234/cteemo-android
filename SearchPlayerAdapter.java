package com.example.duang;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class SearchPlayerAdapter extends ArrayAdapter<JSONObject>{
	private final Context context;
	private JSONObject[] jsons;
	private Context this_context;
	private Bitmap[] bitmap_temp;
	
	public SearchPlayerAdapter(Context context, JSONObject[] jsons, Context this_context){
		super(context, R.layout.search_player_list, jsons);
		this.context = context;
		this.jsons = jsons;
		this.this_context = this_context;
		bitmap_temp = new Bitmap[jsons.length];
		for(int i = 0 ; i < jsons.length; i++){
			bitmap_temp[i] = null;
		}
	}
	
	public void setListData(JSONObject[] data){
		jsons = data;
	}
  
	private void update_icon(String url, ImageView icon, int position){
		if(bitmap_temp[position] == null){
			Asynhttp get_player_icon = new Asynhttp(this_context, global.download_profile_icon);
			get_player_icon.execute(url);
			try{
				bitmap_temp[position] = (Bitmap)get_player_icon.get();
				icon.setImageBitmap(bitmap_temp[position]);
			}
			catch(InterruptedException e){
				SearchPlayer.post_toast("Downloading profile icon failure.");
			}
			catch(ExecutionException e){
				SearchPlayer.post_toast("Downloading profile icon failure.");
			}
		}
		else{
			icon.setImageBitmap(bitmap_temp[position]);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//if(counter <= position){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View player_view = inflater.inflate(R.layout.search_player_list, parent, false);
			TextView search_player_name = (TextView)player_view.findViewById(R.id.search_player_name);
			TextView search_player_school_name = (TextView)player_view.findViewById(R.id.search_player_school_name);
			ImageView search_player_add = (ImageView) player_view.findViewById(R.id.search_player_add);
			ImageView search_player_icon = (ImageView) player_view.findViewById(R.id.search_player_icon);
			JSONObject json = jsons[position];
			//Log.e("position", Integer.toString(position));
			//counter++;
			try{
				final String player_name = (String)json.get("username");
				final String profileID = (String)json.get("profile_id");
				search_player_name.setText(player_name);
				if(!json.isNull("school")){
					search_player_school_name.setText((String)json.get("school"));
				}
				if(!json.isNull("profile_icon")){
					update_icon((String)json.get("profile_icon"), search_player_icon, position);
				}
				search_player_add.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0){
						Asynhttp add_player = new Asynhttp(this_context, global.add_player);
						add_player.execute(global.token, profileID);
						try{
							SearchPlayer.post_toast((String)add_player.get());
						}
						catch(InterruptedException | ExecutionException e){
							
						}
					}
				});
			}
			catch(JSONException e){
				SearchPlayer.post_toast("Search player information JSONException.");
			}
			return player_view;
		//}
		//else{
		//	return convertView;
		//}
	}
}
