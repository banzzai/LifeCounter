package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.commons.RoundedImageView;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LoadPlayerDialog extends LifeCounterDialog {
	private static final String TAG = LoadPlayerDialog.class.getName();
	
	LoadPlayerDialogListener mListener;
	
	private WheelView mPlayerOneProfileWheel;
	private WheelView mPlayerTwoProfileWheel;
	private UserWheelAdapter mUserLeftWheelAdapter;
	private UserWheelAdapter mUserRightWheelAdapter;
	
	private Player[] mUsers;
	
	public LoadPlayerDialog(Context context) {
		super(context);
		
		this.setContentView(R.layout.load_player);
		
		String fileName = Constants.PROFILES_FILE_NAME;

	    File externalDir = mContext.getExternalFilesDir(null);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(externalDir + fileName);

		} catch (Exception e) {
			Log.e(TAG, "Couldn't load profiles");
		}
		
		Gson gson = new Gson();
		if (fis == null) {
			//Empty list
		} else {
			Reader reader = new InputStreamReader(fis);
			mUsers = gson.fromJson(reader, Player[].class);

			if (mUsers != null && mUsers.length > 0) {
				mUserLeftWheelAdapter = new UserWheelAdapter(mUsers, mContext, true);
				mUserRightWheelAdapter = new UserWheelAdapter(mUsers, mContext, false);
			}

			for (int i=0; i < mUsers.length; i++)
			{
				if (mUsers[i].getThumbnailUrl() == null)
				{
					//PickImageActivity.createThumbnailForPlayer(mUsers[i].getName(), mContext);
				}
			}
		}

		mPlayerOneProfileWheel = (WheelView) findViewById(R.id.player_one_profile);
		mPlayerOneProfileWheel.setViewAdapter(mUserLeftWheelAdapter);
		
		mPlayerTwoProfileWheel = (WheelView) findViewById(R.id.player_two_profile);
		mPlayerTwoProfileWheel.setViewAdapter(mUserRightWheelAdapter);
		
		Button okButton = (Button) findViewById(R.id.select_players_ok_button);
	    okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null && mUsers != null)
				{
					mListener.onValidateLoad(mUsers[mPlayerOneProfileWheel.getCurrentItem()], 
							mUsers[mPlayerTwoProfileWheel.getCurrentItem()]);
				}
				dismiss();
			}
		});
	}

	public void setListener(LoadPlayerDialogListener mListener) {
		this.mListener = mListener;
	}

	public interface LoadPlayerDialogListener {
		public void onValidateLoad(Player player1, Player player2);
	}
	
	private class UserWheelAdapter extends AbstractWheelTextAdapter {
		private Player[] mUserList;
		
		public UserWheelAdapter(Player[] users, Context mContext, boolean leftPlayer) {
			super(mContext, leftPlayer? R.layout.profile_wheel_item_left : R.layout.profile_wheel_item_right, NO_RESOURCE);
			
			mUserList = users;
		}

		@Override
	    public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
	        
			TextView profileName = (TextView) view.findViewById(R.id.profile_wheel_name);
	        profileName.setText(mUserList[index].getName());
	        
	        RoundedImageView profileIcon = (RoundedImageView) view.findViewById(R.id.profile_wheel_icon);
	        profileIcon.setImageURI(Uri.parse(mUserList[index].getLargeBgUrl()));
	        
	        return view;
		}
		
		@Override
	    public int getItemsCount() {
	        return mUserList.length;
	    }
	    
	    @SuppressLint("DefaultLocale")
		@Override
	    protected CharSequence getItemText(int index) {
	        return mUserList[index].getName();
	    }
	}
}
