package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.utils.SingleMediaScanner;
import com.banzz.lifecounter.utils.Utils.Constants;

public class PickImageActivity extends Activity {
    private static final int INDEX_LARGE = 0;
    private static final int INDEX_TALL = 1;
    
    private static final int STATUS_EMPTY = -1;
    private static final int STATUS_PICKED = 0;
    private static final int STATUS_CROPPED = 1;
    
    private Button mCropLargeButton;
    private Button mCropTallButton;
    private Button mPickTallButton;
    private Button mPickLargeButton;
    private Button mSaveImageButton;
    private ImageView mImageViewLarge;
    private ImageView mImageViewTall;

	public boolean isLargeImage = false;
	private Player mPlayer;
	
	private int imageStatus[] = new int[]{STATUS_EMPTY, STATUS_EMPTY};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);

           Intent bundle = getIntent();
           mPlayer = (Player) bundle.getParcelableExtra(Constants.KEY_PLAYER_TARGET);
           
           setContentView(R.layout.pick_images_activity);

           mImageViewLarge = (ImageView) findViewById(R.id.image_large);
           mImageViewTall = (ImageView) findViewById(R.id.image_tall);
           
           mCropLargeButton = (Button) findViewById(R.id.crop_large_button);
           mCropTallButton = (Button) findViewById(R.id.crop_tall_button);
           
           mPickTallButton = (Button) findViewById(R.id.pick_tall_button);
           mPickLargeButton = (Button) findViewById(R.id.pick_large_button);
           
           mSaveImageButton = (Button) findViewById(R.id.image_ok);
           
           mPickTallButton.setOnClickListener(mButtonListener);
           mPickLargeButton.setOnClickListener(mButtonListener);
           mCropLargeButton.setOnClickListener(mButtonListener);
           mCropTallButton.setOnClickListener(mButtonListener);
           mSaveImageButton.setOnClickListener(mButtonListener);
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
		private void pickImage(boolean isLarge, boolean cropImage, String status) {
			isLargeImage = isLarge;
			int actionCode = cropImage ? Constants.REQUEST_PICK_CROP_IMAGE : Constants.REQUEST_PICK_IMAGE;
			
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				Intent pickCropImageIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				
				//Crop options, if set
				if (cropImage) {
					String fileName = isLarge ? Constants.TEMP_LARGE_FILE_NAME : Constants.TEMP_FILE_NAME;
					File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
					Uri tempUri = Uri.fromFile(tempFile);

					pickCropImageIntent.setType("image/*");
					pickCropImageIntent.putExtra("crop", "true");
					pickCropImageIntent.putExtra("scale", true);
					
					Display display = getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int width = size.x;
					int height = size.y;
					if (isLarge) {
						pickCropImageIntent.putExtra("outputX", height / 2);
						pickCropImageIntent.putExtra("outputY", width);
						pickCropImageIntent.putExtra("aspectX", height / 2);
						pickCropImageIntent.putExtra("aspectY", width);
					} else {
						pickCropImageIntent.putExtra("outputX", width / 2);
						pickCropImageIntent.putExtra("outputY", height);
						pickCropImageIntent.putExtra("aspectX", width / 2);
						pickCropImageIntent.putExtra("aspectY", height);
					}
					pickCropImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							tempUri);
					pickCropImageIntent.putExtra("outputFormat",
							Bitmap.CompressFormat.JPEG.toString());
				}
				
				startActivityForResult(pickCropImageIntent, actionCode);
			} else {
				Toast.makeText(PickImageActivity.this, PickImageActivity.this.getString(R.string.sd_not_found), Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		public void onClick(View v) {
			String status = Environment.getExternalStorageState();

			switch (v.getId()) {
				case R.id.pick_large_button:
					pickImage(true, false, status);
					break;
				case R.id.pick_tall_button:
					pickImage(false, false, status);
					break;
				case R.id.crop_large_button:
					pickImage(true, true, status);
					break;
				case R.id.crop_tall_button:
					pickImage(false, true, status);
					break;
					
				case R.id.image_ok:
					if (imageStatus[INDEX_LARGE] == STATUS_CROPPED || imageStatus[INDEX_TALL] == STATUS_CROPPED) {
						File sdCardDirectory = Environment
								.getExternalStorageDirectory();
						String dirString = sdCardDirectory + "/"
								+ getString(R.string.app_name) + "/";
			
						// Encode the file as a PNG image.
						FileOutputStream outStream;
						
						File dir = new File(dirString);
						if (!dir.isDirectory()) {
							dir.mkdirs();
						}
						
						//Once for each format, if it's cropped and not just picked
						for (int index: new int[]{INDEX_LARGE, INDEX_TALL}) {
							try {
								if (imageStatus[index] == STATUS_CROPPED) {
									boolean success = false;
									
									String newImagePath = dirString + mPlayer.getName() + (index==INDEX_LARGE?"_large":"") + ".png";
									File image = new File(dirString, mPlayer.getName() + (index==INDEX_LARGE?"_large":"") + ".png");
									if (image.exists()) {
										image.delete();
									}									
									image.createNewFile();
									
									outStream = new FileOutputStream(image);

									String fileName = (index==INDEX_LARGE) ? Constants.TEMP_LARGE_FILE_NAME : Constants.TEMP_FILE_NAME;
									
									Bitmap selectedImage = BitmapFactory.decodeFile(Environment
											.getExternalStorageDirectory() + "/" + fileName);
									selectedImage.compress(Bitmap.CompressFormat.PNG, 100,
											outStream);
									// 100 to keep full quality of the image
				
									outStream.flush();
									outStream.close();
									
									//Deleting temp file
									File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
									if (tempFile.exists()) {
										tempFile.delete();
									}
									
									if (isLargeImage) {
					                  	mPlayer.setLargeBgUrl(newImagePath);
					                  } else {
					                  	mPlayer.setTallBgUrl(newImagePath);
					                  }
									
									success = true;
									
									if (success) {
										//addToDatabase(PickImageActivity.this, image, playerName, isLargeImage);
										new SingleMediaScanner(PickImageActivity.this, image);
									}
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				//Quit the image picking activity
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Constants.KEY_PLAYER_TARGET, mPlayer);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				break;
			}
		}

		private void addToDatabase(Context context, File imageFile, String playerName, boolean isLarge) {
		    ContentValues image = new ContentValues();

		    image.put(Images.Media.TITLE, playerName + (isLarge?"_large":"_tall"));
		    image.put(Images.Media.DISPLAY_NAME, playerName + (isLarge?"_large":"_tall"));
		    image.put(Images.Media.DESCRIPTION,  (isLarge?"Large":"Tall") + " image for " + playerName);
		    String dateTaken = "" + (new Date()).getTime();
		    image.put(Images.Media.DATE_ADDED, dateTaken);
		    image.put(Images.Media.DATE_TAKEN, dateTaken);
		    image.put(Images.Media.DATE_MODIFIED, dateTaken);
		    image.put(Images.Media.MIME_TYPE, "image/jpg");
		    image.put(Images.Media.ORIENTATION, 0);

		     File parent = imageFile.getParentFile();
		     String path = parent.toString().toLowerCase();
		     String name = parent.getName().toLowerCase();
		     image.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
		     image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
		     image.put(Images.Media.SIZE, imageFile.length());

		     image.put(Images.Media.DATA, imageFile.getAbsolutePath());

		     Uri result = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
           case Constants.REQUEST_PICK_IMAGE:
               if (RESULT_OK == resultCode) {
                     Uri imageUri = intent.getData();
                     Bitmap bitmap;
                     try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                          getContentResolver(), imageUri);
                            (isLargeImage?mImageViewLarge:mImageViewTall).setImageBitmap(bitmap);
                            
                            if (isLargeImage) {
                            	mPlayer.setLargeBgUrl(imageUri.toString());
                            } else {
                            	mPlayer.setTallBgUrl(imageUri.toString());
                            }
                            imageStatus[isLargeImage?INDEX_LARGE:INDEX_TALL] = STATUS_PICKED;
                            checkDone();
                     } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                     } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                     }

               }
               break;
           case Constants.REQUEST_PICK_CROP_IMAGE:
	        	  Bitmap croppedImage = BitmapFactory.decodeFile(Environment
                               .getExternalStorageDirectory() + "/"+ (isLargeImage?Constants.TEMP_LARGE_FILE_NAME:Constants.TEMP_FILE_NAME));
                  (isLargeImage?mImageViewLarge:mImageViewTall).setImageBitmap(croppedImage);
                  
                  imageStatus[isLargeImage?INDEX_LARGE:INDEX_TALL] = STATUS_CROPPED;
                  checkDone();
                  break;
		}
    }
	
	//Make sure both images have been selected
	private void checkDone() {
		if (imageStatus[INDEX_TALL]!=STATUS_EMPTY && imageStatus[INDEX_LARGE]!=STATUS_EMPTY) {
			mSaveImageButton.setVisibility(View.VISIBLE);
		}
	}
}