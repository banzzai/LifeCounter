package com.banzz.lifecounter.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.banzz.lifecounter.R;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Utils
{
	private static final String TAG = Utils.class.getName();

	public static class Constants
	{
		// Game parameters
		public static final int LIFE_MAX = 999;
		public static final int LIFE_MIN = 0;
		public static final int PLAYER_ZERO = -1;
		public static final int PLAYER_ONE = 0;
		public static final int PLAYER_TWO = 1;

		// Toolbox action ids
		public static final int ACTION_RESTART = 0;
		public static final int ACTION_LOAD_PLAYERS = 1;
		public static final int ACTION_FLIP_PLAYER_2 = 2;

		// Misc
		public static final String PAYPAL_DONATIONS = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=BD7UPFTH3FTFL&lc=US&item_name=Mayor%20Suplex&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
		public static final String APP_STORE_DONATIONS = "https://play.google.com/store/apps/details?id=com.banzz.lifecounter";
		public static final String BLOG_URL = "http://bullzzai.wordpress.com/";
		public static final String FACEBOOK_URL = "https://www.facebook.com/pages/Bullzzai/139751209567171";
		public static final String TWITTER_URL = "https://twitter.com/Bullzzai_blog";
		public static final String BULLZZAI_EMAIL = "bullzzai.blog@gmail.com";

		// Intent request codes
		public static final int REQUEST_EDIT_PLAYERS = 0;
		public static final int REQUEST_PICK_IMAGES = 1;
		public static final int REQUEST_PICK_IMAGE = 2;
		public static final int REQUEST_PICK_CROP_IMAGE = 3;
		public static final int REQUEST_NEXT_ROUND = 4;
		public static final int REQUEST_DELETE_PLAYERS = 5;
		public static final int REQUEST_EDIT_WIZARD = 6;
		public static final int REQUEST_NEW_TOURNAMENT = 7;

		public static final String KEY_PLAYER_TARGET = "player_target";
		public static final String KEY_PLAYER_ONE = "player_one";
		public static final String KEY_PLAYER_TWO = "player_two";
		public static final String KEY_TOURNAMENT_PLAYERS = "tournament_players";

		// Profiles file names
		public static final String PROFILES_FILE_NAME = "/players.JSON";
		public static final String TEMP_FILE_NAME = "temp.jpg";
		public static final String TEMP_LARGE_FILE_NAME = "temp_large.jpg";
		public static final String THUMBNAIL_FILE_NAME_SUFFIX = "_thumb";
		public static final String LARGE_FILE_NAME_SUFFIX = "_thumb";
		public static final String TALL_FILE_NAME_SUFFIX = "_thumb";

		// Fonts
		public static Typeface FONT_HELVETICA_NUEUE = null;
		public static Typeface FONT_HELVETICA_NUEUE_CONDENSED = null;
		public static String STRING_HELVETICA_NUEUE = "HelveticaNeue";
		public static String STRING_HELVETICA_NUEUE_CONDENSED = "HelveticaNeueC";
	}

	private static String mStorageString;
	private static SharedPreferences mPreferences;

	public static void initUtils(Context context)
	{
		if (Constants.FONT_HELVETICA_NUEUE == null)
		{
			Constants.FONT_HELVETICA_NUEUE = Typeface.createFromAsset(context.getAssets(), "fonts/"
					+ Constants.STRING_HELVETICA_NUEUE + ".ttf");
			Constants.FONT_HELVETICA_NUEUE_CONDENSED = Typeface.createFromAsset(context.getAssets(), "fonts/"
					+ Constants.STRING_HELVETICA_NUEUE_CONDENSED + ".ttf");
		}

		if (mStorageString == null)
		{
			File sdCardDirectory = Environment.getExternalStorageDirectory();
			mStorageString = sdCardDirectory + "/" + context.getString(R.string.app_name) + "/";
		}

		if (mPreferences == null)
		{
			mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
	}

	/**
	 * Adding default player profiles if there is no profile added.
	 * 
	 * @param context
	 *            of the app.
	 */
	public static void fillInDefaultProfiles(final Context context)
	{
		if (loadProfiles(context) == null)
		{
			InputStream fis = context.getResources().openRawResource(R.raw.default_profiles);

			Gson gson = new Gson();
			if (fis == null)
			{
				Log.e(TAG, "Couldn't load the default player json!");
				return;
			}
			else
			{
				Reader reader = new InputStreamReader(fis);
				saveProfiles(gson.fromJson(reader, Player[].class), context);
			}
		}
	}

	/**
	 * Save a list of profiles to the local storage.
	 * 
	 * @param profiles
	 *            List of profiles to be saved.
	 * @param context
	 *            Context of the app.
	 */
	public static void saveProfiles(final Player[] profiles, final Context context)
	{
		Gson gson = new Gson();
		String json = gson.toJson(profiles);
		String fileName = Constants.PROFILES_FILE_NAME;

		FileOutputStream fos;
		try
		{
			File image = new File(Utils.getAppStoragePath(), fileName);
			if (!image.exists())
			{
				image.createNewFile();
			}

			fos = new FileOutputStream(image);
			fos.write(json.getBytes());
			fos.close();
		}
		catch (Exception e)
		{
			Toast.makeText(context, "JSON WRITE FAILED", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	/**
	 * Loading all player profiles.
	 * 
	 * @param context
	 *            Context of the app.
	 * @return List of all player profiles.
	 */
	public static Player[] loadProfiles(final Context context)
	{
		String fileName = Constants.PROFILES_FILE_NAME;

		InputStream fis = null;
		try
		{
			fis = new FileInputStream(Utils.getAppStoragePath() + fileName);
		}
		catch (Exception e)
		{
			Log.e(TAG, "Couldn't load profiles");
		}

		Gson gson = new Gson();
		if (fis == null)
		{
			Log.e(TAG, "Couldn't load player profiles from the local storage");
			return null;
		}
		else
		{
			Reader reader = new InputStreamReader(fis);
			return gson.fromJson(reader, Player[].class);
		}
	}

	/**
	 * Save an image to the app storage.
	 * 
	 * @param imageName
	 *            name of the image file
	 * @bitmapImage the image itself
	 * @context Context used to retrieve the app's path
	 */
	public static void saveImage(String imageName, Bitmap bitmapImage) throws IOException
	{
		String dirString = getAppStoragePath();

		File image = new File(dirString, imageName);
		if (image.exists())
		{
			image.delete();
		}
		image.createNewFile();

		FileOutputStream outStream = new FileOutputStream(image);
		bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outStream);

		outStream.flush();
		outStream.close();
	}

	/**
	 * Generate an id for a new user profile. The implementation is primitive I
	 * don't think we need too sophisticated code here right now.
	 * 
	 * @param context
	 *            Context used to retrieve the setting containing the next
	 *            available id
	 * @return next available id
	 */
	public static String generateProfileId(Context context)
	{
		// Get last available id
		String profileIdKey = context.getString(R.string.key_profile_id);
		int index = getIntPreference(profileIdKey, 0);

		// Update next available id
		setIntPreference(profileIdKey, index + 1);

		return "mtg" + index + "lc";
	}

	/**
	 * Wrapper around SharedPreference to get a String preference
	 * 
	 * @param key
	 *            Key to the preference
	 * @param defaultValue
	 *            Default String value
	 * @return Value set in Preferences or default if not found
	 */
	public static String getStringPreference(final String key, final String defaultValue)
	{
		return mPreferences.getString(key, defaultValue);
	}

	/**
	 * Wrapper around SharedPreference to get an int preference
	 * 
	 * @param key
	 *            Key to the preference
	 * @param defaultValue
	 *            Default int value
	 * @return Value set in Preferences or default if not found
	 */
	public static int getIntPreference(final String key, final int defaultValue)
	{
		return mPreferences.getInt(key, defaultValue);
	}

	/**
	 * Wrapper around SharedPreference to get a boolean preference
	 * 
	 * @param key
	 *            Key to the preference
	 * @param defaultValue
	 *            Default boolean value
	 * @return Value set in Preferences or default if not found
	 */
	public static boolean getBooleanPreference(final String key, final boolean defaultValue)
	{
		return mPreferences.getBoolean(key, defaultValue);
	}

	/**
	 * Wrapper around SharedPreference to set a String preference
	 * 
	 * @param key
	 *            Key to the preference
	 * @param defaultValue
	 *            String value to set
	 */
	public static void setStringPreference(final String key, final String value)
	{
		SharedPreferences.Editor edit = mPreferences.edit();
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * Wrapper around SharedPreference to set an int preference
	 * 
	 * @param key
	 *            Key to the preference
	 * @param defaultValue
	 *            int value to set
	 */
	public static void setIntPreference(final String key, final int value)
	{
		SharedPreferences.Editor edit = mPreferences.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	/**
	 * Wrapper around SharedPreference to set a boolean preference
	 * 
	 * @param key
	 *            Key to the preference
	 * @param defaultValue
	 *            boolean value to set
	 */
	public static void setBooleanPreference(final String key, final boolean value)
	{
		SharedPreferences.Editor edit = mPreferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
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

	/**
	 * Getting current version of the app
	 * 
	 * @param context
	 *            needed to call the package manager
	 * @return version of the app in a String
	 */
	public static String getVersionString(Context context)
	{
		String versionString = "1.1.001";
		try
		{
			versionString = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			Log.e(TAG, "Can't retrieve app version");
		}
		return versionString;
	}
}