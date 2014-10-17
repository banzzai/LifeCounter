package com.banzz.lifecounter.activities;

import com.banzz.lifecounter.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class LifeCounterDialog extends Dialog {
	protected Context mContext;

	public interface CloseWizardDialogListener {
		public void onDismissWizard(final boolean neverShow);
	}
	
	public LifeCounterDialog(final Context context)
    {
        super(context, R.style.custom_dialog_theme);
        mContext = context;
        
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
