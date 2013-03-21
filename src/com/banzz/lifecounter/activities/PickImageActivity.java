package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.SingleMediaScanner;
import com.banzz.lifecounter.utils.Utils.Constants;

public class PickImageActivity extends Activity {

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_PICK_CROP_IMAGE = 2;

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
	private String playerName;
	
	private int imageStatus[] = new int[]{STATUS_EMPTY, STATUS_EMPTY};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);

           Intent bundle = getIntent();
           playerName = bundle.getStringExtra(Constants.KEY_PLAYER_TARGET);
           
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
			int actionCode = cropImage ? REQUEST_PICK_CROP_IMAGE : REQUEST_PICK_IMAGE;
			
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
									
									File image = new File(dirString, playerName + (index==INDEX_LARGE?"_large":"") + ".png");
									if (!image.exists()) {
										image.createNewFile();
									}

									outStream = new FileOutputStream(image);

									String fileName = index==INDEX_LARGE ? Constants.TEMP_LARGE_FILE_NAME : Constants.TEMP_FILE_NAME;
									
									Bitmap selectedImage = BitmapFactory.decodeFile(Environment
											.getExternalStorageDirectory() + "/" + fileName);
									selectedImage.compress(Bitmap.CompressFormat.PNG, 100,
											outStream);
									// 100 to keep full quality of the image
				
									outStream.flush();
									outStream.close();
									success = true;
									
									if (success) {
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
				break;
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
           switch (requestCode) {
           case REQUEST_PICK_IMAGE:
               if (RESULT_OK == resultCode) {
                     Uri imageUri = intent.getData();
                     Bitmap bitmap;
                     try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                          getContentResolver(), imageUri);
                            (isLargeImage?mImageViewLarge:mImageViewTall).setImageBitmap(bitmap);
                            
                            imageStatus[isLargeImage?INDEX_LARGE:INDEX_TALL] = STATUS_PICKED;
                     } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                     } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                     }

               }
               break;
           case REQUEST_PICK_CROP_IMAGE:
	        	  Bitmap croppedImage = BitmapFactory.decodeFile(Environment
                               .getExternalStorageDirectory() + "/"+ (isLargeImage?Constants.TEMP_LARGE_FILE_NAME:Constants.TEMP_FILE_NAME));
                  (isLargeImage?mImageViewLarge:mImageViewTall).setImageBitmap(croppedImage);
                  imageStatus[isLargeImage?INDEX_LARGE:INDEX_TALL] = STATUS_CROPPED;
                  break;
           }
    }
}