package com.banzz.lifecounter.activity;

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

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.Player;
import com.banzz.lifecounter.common.Utils;

public class DeletePlayerActivity extends FullScreenActivity {
	private Player[] mUsers;
	private ListView mPlayersView;
    private boolean[] mMarkedForDeletion;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_player);
		
        mUsers = Utils.loadProfiles(this);
        mMarkedForDeletion = new boolean[mUsers.length];
        for (int i=0; i<mUsers.length; i++)
        {
            mMarkedForDeletion[i] = false;
        }
        
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
        int deletionCount = 0;
        for (int i=0; i<mUsers.length; i++)
        {
            if (mMarkedForDeletion[i])
            {
                deletionCount++;
            }
        }
        if (deletionCount != 0)
        {
            Player[] newUsers = new Player[mUsers.length-deletionCount];
            int newUserPosition = 0;
            for (int i=0; i<mUsers.length; i++)
            {
                if (!mMarkedForDeletion[i])
                {
                    newUsers[newUserPosition] = mUsers[i];
                    newUserPosition++;
                }
            }
            mUsers = newUsers;
            
            Utils.saveProfiles(mUsers, this);
        }
	}
	
	private class PlayerProfileAdapter extends ArrayAdapter<Player> {
		public PlayerProfileAdapter(Context context, List<Player> players) {
			super(context, 0, players);
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
				public void onClick(View view) {
                    mMarkedForDeletion[position] = ((CheckBox)view).isChecked();
                    Log.e("DELETE", "Marking "+position+" for deletion");
				}
			});
			
			return view;
		}
	}
}