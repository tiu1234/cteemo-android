package com.example.duang;

import com.example.duang.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TournamentTableLayout extends TableLayout {
	
	public static final int MODE = 0;
	public static final int TIME = 1;
	public static final int TEAMS = 2;

	
	private TextView mode_view, time_view, curr_teams_view, max_teams_view;
	private TableLayout teams_view;

	private String url = "";
	private String date = "NO DATE";
	private String time = "NO TIME";
	private String type = "MODE";
	private int curr = 0;
	private int max = 0;


	private Context context;
	
	public TournamentTableLayout(Context context, String url, String date, String time, String type, int curr, int max){
		super(context);
		this.context = context;
		this.url = url;
		this.date = date;
		this.time = time;
		this.type = type;
		this.curr = curr;
		this.max = max;
		
		Log.e("url", url);
		Log.e("date", date);
		Log.e("time", time);
		Log.e("type", type);
		Log.e("curr", "" + curr);
		Log.e("max", "" + max);
		
		init();
	}

	public TournamentTableLayout(Context context, String url, AttributeSet attrs, String date, String time, String type, int curr, int max) {
		super(context, attrs);
		this.context = context;
		this.url = url;
		this.date = date;
		this.time = time;
		this.type = type;
		this.curr = curr;
		this.max = max;
		init();
	}
	
	
	
	@SuppressLint("NewApi")
	private void init()
	{
		addView(initRow(initCell(type+"\n5 V 5", MODE), initCell(date+"\n"+time, TIME),
				initTeamsCell(""+curr, ""+max)));
		
		setGravity(Gravity.CENTER);
        
        int sdk = Build.VERSION.SDK_INT;
		if(sdk < Build.VERSION_CODES.JELLY_BEAN)
			setBackgroundDrawable(getResources().getDrawable(R.drawable.beveled_white));
		else
			setBackground(getResources().getDrawable(R.drawable.beveled_white));

        setShrinkAllColumns(true);
    }
	
	private TableRow initRow(View... cells)
	{
		TableRow row = new TableRow(context);
		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.gravity = Gravity.CENTER;
		
		int size = cells.length > 2 ? 3 : cells.length;
		for(int i=0; i<size; i++)
		{
			row.addView(cells[i], params);
		}
			
		row.setShowDividers(TableRow.SHOW_DIVIDER_MIDDLE);
        row.setDividerDrawable(getResources().getDrawable(R.drawable.black_bar));
		
		return row;
	}
	
	private TextView initCell(String text, int entry)
	{
		TextView v = new TextView(context);	
		v.setTextColor(Color.BLACK);
		v.setText(text);
		v.setGravity(Gravity.CENTER);
		v.setPadding(4, 4, 4, 4);
		
		if(entry == MODE)
			mode_view = v;
		else if(entry == TIME)
			time_view = v;		

		return v;
	}
	
	private TableLayout initTeamsCell(String curr_num, String max_num)
	{
		TableLayout teams = new TableLayout(context);
		TableRow curr_row = new TableRow(context);
		curr_teams_view = new TextView(context);
		TableRow max_row = new TableRow(context);
		max_teams_view = new TextView(context);
		
		curr_teams_view.setPadding(40, 8, 16, 8);
		max_teams_view.setPadding(16, 8, 40, 8);
		
		formatAndSetTeamNums(curr_num, max_num);

		curr_row.addView(curr_teams_view);
		max_row.addView(max_teams_view);
		teams.addView(curr_row);
		teams.addView(max_row);
		
		teams.setDividerDrawable(getResources().getDrawable(R.drawable.black_bar));
		teams.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
		teams.setColumnStretchable(0, true);
		
		teams.setRotation(-45);
		curr_teams_view.setRotation(45);
		max_teams_view.setRotation(45);
		
		teams_view = teams;
		return teams;
	}	
	
	private void formatAndSetTeamNums(String curr_num, String max_num)
	{
		int num_curr_num = Integer.parseInt(curr_num);
		int num_max_num = Integer.parseInt(max_num);
		boolean full = num_curr_num >= num_max_num;
		
		curr_teams_view.setTextColor(full ? Color.RED : Color.BLUE);
		curr_teams_view.setText(curr_num);
		float num_scale_factor = 2 / (float)curr_num.length();
		curr_teams_view.setTextScaleX(num_scale_factor);

		max_teams_view.setTextColor(full ? Color.RED : Color.BLUE);
		max_teams_view.setText(max_num);
		float max_scale_factor = 2 / (float)max_num.length();
		max_teams_view.setTextScaleX(max_scale_factor);
	}
	
	public void setModeAndTeamSize(String mode, String team_size)
	{
		setEntry(mode+"\n"+team_size, MODE);
	}
	
	public void setMode(String mode)
	{
		setEntry(mode+"\n"+getModeAndTeamSize()[1], MODE);
	}
	
	public void setTeamSize(String team_size)
	{
		setEntry(getModeAndTeamSize()[0]+"\n"+team_size, MODE);
	}
	
	public void setDateAndTime(String date, String time)
	{
		setEntry(date+"\n"+time, TIME);
	}
	
	public void setDate(String date)
	{
		setEntry(date+"\n"+getDateAndTime()[1], TIME);
	}
	
	public void setTime(String time)
	{
		setEntry(getDateAndTime()[0]+"\n"+time, TIME);
	}
	
	public void setTeamNums(String curr_num, String max_num)
	{
		setEntry(curr_num+' '+max_num, TEAMS);
	}

	public void setCurrTeamNum(String curr_num)
	{
		setEntry(curr_num+' '+ getTeamNums()[1], TEAMS);
	}
	
	public void setMaxTeamNum(String max_num)
	{
		setEntry(getTeamNums()[0]+' '+max_num, TEAMS);
	}
	
	
	private void setEntry(String text, int entry)
	{
		switch(entry){
			case MODE:
				mode_view.setText(text);
				break;
			case TIME:
				time_view.setText(text);
				break;
			case TEAMS:
				String curr_num = text.substring(0, text.indexOf(' '));
				String max_num = text.substring(text.indexOf(' ')+1);
				formatAndSetTeamNums(curr_num, max_num);
				break;
		}
				
	}
	
	public String[] getModeAndTeamSize()
	{
		String text = mode_view.getText().toString();
		String[] arr = {text.substring(0, text.indexOf("\n")), text.substring(text.indexOf("\n")+1)};
		return arr;
	}
	
	public String[] getDateAndTime()
	{
		String text = time_view.getText().toString();
		String[] arr = {text.substring(0, text.indexOf("\n")), text.substring(text.indexOf("\n")+1)};
		return arr;
	}
	
	public String[] getTeamNums()
	{		
		return new String[]{curr_teams_view.getText().toString(), max_teams_view.getText().toString()};
	}
}