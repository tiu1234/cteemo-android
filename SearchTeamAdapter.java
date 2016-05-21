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
public class SearchTeamAdapter extends ArrayAdapter<JSONObject>{
	private final Context context;
	private JSONObject[] jsons;
	private Context this_context;
	private Bitmap[] bitmap_temp;
	
	public SearchTeamAdapter(Context context, JSONObject[] jsons, Context this_context){
		super(context, R.layout.search_team_list, jsons);
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
			Asynhttp get_team_icon = new Asynhttp(this_context, global.download_profile_icon);
			get_team_icon.execute(url);
			try{
				bitmap_temp[position] = (Bitmap)get_team_icon.get();
				icon.setImageBitmap(bitmap_temp[position]);
			}
			catch(InterruptedException e){
				SearchTeam.post_toast("Downloading icon failure.");
			}
			catch(ExecutionException e){
				SearchTeam.post_toast("Downloading icon failure.");
			}
		}
		else{
			icon.setImageBitmap(bitmap_temp[position]);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View team_view = inflater.inflate(R.layout.search_team_list, parent, false);
			TextView search_team_name = (TextView)team_view.findViewById(R.id.search_team_name);
			TextView search_team_captain_name = (TextView)team_view.findViewById(R.id.search_team_captain_name);
			TextView search_team_school_name = (TextView)team_view.findViewById(R.id.search_team_school_name);
			ImageView search_team_add = (ImageView) team_view.findViewById(R.id.search_team_add);
			ImageView search_team_icon = (ImageView) team_view.findViewById(R.id.search_team_icon);
			JSONObject json = jsons[position];
			try{
				final String team_name = (String)json.get("teamName");
				search_team_name.setText(team_name);
				if(!json.isNull("captain")){
					search_team_captain_name.setText((String)json.get("captain"));
				}
				if(!json.isNull("school")){
					search_team_school_name.setText((String)json.get("school"));
				}
				if(!json.isNull("teamIcon")){
					update_icon((String)json.get("teamIcon"), search_team_icon, position);
				}
				search_team_add.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0){
						Asynhttp join_team = new Asynhttp(this_context, global.join_team);
						join_team.execute(global.token, team_name);
					}
				});
			}
			catch(JSONException e){
				SearchTeam.post_toast("Search team information JSONException.");
			}
			return team_view;
		//}
		//else{
		//	return convertView;
		//}
	}
}
