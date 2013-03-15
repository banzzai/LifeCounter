package com.banzz.lifecounter.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.Utils.Constants;

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
        
        Button mDonations = (Button) findViewById(R.id.donations_button);
        mDonations.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(FrontMenuActivity.this, FrontMenuActivity.this.getString(R.string.donation_thanks), Toast.LENGTH_LONG).show();
				//startActivity(new Intent(getApplicationContext(), TestActivity.class));
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PAYPAL_DONATIONS)));
			}
		});
	}
}
