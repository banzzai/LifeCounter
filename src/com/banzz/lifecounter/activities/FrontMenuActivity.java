package com.banzz.lifecounter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.banzz.lifecounter.R;

public class FrontMenuActivity extends android.app.Activity {
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_menu);
        
        Button mButton = (Button) findViewById(R.id.two_player_button);
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), TwoPlayerActivity.class));
			}
		});
        
        Button mSettings = (Button) findViewById(R.id.settings_button);
        mSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			}
		});
        
        Button mImagePicker = (Button) findViewById(R.id.pick_image);
        mImagePicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), ActionPickDemo.class));
			}
		});
	}
}
