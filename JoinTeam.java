package com.example.duang;

import com.example.duang.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class JoinTeam extends Activity{
	
	private Button postButton;
	private Button[] buttons;
	private int[][] icon_ids =
		{ {R.drawable.school_button_focused, R.drawable.school_button},
		{R.drawable.public_button_focused, R.drawable.public_button}
		};
	private int currSelected = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.join_team);
	    
	    buttons = new Button[2];
	    buttons[0] = (Button)findViewById(R.id.school_button);
	    buttons[1] = (Button)findViewById(R.id.public_button);
	    
	    buttons[0].setOnClickListener(new OnClickListener(){
	    	@Override
			public void onClick(View v)
	    	{
	    		Toast.makeText(getApplicationContext(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
	    		if(buttons[currSelected] != (Button)v)
					selectIcon(0);
	    	}
	    });
	    buttons[1].setOnClickListener(new OnClickListener(){
	    	@Override
			public void onClick(View v)
	    	{
	    		Toast.makeText(getApplicationContext(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
	    		if(buttons[currSelected] != (Button)v)
					selectIcon(1);
	    	}
	    });

	    postButton = (Button)findViewById(R.id.join_team_post_button);
	    postButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
			}
	    });
	    
	    ((Button)findViewById(R.id.search_team_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				Intent intent = new Intent(JoinTeam.this, SearchTeam.class);
				startActivity(intent);
			}
	    });
	}
	
	public void selectIcon(int idx)
	{
		if(idx<0 || idx>=buttons.length)
		{
			Toast.makeText(getApplicationContext(), "Button index is out of range!", Toast.LENGTH_SHORT).show();
			return;
		}
		switchIcon(idx, 0);
		switchIcon(currSelected, 1);
		currSelected = idx;
	}
	
	@SuppressLint("NewApi")
	public void switchIcon(int idx, int selected)
	{
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
		{
			buttons[idx].setBackgroundDrawable(getResources().getDrawable(icon_ids[idx][selected]));
		}
		else
		{
			buttons[idx].setBackground(getResources().getDrawable(icon_ids[idx][selected]));
		}
	}
	
}
