package com.example.duang;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;
import com.example.duang.LogInActivity.InternalStorage;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;



public class MyProfile extends Activity{
		
	protected static final int SELECT_PICTURE = 1;
	private LinearLayout my_icon_pick;
	private ImageView my_icon;
    private String selectedImagePath;
    private Context context;
	private Asynhttp login;


	@Override
	protected void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.my_profile);
	    context = getApplicationContext();
	    selectedImagePath = null;

	    
	    
	    my_icon_pick = (LinearLayout)findViewById(R.id.my_icon_pick);
	    my_icon = (ImageView)findViewById(R.id.my_icon);
	    
	    
	    my_icon_pick.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				/*
				Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                */
				Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //uiHelper.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                if(selectedImagePath != null){
    			    // Get the dimensions of the View
//    			    int targetW = 300;
//   			    int targetH = 300;
                	int targetW = my_icon.getWidth();
    			    int targetH = my_icon.getHeight();
    			    Log.e("w", String.valueOf(targetW));
    			    Log.e("h", String.valueOf(targetH));
    			    //my_icon.setPadding(0, 0, 0, 0);
    			    //my_icon_pick.setPadding(0, 0, 0, 0);
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
    			    bitmap = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
    			    bitmap = LogInActivity.getCircularBitmapWithWhiteBorder(bitmap, 0);
    			    my_icon.setPadding(0, 0, 0, 0);
    			    my_icon.setImageBitmap(bitmap);
    			   
    			    if(login == null || login.getStatus() == AsyncTask.Status.PENDING || login.getStatus() == AsyncTask.Status.FINISHED){
	    			    login = new Asynhttp(getApplicationContext(), global.profile_icon);
						login.execute(global.token, selectedImagePath);
						try{
							global.user_json = (JSONObject)login.get();
							if(global.user_json == null){
								post_toast("Fail to transmit icon.");
								return;
							}
							global.icon_url = (String)global.user_json.get("profile_icon");
							try{
								InternalStorage.writeObject(getApplicationContext(), "ICON_URL", global.icon_url);
							}
							catch(IOException e1){
								post_toast("Write fail.");
								return;
							}
							if(!global.icon_url.equals("")){
								//Asynhttp icon = new Asynhttp(getApplicationContext(), global.download_profile_icon);
								//icon.execute(icon_url);
								//Bitmap profile_icon = (Bitmap)icon.get();
								global.mode = global.logged_in_mode;
								try{
									InternalStorage.writeObject(getApplicationContext(), "MODE", global.mode);
								}
								catch(IOException e1){
									post_toast("Write fail.");
									return;
								}
							}
						}
						catch(InterruptedException e){
							post_toast("Fail to transmit icon.");
							return;
						}
						catch(ExecutionException e){
							post_toast("Fail to transmit icon.");
							return;
						}
						catch(JSONException e){
							post_toast("Fail to transmit icon.");
							return;
						}
    			    }

                
                }
                }
            }
        }
	/*
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
	*/
	private String getPath(Uri uri){
	    if(uri == null){
	        return null;
	    }
	    String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
	    if(cursor != null){
	        int column_index = cursor.getColumnIndexOrThrow(projection[0]);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }
	    return uri.getPath();
	}
	
	private void post_toast(String message){
		
		if(context != null){
			CharSequence text = message;
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}
}
