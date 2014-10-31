package com.banzz.lifecounter.dialog;

import com.banzz.lifecounter.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

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
	
	/**
	 * Copy the visibility of the Activity that has started the dialog {@link mActivity}. If the
	 * activity is in Immersive mode the dialog will be in Immersive mode too and vice versa.
	 */
	@SuppressLint("NewApi")
	private void copySystemUiVisibility() {
	    if (android.os.Build.VERSION.SDK_INT >= 19)
	    {
	        getWindow().getDecorView().setSystemUiVisibility(
	                ((Activity)mContext).getWindow().getDecorView().getSystemUiVisibility());
	    }
	}
	
	/**
	 * An hack used to show the dialogs in Immersive Mode (that is with the NavBar hidden). To
	 * obtain this, the method makes the dialog not focusable before showing it, change the UI
	 * visibility of the window like the owner activity of the dialog and then (after showing it)
	 * makes the dialog focusable again.
	 */
	@Override
	public void show() {
	    // Set the dialog to not focusable.
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
	            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

	    copySystemUiVisibility();

	    // Show the dialog with NavBar hidden.
	    super.show();

	    // Set the dialog to focusable again.
	    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
	}
}
