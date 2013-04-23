package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.utils.SystemUiHider;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.google.gson.Gson;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class DeletePlayerActivity extends Activity implements OnClickListener {
	private Player[] mUsers;
	private ListView mPlayersView;
	
	@Override
	protected void onPause() {
		super.onPause();
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_player);
		
        loadSavedProfiles();
        
        Button mDeleteButton = (Button) findViewById(R.id.ok_delete_button);
        mDeleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				validateDelete();
			}
		});
        
        mPlayersView = (ListView) findViewById(R.id.round_list);
        mPlayersView.setAdapter(new PlayerProfileAdapter(this, mUsers));
	}

	private void validateDelete() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
	    dialog.setTitle(this.getString(R.string.Delete));
	    String message = this.getString(R.string.delete_profiles);
	    
	    dialog.setMessage(message);
	    dialog.setCancelable(true);
	    dialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int buttonId) {
	        	doDelete();
	        	setResult(Activity.RESULT_OK, new Intent());
				finish();
	        }
	    });
	    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int buttonId) {
	    		dialog.dismiss();        
	    	}
	    });

	    dialog.show();
	}
	
	private void doDelete() {
		//TODO Delete profiles
	}
	
	private void loadSavedProfiles() {
	    String fileName = Constants.PROFILES_FILE_NAME;
	    //Bad bad casts here. Then again, this is not meant to be adaptable code, used in x different activities;
	    //worse case scenario it crashes and it'll serve as a reminder to set a listener...
	    File externalDir = getExternalFilesDir(null);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(externalDir + fileName);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		if (fis == null) {
			//Empty list
		} else {
			Reader reader = new InputStreamReader(fis);
			mUsers = gson.fromJson(reader, Player[].class);
		}
	}

	private void savePlayers() {
		Gson gson = new Gson();
		String json = gson.toJson(mUsers);
		String fileName = Constants.PROFILES_FILE_NAME;
		File externalDir = getExternalFilesDir(null);
		
		FileOutputStream fos;
		try {
			File image = new File(externalDir, fileName);
			if (!image.exists()) {
				image.createNewFile();
			}	
			
			fos = new FileOutputStream(image);
			//fos = openFileOutput(externalDir + fileName, Context.MODE_PRIVATE);
			fos.write(json.getBytes());
			fos.close();
		} catch (Exception e) {
			Toast.makeText(this, "JSON WRITE FAILED", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View clickedView) {
//		if (clickedView.equals(mBigLife0)) {
//			//Clicking on player 0 life total
//		} else if (clickedView.equals(mPlus0)) {
//			//Clicking on player 1 + button
//		}
	}
	
	private class PlayerProfileAdapter implements ListAdapter {

		public PlayerProfileAdapter(DeletePlayerActivity deletePlayerActivity,
				Player[] mUsers) {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled(int arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}