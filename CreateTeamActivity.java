package com.example.duang;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateTeamActivity extends Activity{
    private static final int SELECT_PICTURE = 1;
	private ImageView create_back;
	private ImageView team_pick;
	private ImageView create_team;
	private EditText team_name;
	private EditText team_intro;
	private EditText team_school;
	//private CheckBox school_check;
    private String selectedImagePath;
    private Asynhttp create_team_http;
    private Asynhttp upload_team_icon;
    private Context context;
    
    private void post_toast(String message){
    	if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }
    
    private String getPath(Uri uri){
        if(uri == null){
            return null;
        }
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
            .getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                if(selectedImagePath != null){
    			    // Get the dimensions of the View
    			    int targetW = 300;
    			    int targetH = 300;

    			    // Get the dimensions of the bitmap
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
    			    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, bmOptions);
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
    			    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    			    team_pick.setImageBitmap(bitmap);
    			    //bitmap.recycle();
                }
            }
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_team);
        context = getApplicationContext();
		
		create_back = (ImageView)findViewById(R.id.create_back);
		team_pick = (ImageView)findViewById(R.id.team_pick);
		create_team = (ImageView)findViewById(R.id.create_team);
		team_name = (EditText)findViewById(R.id.team_name);
		team_intro = (EditText)findViewById(R.id.team_intro);
		team_school = (EditText)findViewById(R.id.team_school);
//		school_check = (CheckBox)findViewById(R.id.school_check);
		
		create_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				finish();
			}
		});
		team_pick.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
			}
		});
		create_team.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(create_team_http == null || create_team_http.getStatus() == AsyncTask.Status.PENDING || create_team_http.getStatus() == AsyncTask.Status.FINISHED){
					create_team_http = new Asynhttp(CreateTeamActivity.this, global.create_team);
					if(team_name.getText().toString() == null || team_name.getText().toString().equals("")){
						post_toast("Please enter a name.");
						return;
					}
					//create_team_http.execute(global.token, team_name.getText().toString(), String.valueOf(school_check.isChecked()), team_intro.getText().toString(), "UIUC");
					if(team_school.getText().toString() != null && !team_school.getText().toString().equals("")){
						create_team_http.execute(global.token, team_name.getText().toString(), String.valueOf(true), team_intro.getText().toString(), team_school.getText().toString());
					}
					else{
						create_team_http.execute(global.token, team_name.getText().toString(), String.valueOf(false), team_intro.getText().toString(), null);
					}
					try{
						String result = (String)create_team_http.get();
						if(result == null){
							return;
						}
						global.team_json = new JSONObject(result);
						//NewsActivity.alert_dialog(result);
						//return;
					}
					catch(InterruptedException e){
						post_toast("Team creating failure.");
					}
					catch(ExecutionException e){
						post_toast("Team creating failure.");
					}
					catch(JSONException e){
						post_toast("Team creating failure.");
					}
				}
				if(selectedImagePath != null && !selectedImagePath.equals("")){
					if(upload_team_icon == null || upload_team_icon.getStatus() == AsyncTask.Status.PENDING || upload_team_icon.getStatus() == AsyncTask.Status.FINISHED)
					upload_team_icon = new Asynhttp(CreateTeamActivity.this, global.team_icon);
					upload_team_icon.execute(global.token, selectedImagePath);
					try{
						global.team_json = (JSONObject)upload_team_icon.get();
						if(global.team_json == null){
							post_toast("Icon upload failure.");
							return;
						}
						global.team_icon_url = global.team_json.getString("teamIcon");
					}
					catch(InterruptedException | ExecutionException | JSONException e){
						post_toast("Icon upload failure.");
						return;
					}
					finish();
				}
				finish();
			}
		});
	}
}
