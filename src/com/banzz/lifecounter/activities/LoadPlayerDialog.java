package com.banzz.lifecounter.activities;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.Utils.Constants;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
	private Player[] mUsers;
	private RadioGroup mRadio;
	private ListView mSelectUser;

	public void setUsers(Player[] users) {
		this.mUsers = users;
	}

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
	    
	    mSelectUser.setAdapter(new UserListAdapter(mUsers, getActivity()));
	    mSelectUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mListener.onValidateLoad(mUsers[position], (mRadio.getCheckedRadioButtonId() == R.id.radio_player_1 ? Constants.PLAYER_ONE : Constants.PLAYER_TWO));
				dismiss();
			}
		});

	    builder.setView(view)
	    // Add action buttons
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   LoadPlayerDialog.this.getDialog().cancel();
	               }
	           });      
	    return builder.create();
	}
}
