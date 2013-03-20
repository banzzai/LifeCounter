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
import com.banzz.lifecounter.utils.SingleMediaScanner;

public class PickImageActivity extends Activity {

    protected static final int REQUEST_PICK_IMAGE = 1;
    protected static final int REQUEST_PICK_CROP_IMAGE = 2;
    private Button mCropLargeButton;
    private Button mCropTallButton;
    private Button mPickTallButton;
    private Button mPickLargeButton;
    private Button mSaveImageButton;
    private ImageView mImageViewLarge;
    private ImageView mImageViewTall;

	public String playerName = "Bob";
	public boolean isLargeImage = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.pick_images_activity);

           mImageViewLarge = (ImageView) findViewById(R.id.image_large);
           mImageViewTall = (ImageView) findViewById(R.id.image_tall);
           
           mCropLargeButton = (Button) findViewById(R.id.crop_large_button);
           mCropTallButton = (Button) findViewById(R.id.crop_tall_button);
           
           mPickTallButton = (Button) findViewById(R.id.pick_tall_button);
           mPickLargeButton = (Button) findViewById(R.id.pick_large_button);
           
           mSaveImageButton = (Button) findViewById(R.id.save_image);
           
           mCropLargeButton.setOnClickListener(mButtonListener);
           mCropTallButton.setOnClickListener(mButtonListener);
           mSaveImageButton.setOnClickListener(mButtonListener);
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
    	
           @Override
           public void onClick(View v) {
        	   String status = Environment.getExternalStorageState();
        	   
        	   switch (v.getId()) {

                  case R.id.crop_large_button:
                	  isLargeImage  = true;
                	  if (status.equals(Environment.MEDIA_MOUNTED)) {
                             File tempFile = new File(Environment.getExternalStorageDirectory() + "/temp_large.jpg");
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
                             pickCropImageIntent.putExtra("outputX", height/2);
                             pickCropImageIntent.putExtra("outputY", width);
                             pickCropImageIntent.putExtra("aspectX", height/2);
                             pickCropImageIntent.putExtra("aspectY", width);
                             pickCropImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                           tempUri);
                              pickCropImageIntent.putExtra("outputFormat",
                                           Bitmap.CompressFormat.JPEG.toString());
                             startActivityForResult(pickCropImageIntent,
                                           REQUEST_PICK_CROP_IMAGE);
                      }else{
                            
                      }
                      break;
                  case R.id.crop_tall_button:
                	  isLargeImage = false;
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
                	  File sdCardDirectory = Environment.getExternalStorageDirectory();
                	  Bitmap selectedImage = BitmapFactory.decodeFile(Environment
                              .getExternalStorageDirectory() + "/temp_large.jpg");
                	  String dirString = sdCardDirectory + "/" + getString(R.string.app_name) + "/";
            	      boolean success = false;
            	      boolean success2 = false;
            	      
            	      // Encode the file as a PNG image.
            	      FileOutputStream outStream;
            	      try {
            	    	  File dir = new File(dirString);
            	    	  if (!dir.isDirectory()) {
            	    		  dir.mkdirs();
            	    	  }
            	    	  File image = new File(dirString, playerName + "_large.png");
            	    	  if (!image.exists()) {
                    		  image.createNewFile();
                    	  }
            	    	  
            	          outStream = new FileOutputStream(image);
            	          selectedImage.compress(Bitmap.CompressFormat.PNG, 100, outStream); 
            	          // 100 to keep full quality of the image

            	          outStream.flush();
            	          outStream.close();
            	          success = true;
            	          
            	          File image2 = new File(dirString, playerName + ".png");
            	    	  if (!image2.exists()) {
                    		  image2.createNewFile();
                    	  }
            	    	  
            	          outStream = new FileOutputStream(image2);
            	          Bitmap selectedImage2 = BitmapFactory.decodeFile(Environment
                                  .getExternalStorageDirectory() + "/temp.jpg");
                    	  selectedImage2.compress(Bitmap.CompressFormat.PNG, 100, outStream); 
            	          // 100 to keep full quality of the image

            	          outStream.flush();
            	          outStream.close();
            	          success2 = true;
            	          
            	          if (success) {
                	    	  new SingleMediaScanner(PickImageActivity.this, image);
                	      }
                	      if (success2) {
                	    	  new SingleMediaScanner(PickImageActivity.this, image2);
                	      }
            	      } catch (FileNotFoundException e) {
            	    	  e.printStackTrace();
            	      } catch (IOException e) {
            	          e.printStackTrace();
            	      }
                	  break;
                  }
           }
    };

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
           switch (requestCode) {
	           case REQUEST_PICK_CROP_IMAGE:
	        	  Bitmap croppedImage = BitmapFactory.decodeFile(Environment
                               .getExternalStorageDirectory() + "/temp"+(isLargeImage?"_large":"")+".jpg");
                  (isLargeImage?mImageViewLarge:mImageViewTall).setImageBitmap(croppedImage);
                  break;
           }
    }
}