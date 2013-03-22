package com.banzz.lifecounter.utils;

public class Utils {
	public class Constants {
		//Game parameters
		public static final int LIFE_MAX 	= 999;
		public static final int LIFE_MIN 	= 0;
		public static final int PLAYER_ZERO = -1;
		public static final int PLAYER_ONE 	= 0;
		public static final int PLAYER_TWO 	= 1;
		
		//Misc
		public static final String PAYPAL_DONATIONS = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=BD7UPFTH3FTFL&lc=US&item_name=Mayor%20Suplex&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
		
		//Intent request codes
		public static final int REQUEST_EDIT_PLAYER		= 0;
		public static final int REQUEST_PICK_IMAGES 	= 1;
		public static final int REQUEST_PICK_IMAGE 		= 2;
		public static final int REQUEST_PICK_CROP_IMAGE = 3;
		public static final String KEY_PLAYER_TARGET 	= "player_target";
		
		//Config
		public static final String PROFILES_FILE_NAME 	= "/players.JSON";
		public static final String TEMP_FILE_NAME 		= "temp.jpg";
		public static final String TEMP_LARGE_FILE_NAME = "temp_large.jpg";
	}
}