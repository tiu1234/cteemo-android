package com.example.duang;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.example.duang.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SummonerInfo extends Activity {

    private Context context;
    private TextView summoner_school;
    private TextView summoner_rank;
    private TextView summoner_level;
    private TextView summoner_start;
    private TextView summoner_change;
    private ImageView summoner_icon;

	private MediaPlayer bgm_player;
    
    private void post_toast(String message){
    	if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
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
    
	private void update_icon(String url, ImageView icon){
		Asynhttp get_summoner_icon = new Asynhttp(context, global.download_profile_icon);
		get_summoner_icon.execute(url);
		try{
		    Bitmap bitmap = (Bitmap)get_summoner_icon.get();
		    bitmap = getCircularBitmapWithWhiteBorder(bitmap, 0);
			icon.setImageBitmap(bitmap);
		}
		catch(InterruptedException e){
			post_toast("Downloading icon failure.");
		}
		catch(ExecutionException e){
			post_toast("Downloading icon failure.");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(bgm_player == null){
			bgm_player = MediaPlayer.create(this, R.raw.welcome);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        if (bgm_player != null && bgm_player.isPlaying()) {
        	bgm_player.stop();
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		bgm_player = null;
		bgm_player = MediaPlayer.create(this, R.raw.welcome);
        context = getApplicationContext();
		setContentView(R.layout.summoner_info);
		summoner_school = (TextView)findViewById(R.id.summoner_school);
		summoner_rank = (TextView)findViewById(R.id.summoner_rank);
		summoner_level = (TextView)findViewById(R.id.summoner_level);
		summoner_start = (TextView)findViewById(R.id.summoner_start);
		summoner_change = (TextView)findViewById(R.id.summoner_change);
		summoner_icon = (ImageView)findViewById(R.id.summoner_icon);
		
		summoner_school.setText(global.summonername+"");
		summoner_rank.setText(global.solo_tier+"");
		summoner_level.setText(global.summonerlevel+"");
		summoner_start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				global.mode = global.logged_in_mode;
				try{
					LogInActivity.InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
				}
				catch(IOException e1){
					post_toast("Write fail.");
					return;
				}
				Intent intent = new Intent(SummonerInfo.this, NewsActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//intent.putExtra("EXIT", true);
				startActivity(intent);
				//overridePendingTransition(0, 0);
				finish();
				return;
			}
		});
		summoner_change.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SummonerInfo.this, LogInActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//intent.putExtra("EXIT", true);
				startActivity(intent);
				//overridePendingTransition(0, 0);
				finish();
				return;
			}
		});
		try{
			update_icon((String)global.user_json.get("profile_icon"), summoner_icon);
		}
		catch(JSONException e){
			post_toast("Downloading icon failure.");
		}
		/*
		new ParticleSystem(this, 4, R.drawable.white_dust, 300000)
		.setSpeedByComponentsRange(0.004f, 0.005f, -0.005f, -0.004f)		
		.setAcceleration(0.0f, 300)
		.setInitialRotationRange(0, 360)
		.addModifier(new ScaleModifier(1.0f, 1.2f, 0, 0))
		.addModifier(new AlphaModifier(50, 200, 0, 3000))
		.oneShot(findViewById(R.id.emiter_bottom), 4);
		*/
		Asynsummonerinit init = new Asynsummonerinit(this, bgm_player);
		init.execute();
	}
}
