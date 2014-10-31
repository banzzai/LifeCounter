package com.banzz.lifecounter.activity;

import com.banzz.lifecounter.common.Utils.Constants;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = new Bundle();
        args.putInt(Constants.KEY_PLAYER_TARGET, getIntent().getIntExtra(Constants.KEY_PLAYER_TARGET, Constants.PLAYER_ZERO));
        
        SettingsFragment frag = new SettingsFragment();
        frag.setArguments(args);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, frag)
                .commit();
    }
}
