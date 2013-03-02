package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.banzz.lifecounter.R;

public class ActionPickDemo extends Activity {

    protected static final int REQUEST_PICK_IMAGE = 1;
    protected static final int REQUEST_PICK_CROP_IMAGE = 2;
    private Button mPickImageButton;
    private Button mPickCropImageButton;
    private Button mSaveImageButton;
    private ImageView mImageViewLarge;
    private ImageView mImageViewTall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.action_pick_demo);

           mImageViewLarge = (ImageView) findViewById(R.id.image_large);
           mImageViewTall = (ImageView) findViewById(R.id.image_tall);
           mPickImageButton = (Button) findViewById(R.id.pick_image_button);
           mPickCropImageButton = (Button) findViewById(R.id.pick_crop_image_button);
           mSaveImageButton = (Button) findViewById(R.id.save_image);
           mPickImageButton.setOnClickListener(mButtonListener);
           mPickCropImageButton.setOnClickListener(mButtonListener);
           mSaveImageButton.setOnClickListener(mButtonListener);
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {

           @Override
           public void onClick(View v) {
        	   String status = Environment.getExternalStorageState();
        	   
        	   switch (v.getId()) {

                  case R.id.pick_image_button:
                	  if (status.equals(Environment.MEDIA_MOUNTED)) {
                             File tempFile = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
                             Uri tempUri = Uri.fromFile(tempFile);
                             Intent pickCropImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                             pickCropImageIntent.setType("image/*");
                             pickCropImageIntent.putExtra("crop", "true");
                             pickCropImageIntent.putExtra("scale", true);
                             Display display = getWindowManager().getDefaultDisplay();
                             Point size = new Point();
                             display.getSize(size);
                             int width = size.x;
                             int height = size.y;
                             pickCropImageIntent.putExtra("outputX", width);
                             pickCropImageIntent.putExtra("outputY", height/2);
                             pickCropImageIntent.putExtra("aspectX", width);
                             pickCropImageIntent.putExtra("aspectY", height/2);
                             pickCropImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                           tempUri);
                              pickCropImageIntent.putExtra("outputFormat",
                                           Bitmap.CompressFormat.JPEG.toString());
                             startActivityForResult(pickCropImageIntent,
                                           REQUEST_PICK_CROP_IMAGE);
                      }else{
                            
                      }
                      break;
                  case R.id.pick_crop_image_button:
                        if (status.equals(Environment.MEDIA_MOUNTED)) {
                               File tempFile = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
                               Uri tempUri = Uri.fromFile(tempFile);
                               Intent pickCropImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                               pickCropImageIntent.setType("image/*");
                               pickCropImageIntent.putExtra("crop", "true");
                               pickCropImageIntent.putExtra("scale", true);
                               Display display = getWindowManager().getDefaultDisplay();
                               Point size = new Point();
                               display.getSize(size);
                               int width = size.x;
                               int height = size.y;
                               pickCropImageIntent.putExtra("outputX", width/2);
                               pickCropImageIntent.putExtra("outputY", height);
                               pickCropImageIntent.putExtra("aspectX", width/2);
                               pickCropImageIntent.putExtra("aspectY", height);
                               pickCropImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                             tempUri);
                                pickCropImageIntent.putExtra("outputFormat",
                                             Bitmap.CompressFormat.JPEG.toString());
                               startActivityForResult(pickCropImageIntent,
                                             REQUEST_PICK_CROP_IMAGE);
                        }else{
                              
                        }
                        break;
                  case R.id.save_image:
				try {
					MediaStore.Images.Media.insertImage(getContentResolver(), Environment.getExternalStorageDirectory() + "/temp.jpg", "LargeTest" , "Alriiiiiiight");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                	  
                	  /*File sdCardDirectory = Environment.getExternalStorageDirectory();
                	  //Bitmap selectedImage = BitmapFactory.decodeFile(Environment
                      //        .getExternalStorageDirectory() + "/temp.jpg");
                	  File image = new File(sdCardDirectory, "test.png");

                	      boolean success = false;

                	      // Encode the file as a PNG image.
                	      FileOutputStream outStream;
                	      try {

                	          outStream = new FileOutputStream(image);
                	          croppedImage.compress(Bitmap.CompressFormat.PNG, 100, outStream); 
                	          // 100 to keep full quality of the image

                	          outStream.flush();
                	          outStream.close();
                	          success = true;
                	      } catch (FileNotFoundException e) {
                	          e.printStackTrace();
                	      } catch (IOException e) {
                	          e.printStackTrace();
                	      }*/
                	  break;
                  }
           }
    };

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
           switch (requestCode) {
	           case REQUEST_PICK_CROP_IMAGE:
                  Bitmap croppedImage = BitmapFactory.decodeFile(Environment
                               .getExternalStorageDirectory() + "/temp.jpg");
                  mImageViewTall.setImageBitmap(croppedImage);
                  break;
           }
    }
}