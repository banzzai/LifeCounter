package com.banzz.lifecounter.activity;

import com.banzz.lifecounter.common.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class FullScreenActivity extends Activity {
	@Override
    public void onWindowFocusChanged(boolean hasFocus) 
    {
    	super.onWindowFocusChanged(hasFocus);
    	// For versions > 19 (KITKAT) we use immersive mode
    	if (hasFocus && android.os.Build.VERSION.SDK_INT >= 19)
        {
        	hideNavigationControls();
        }
    }
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
	    Utils.initUtils(getApplicationContext());
	    
	    if (android.os.Build.VERSION.SDK_INT >= 19)
        {
        	hideNavigationControls();
        }
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Utils.initUtils(getApplicationContext());
	}
	
	private void hideNavigationControls()
	{
		getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}
