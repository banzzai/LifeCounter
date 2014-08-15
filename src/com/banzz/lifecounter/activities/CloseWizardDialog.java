package com.banzz.lifecounter.activities;

import com.banzz.lifecounter.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

public class CloseWizardDialog extends DialogFragment {
	CloseWizardDialogListener mListener;
	private CheckBox mNeverShowBox;

	public void setListener(CloseWizardDialogListener mListener) {
		this.mListener = mListener;
	}

	public interface CloseWizardDialogListener {
		public void onDismissWizard(final boolean neverShow);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.close_wizard_dialog, null);
	    mNeverShowBox = (CheckBox) view.findViewById(R.id.never_show_checkbox);
	    
		builder.setView(view)
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   CloseWizardDialog.this.getDialog().cancel();
	               }
	           })
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   mListener.onDismissWizard(mNeverShowBox.isChecked());
	                   dismiss();
	               }
	           });   
	    return builder.create();
	}
}
