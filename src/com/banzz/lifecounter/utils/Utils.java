package com.banzz.lifecounter.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.banzz.lifecounter.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;

public class Utils {
	public static class Constants {
		//Game parameters
		public static final int LIFE_MAX 	= 999;
		public static final int LIFE_MIN 	= 0;
		public static final int PLAYER_ZERO = -1;
		public static final int PLAYER_ONE 	= 0;
		public static final int PLAYER_TWO 	= 1;
		
		//Misc
		public static final String PAYPAL_DONATIONS     = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=BD7UPFTH3FTFL&lc=US&item_name=Mayor%20Suplex&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
		public static final String APP_STORE_DONATIONS  = "https://play.google.com/store/apps/details?id=com.banzz.lifecounter";
        public static final String BLOG_URL             = "http://bullzzai.wordpress.com/";
        public static final String FACEBOOK_URL         = "https://www.facebook.com/pages/Bullzzai/139751209567171";
        public static final String TWITTER_URL          = "https://twitter.com/Bullzzai_blog";
        public static final String BULLZZAI_EMAIL       = "bullzzai.blog@gmail.com";

		//Intent request codes
		public static final int REQUEST_EDIT_PLAYERS	= 0;
		public static final int REQUEST_PICK_IMAGES 	= 1;
		public static final int REQUEST_PICK_IMAGE 		= 2;
		public static final int REQUEST_PICK_CROP_IMAGE = 3;
		public static final int REQUEST_NEXT_ROUND 		= 4;
		public static final int REQUEST_DELETE_PLAYERS	= 5;
        public static final int REQUEST_EDIT_WIZARD 	= 6;
        public static final int REQUEST_NEW_TOURNAMENT 	= 7;
		
		public static final String KEY_PLAYER_TARGET	 	= "player_target";
		public static final String KEY_PLAYER_ONE 			= "player_one";
		public static final String KEY_PLAYER_TWO			= "player_two";
		public static final String KEY_TOURNAMENT_PLAYERS	= "tournament_players";
		
		//Config
		public static final String PROFILES_FILE_NAME 	= "/players.JSON";
		public static final String TEMP_FILE_NAME 		= "temp.jpg";
		public static final String TEMP_LARGE_FILE_NAME = "temp_large.jpg";
		
		public static Typeface FONT_HELVETICA_NUEUE = null;
		public static String STRING_HELVETICA_NUEUE = "HelveticaNeue";
		public static Typeface FONT_HELVETICA_NUEUE_CONDENSED = null;
		public static String STRING_HELVETICA_NUEUE_CONDENSED = "HelveticaNeueC";
	}

	private static String mStorageString;
	
	public static void initUtils(Context context)
	{
		Constants.FONT_HELVETICA_NUEUE = Typeface.createFromAsset(context.getAssets(), "fonts/"+Constants.STRING_HELVETICA_NUEUE+".ttf");
		Constants.FONT_HELVETICA_NUEUE_CONDENSED = Typeface.createFromAsset(context.getAssets(), "fonts/"+Constants.STRING_HELVETICA_NUEUE_CONDENSED+".ttf");
		
		if (mStorageString == null)
		{
			File sdCardDirectory = Environment.getExternalStorageDirectory();
			mStorageString = sdCardDirectory + "/" + context.getString(R.string.app_name) + "/";	
		}
	}
	
	/**
	 * Save an image to the app storage.
	 * 
	 * @param imageName name of the image file
	 * @bitmapImage the image itself
	 * @context Context used to retrieve the app's path
	 */
	public static void saveImage(String imageName, Bitmap bitmapImage) throws IOException {
		String dirString = getAppStoragePath();
		
		File image = new File(dirString, imageName);
		if (image.exists()) {
			image.delete();
		}									
		image.createNewFile();
		
		FileOutputStream outStream = new FileOutputStream(image);
		bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outStream);

		outStream.flush();
		outStream.close();
	}
	
	/**
	 * Generate an id for a new user profile. The implementation is primitive I don't think we need too
	 * sophisticated code here right now.
	 * 
	 * @param context Context used to retrieve the setting containing the next available id
	 * @return next available id
	 */
	public static String generateProfileId(Context context)
	{
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = preferences.edit();
		
		// Get last available id
		String profileIdKey = context.getString(R.string.key_profile_id);
		int index = preferences.getInt(profileIdKey, 0);
        
		// Update next available id 
		edit.putInt(profileIdKey, index+1);
        edit.commit();
		
        return "mtg"+index+"lc";
	}
	
	/**
	 * Returns the app's storage path. Needs initUtils to have been called once.
	 * 
	 * @return app's storage path
	 */
	public static String getAppStoragePath()
	{
		return mStorageString;
	}
}