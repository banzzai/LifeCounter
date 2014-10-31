package com.banzz.lifecounter.dialog;

import com.banzz.lifecounter.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class CloseWizardDialog extends LifeCounterDialog {
	CloseWizardDialogListener mListener;
	private CheckBox mNeverShowBox;

	public void setListener(CloseWizardDialogListener mListener) {
		this.mListener = mListener;
	}

	public interface CloseWizardDialogListener {
		public void onDismissWizard(final boolean neverShow);
	}
	
	public CloseWizardDialog(final Context context)
    {
        super(context);
        this.setContentView(R.layout.close_wizard_dialog);
        
        mNeverShowBox = (CheckBox) findViewById(R.id.never_show_checkbox);
        
        Button yesButton = (Button) findViewById(R.id.close_wizard_yes);
	    yesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onDismissWizard(mNeverShowBox.isChecked());
                dismiss();
			}
		});
	    
	    Button noButton = (Button) findViewById(R.id.close_wizard_no);
	    noButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		        dismiss();
			}
		});
    }
}
