package com.example.duang;

import com.example.duang.R;
import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.AlphaModifier;
import com.plattysoft.leonids.modifiers.ScaleModifier;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;


public class Asynsummonerinit extends AsyncTask<Void, Void, Void>{
	private Activity temp;
	
	private MediaPlayer bgm_player;
	private int type;
	
	Asynsummonerinit(Activity temp, MediaPlayer bgm_player){
		this.temp = temp;
		this.bgm_player = bgm_player;
		type = 0;
	}
	
	@Override
	protected void onProgressUpdate(Void...params){
		if(type == 0){
			new ParticleSystem(temp, 1, R.drawable.white_dust, 300000)
			.setSpeedByComponentsRange(-0.015f, -0.014f, -0.005f, -0.004f)		
			.setAcceleration(0.0f, 300)
			.setInitialRotationRange(0, 360)
			.addModifier(new ScaleModifier(1.0f, 1.3f, 0, 0))
			.addModifier(new AlphaModifier(50, 200, 0, 3000))
			.emit(global.width/6, global.height/2-10, 1, 30);
			new ParticleSystem(temp, 1, R.drawable.white_dust, 300000)
			.setSpeedByComponentsRange(-0.015f, -0.014f, -0.003f, -0.002f)		
			.setAcceleration(0.0f, 300)
			.setInitialRotationRange(0, 360)
			.addModifier(new ScaleModifier(1.0f, 1.3f, 0, 0))
			.addModifier(new AlphaModifier(50, 200, 0, 3000))
			.emit(global.width/6, global.height/2-10, 1, 30);
			new ParticleSystem(temp, 1, R.drawable.white_dust, 300000)
			.setSpeedByComponentsRange(0.014f, 0.015f, -0.005f, -0.004f)		
			.setAcceleration(0.0f, 300)
			.setInitialRotationRange(0, 360)
			.addModifier(new ScaleModifier(1.0f, 1.3f, 0, 0))
			.addModifier(new AlphaModifier(50, 200, 0, 3000))
			.emit((global.width/6)*5, global.height/2-10, 1, 30);
			new ParticleSystem(temp, 1, R.drawable.white_dust, 300000)
			.setSpeedByComponentsRange(0.018f, 0.019f, 0.003f, 0.002f)		
			.setAcceleration(0.0f, 300)
			.setInitialRotationRange(0, 360)
			.addModifier(new ScaleModifier(1.0f, 1.3f, 0, 0))
			.addModifier(new AlphaModifier(50, 200, 0, 3000))
			.emit((global.width/6)*5, global.height/2-10, 1, 30);
			new ParticleSystem(temp, 1, R.drawable.white_dust, 300000)
			.setSpeedByComponentsRange(-0.015f, -0.014f, 0.004f, 0.005f)		
			.setAcceleration(0.0f, 300)
			.setInitialRotationRange(0, 360)
			.addModifier(new ScaleModifier(1.0f, 1.3f, 0, 0))
			.addModifier(new AlphaModifier(50, 200, 0, 3000))
			.emit(global.width/6, global.height/2+10, 1, 30);
			new ParticleSystem(temp, 1, R.drawable.white_dust, 300000)
			.setSpeedByComponentsRange(0.014f, 0.015f, 0.004f, 0.005f)		
			.setAcceleration(0.0f, 300)
			.setInitialRotationRange(0, 360)
			.addModifier(new ScaleModifier(1.0f, 1.3f, 0, 0))
			.addModifier(new AlphaModifier(50, 200, 0, 3000))
			.emit((global.width/6)*5, global.height/2+10, 1, 30);
		}
		else{
			if(!bgm_player.isPlaying()){
				bgm_player.start();
			}
		}
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try{
			Thread.sleep(200);
		}
		catch(InterruptedException e){
			
		}
		publishProgress();
		try{
			Thread.sleep(2000);
		}
		catch(InterruptedException e){
			
		}
		type = 1;
		publishProgress();
		return null;
	}

}
