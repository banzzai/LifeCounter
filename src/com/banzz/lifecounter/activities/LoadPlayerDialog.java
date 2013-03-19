package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.commons.UserListAdapter;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;

public class LoadPlayerDialog extends DialogFragment {
	LoadPlayerDialogListener mListener;
	private RadioGroup mRadio;
	private ListView mSelectUser;

	public void setListener(LoadPlayerDialogListener mListener) {
		this.mListener = mListener;
	}

	public interface LoadPlayerDialogListener {
		public void onValidateLoad(Player user, int slot);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.load_player, null);
	    mRadio = (RadioGroup) view.findViewById(R.id.radio_group);
	    mSelectUser = (ListView) view.findViewById(R.id.player_select);
	    
	    final Player[] mUsers;
	    String fileName = "/players.JSON";
	    boolean empty = true;
	    //Bad bad casts here. Then again, this is not meant to be adaptable code, used in x different activities;
	    //worse case scenario it crashes and it'll serve as a reminder to set a listener...
	    File externalDir = ((Context)mListener).getExternalFilesDir(null);
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
		
			if (mUsers != null && mUsers.length > 0) {
				empty = false;
				mSelectUser.setAdapter(new UserListAdapter(mUsers, getActivity()));
			    mSelectUser.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						mListener.onValidateLoad(mUsers[position], (mRadio.getCheckedRadioButtonId() == R.id.radio_player_1 ? Constants.PLAYER_ONE : Constants.PLAYER_TWO));
						dismiss();
					}
				});
			}
		}
			
		if (empty) {
			mSelectUser.setVisibility(View.GONE);
			view.findViewById(R.id.no_saved_players).setVisibility(View.VISIBLE);
		}
		
	    builder.setView(view)
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   LoadPlayerDialog.this.getDialog().cancel();
	               }
	           });      
	    return builder.create();
	}
}
