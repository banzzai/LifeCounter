package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
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
        
        mPlayersView = (ListView) findViewById(R.id.profile_list);
        ArrayList<Player> list = new ArrayList<Player>();
        Collections.addAll(list, mUsers);
        mPlayersView.setAdapter(new PlayerProfileAdapter(this, list));
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
	
	private class PlayerProfileAdapter extends ArrayAdapter<Player> {

		private Context mContext;
		private List<Player> mPlayers;

		public PlayerProfileAdapter(Context context, List<Player> players) {
			super(context, 0, players);
			mContext = context;
			mPlayers = players;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(DeletePlayerActivity.this);
			View view = inflater.inflate(R.layout.delete_player_item, parent, false);

			Player player = getItem(position);
			TextView name = (TextView) view.findViewById(R.id.user_name);
			name.setText(player.getName());
			
			CheckBox checkDelete = (CheckBox) view.findViewById(R.id.check_delete);
			checkDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Log.e("DELETE", "Marking "+position+" for deletion");
				}
			});
			
			return view;
		}
	}
}