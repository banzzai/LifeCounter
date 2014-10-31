package com.banzz.lifecounter.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.Utils.Constants;

public class HelpUsActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.helpus_activity);
        
        Button mRateButton = (Button) findViewById(R.id.rate_button);
        mRateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_STORE_DONATIONS)));
			}
		});
        
        Button mFeedbackButton = (Button) findViewById(R.id.feedback_button);
        mFeedbackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendMail();
			}
		});
        
        Button mDonationButton = (Button) findViewById(R.id.donation_button);
        mDonationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(HelpUsActivity.this, HelpUsActivity.this.getString(R.string.donation_thanks), Toast.LENGTH_LONG).show();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PAYPAL_DONATIONS)));
			}
		});

        Button mBlogButton = (Button) findViewById(R.id.blog_button);
        mBlogButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BLOG_URL)));
            }
        });

        Button mFacebookButton = (Button) findViewById(R.id.facebook_button);
        mFacebookButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FACEBOOK_URL)));
            }
        });

        Button mTwitterButton = (Button) findViewById(R.id.twitter_button);
        mTwitterButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TWITTER_URL)));
            }
        });
	}

    private void sendMail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.BULLZZAI_EMAIL});
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_for_version, getString(R.string.version_name)));
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_feedback)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HelpUsActivity.this, getString(R.string.no_mail_client), Toast.LENGTH_SHORT).show();
        }
    }
}
