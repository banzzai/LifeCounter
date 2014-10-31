package com.banzz.lifecounter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.Player;
import com.banzz.lifecounter.common.Utils;
import com.banzz.lifecounter.common.Utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PickImageActivity extends FullScreenActivity {
	private static final String TAG = PickImageActivity.class.getName();
	
	private static final int INDEX_LARGE = 0;
    private static final int INDEX_TALL = 1;
    private static final int INDEX_THUMB = 2;
    
    private static final int STATUS_EMPTY = -1;
    private static final int STATUS_PICKED = 0;
    private static final int STATUS_CROPPED = 1;

	private static final int THUMBNAIL_HEIGHT = 200;
    
    private Button mSaveImageButton;
    private ImageView mImageViewLarge;
    private ImageView mImageViewTall;

	public boolean isLargeImage = false;
	private Player mPlayer;
	
	private int imageStatus[] = new int[]{STATUS_EMPTY, STATUS_EMPTY};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pick_images_activity);

        Intent bundle = getIntent();
        mPlayer = (Player) bundle.getParcelableExtra(Constants.KEY_PLAYER_TARGET);
        
        //This is a newly created profile, let's give it an id
        if (mPlayer.getId() == null)
        {
        	mPlayer.setId(Utils.generateProfileId(this));
        }

        mImageViewLarge = (ImageView) findViewById(R.id.image_large);
        mImageViewLarge.setOnClickListener(mButtonListener);
        mImageViewTall = (ImageView) findViewById(R.id.image_tall);
        mImageViewTall.setOnClickListener(mButtonListener);

        mSaveImageButton = (Button) findViewById(R.id.image_ok);
        mSaveImageButton.setOnClickListener(mButtonListener);
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
		@SuppressWarnings("deprecation")
		private void pickImage(boolean isLarge, boolean cropImage, String status) {
			isLargeImage = isLarge;
			int actionCode = cropImage ? Constants.REQUEST_PICK_CROP_IMAGE : Constants.REQUEST_PICK_IMAGE;
			
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				Intent pickCropImageIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				
				//Crop options, if set
				if (cropImage) {
					String fileName = generateTempFileName(mPlayer, isLarge);
					File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
					Uri tempUri = Uri.fromFile(tempFile);

					pickCropImageIntent.setType("image/*");
					pickCropImageIntent.putExtra("crop", "true");
					pickCropImageIntent.putExtra("scale", true);

                    Display display = getWindowManager().getDefaultDisplay();
                    int width;
                    int height;
                    
                    if (Build.VERSION.SDK_INT < 13)
                    {
                        width = display.getWidth();
                        height = display.getHeight();
                    }
                    else
                    {
                        Point size = new Point();
                        display.getSize(size);
                        width = size.x;
                        height = size.y;
                    }
					
                    final int xVal = isLarge ? height : width;
                    final int yVal = isLarge ? width : height;
                    
                    pickCropImageIntent.putExtra("outputX", xVal / 2);
					pickCropImageIntent.putExtra("outputY", yVal);
					pickCropImageIntent.putExtra("aspectX", xVal / 2);
					pickCropImageIntent.putExtra("aspectY", yVal);
					
					pickCropImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
					pickCropImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
				}
				
				startActivityForResult(pickCropImageIntent, actionCode);
			} else {
				Toast.makeText(PickImageActivity.this, PickImageActivity.this.getString(R.string.sd_not_found), Toast.LENGTH_LONG).show();
			}
		}
		
		@Override
		public void onClick(View v) {
			final String status = Environment.getExternalStorageState();

			switch (v.getId()) {
                case R.id.image_large:
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickImageActivity.this);
                    builder.setMessage(R.string.pick_dialog_message).setTitle(R.string.pick_large_title);
                    builder.setPositiveButton(R.string.pick, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pickImage(true, false, status);
                            dialog.dismiss();
                        }
                    });
                    builder.setNeutralButton(R.string.crop, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pickImage(true, true, status);
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                case R.id.image_tall:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(PickImageActivity.this);
                    builder2.setMessage(R.string.pick_dialog_message).setTitle(R.string.pick_tall_title);
                    builder2.setPositiveButton(R.string.pick, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pickImage(false, false, status);
                            dialog.dismiss();
                        }
                    });
                    builder2.setNeutralButton(R.string.crop, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pickImage(false, true, status);
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                    break;
				case R.id.image_ok:
					if (imageStatus[INDEX_LARGE] == STATUS_CROPPED || imageStatus[INDEX_TALL] == STATUS_CROPPED) {
						String dirString = Utils.getAppStoragePath();
			
						File dir = new File(dirString);
						if (!dir.isDirectory()) {
							dir.mkdirs();
						}
						
						//Once for each format, if it's cropped and not just picked
						for (int index: new int[]{INDEX_LARGE, INDEX_TALL}) {
							try {
								if (imageStatus[index] == STATUS_CROPPED) {
									String newImageFileName = generateFileName(mPlayer, index);
									String tempFileName = generateTempFileName(mPlayer, index==INDEX_LARGE);
									Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + tempFileName);
									
									Utils.saveImage(newImageFileName, selectedImage);
									
									//Deleting temp file
									File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + tempFileName);
									if (tempFile.exists()) {
										tempFile.delete();
									}
									
									if (index == INDEX_LARGE) 
									{
										mPlayer.setLargeBgUrl(dirString + newImageFileName);
					                } 
									else 
					                {
					                  	mPlayer.setTallBgUrl(dirString + newImageFileName);
					                }
								}
								
							} catch (FileNotFoundException e) {
								Log.e(TAG, "File not found for player "+mPlayer.getName()+", index "+index, e);
							} catch (IOException e) {
								Log.e(TAG, "IOException for player "+mPlayer.getName()+", index "+index, e);
							}
						}
						mPlayer.setThumbnailUrl(PickImageActivity.createThumbnailForPlayer(mPlayer));
					}
				//Quit the image picking activity
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Constants.KEY_PLAYER_TARGET, mPlayer);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				break;
			}
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
                            Log.e(TAG, "Image should have been picked properly for " + mPlayer.getName(), e);
                     } catch (IOException e) {
                    	 Log.e(TAG, "Image should have been picked properly for " + mPlayer.getName(), e);
                     }
               }
               break;
           case Constants.REQUEST_PICK_CROP_IMAGE:
                  String fileName = generateTempFileName(mPlayer, isLargeImage);
	        	  Bitmap croppedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/"+ fileName);
                  (isLargeImage?mImageViewLarge:mImageViewTall).setImageBitmap(croppedImage);
                  
                  imageStatus[isLargeImage?INDEX_LARGE:INDEX_TALL] = STATUS_CROPPED;
                  checkDone();
                  break;
		}
    }

    @Override
    public void onBackPressed()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.KEY_PLAYER_TARGET, "");
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

	//Make sure both images have been selected
	private void checkDone() {
		if (imageStatus[INDEX_TALL]!=STATUS_EMPTY && imageStatus[INDEX_LARGE]!=STATUS_EMPTY) {
			mSaveImageButton.setVisibility(View.VISIBLE);
		}
	}

	// creates the file name for a tall, large, or thumbnail for this player
	// PREFER USING GETTERS ON THE PLAYER ONCE THE IMAGE IS CREATED AND SET TO IT
	private static String generateFileName(Player player, int imageType)
	{
		if (player == null || (imageType != INDEX_LARGE && imageType != INDEX_TALL && imageType != INDEX_THUMB))
		{
			return null;
		}
		return player.getId() + "_" + (imageType==INDEX_LARGE?"_large":imageType==INDEX_TALL?"_tall":"_thumb") + ".png";
	}
	
	// Gets the temp file name corresponding to the player name and aspect ratio
	private static String generateTempFileName(Player player, boolean isLarge)
	{
		if (player == null)
		{
			return null;
		}
		return player.getId() + "_" + (isLarge ? Constants.TEMP_LARGE_FILE_NAME : Constants.TEMP_FILE_NAME);
	}
	
	/**
	 * Create a thumbnail picture for this player, based on the large image.
	 * 
	 * @param player to create a thumbnail for
	 * @return path to the new thumbnail image
	 */
	public static String createThumbnailForPlayer(Player player) {
		Bitmap bitmap;
		String thumbnailImagePath = generateFileName(player, INDEX_THUMB);
		String largeImagePath = generateFileName(player, INDEX_LARGE);
		
        try 
        {
        	String dirString = Utils.getAppStoragePath();
        	bitmap = BitmapFactory.decodeFile(dirString + largeImagePath);
			if (bitmap == null)
			{
				throw new FileNotFoundException("Failed to load " + thumbnailImagePath);
			}
			int imageWidth = bitmap.getWidth();
			int imageHeight = bitmap.getHeight();
			
			Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, (THUMBNAIL_HEIGHT*imageWidth)/imageHeight, THUMBNAIL_HEIGHT, true);
			Utils.saveImage(thumbnailImagePath, thumbnail);
			
			return dirString + thumbnailImagePath;
        }
        catch (FileNotFoundException e)
        {
        	Log.e(TAG, "createThumbnailFrom() FileNotFoundException: " + thumbnailImagePath, e);
        	return null;
        }
        catch (IOException e) 
        {
        	Log.e(TAG, "createThumbnailFrom() IOException: " + thumbnailImagePath, e);
        	return null;
        }
	}
}