package com.banzz.lifecounter.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.activities.CloseWizardDialog.CloseWizardDialogListener;
import com.banzz.lifecounter.utils.Utils;

public class FrontMenuActivity extends android.app.Activity implements CloseWizardDialogListener {

    private static final int DEFAULT_PLAYER_NUMBER = 6;

    // For the first use wizard
    private ViewFlipper mViewFlipper;
    private RelativeLayout mWizardOverlay;
    private float mWizardFlipperLastX;
    private final int DOT_COUNT = 7;
    private WizardPageParams[] mWizardPageParams;
	private AnimationListener mFlipAction;
	
	private final int NO_FADE = 0;
	private final int FADE_IN = 1;
	private final int FADE_OUT = 2;
	
	private int mOverlayFade = NO_FADE;

	private CloseWizardDialog mCloseWizardDialog;
    
	class WizardPageParams
    {
    	public WizardPageParams(int dotIndex, boolean showOverlay)
    	{
    		mDotIndex = dotIndex;
    		mShowOverlay = showOverlay;
    	}
    	
    	public int mDotIndex;
    	public boolean mShowOverlay;
    }
	
    @Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Utils.initUtils(getApplicationContext());
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.front_menu);
        
        
        Button mButton = (Button) findViewById(R.id.two_player_button);
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), TwoPlayerActivity.class));
			}
		});
        
        Button mSettings = (Button) findViewById(R.id.settings_button);
        mSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			}
		});
        
        Button mHelpUs = (Button) findViewById(R.id.helpus_button);
        mHelpUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), HelpUsActivity.class));
			}
		});
        
        Button mTournament = (Button) findViewById(R.id.tournament_button);
        mTournament.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FrontMenuActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.pick_player_number, null);

                final EditText playerNumber = (EditText) view.findViewById(R.id.player_number);
                playerNumber.setText("" + DEFAULT_PLAYER_NUMBER);
                final Intent startTournamentIntent = new Intent(getApplicationContext(), StartTournamentActivity.class);
                startTournamentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                builder.setMessage(R.string.how_many_players).setTitle(R.string.players);
                builder.setPositiveButton(R.string.load_player_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startTournamentIntent.putExtra(getString(R.string.extra_player_number), -1);
                        startActivity(startTournamentIntent);
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton(R.string.new_player_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startTournamentIntent.putExtra(getString(R.string.extra_player_number), Integer.valueOf(playerNumber.getText().toString()));
                        startActivity(startTournamentIntent);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
			}
		});

        mCloseWizardDialog = new CloseWizardDialog(this);
		mCloseWizardDialog.setListener(this);
        
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(getString(R.string.key_hide_wizard), false)) {
    		initWizard();
        }
        else
        {
        	findViewById(R.id.wizard_view).setVisibility(View.GONE);
        }
    		
        String currentNotesKey = getString(R.string.release_notes_key) + getString(R.string.version_code);
        boolean releaseNotesShown = preferences.getBoolean(currentNotesKey, false);
        if (!releaseNotesShown)
        {
            showReleaseNotes(currentNotesKey);
        }
	}

	private void initWizard()
	{
		mViewFlipper = (ViewFlipper) findViewById(R.id.wizard_flipper);
        mViewFlipper.setOnTouchListener(new OnTouchListener() {
        	@Override
        	public boolean onTouch(View v, MotionEvent event) {
	        	return onTouchEvent(event);
        	}
        });
        
        Button close_wizard = (Button) findViewById(R.id.close_wizard);
		close_wizard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mCloseWizardDialog.show();
			}
		});
        
        // Wizard page params
        mWizardPageParams = new WizardPageParams[]{
        		new WizardPageParams(1, true),
        		new WizardPageParams(1, false),
        		new WizardPageParams(2, true),
        		new WizardPageParams(2, false),
        		new WizardPageParams(3, true),
        		new WizardPageParams(3, false),
        		new WizardPageParams(4, true),
        		new WizardPageParams(4, false),
        		new WizardPageParams(5, true),
        		new WizardPageParams(5, false),
        		new WizardPageParams(6, true),
        		new WizardPageParams(7, true)
        		};
        mWizardOverlay = (RelativeLayout) findViewById(R.id.wizard_overlay);

        mFlipAction = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if (mOverlayFade == FADE_OUT)
				{
					mWizardOverlay.animate().setDuration(300).alpha(0);
				}
				else if (mOverlayFade == FADE_IN)
				{
					mWizardOverlay.animate().setDuration(300).alpha(1);
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				updateDots();
			}
		};
        
		updateDots();
        
        TextView wizTitle = (TextView) findViewById(R.id.wizard_title);
        wizTitle.setText(getString(R.string.app_name).toUpperCase(getResources().getConfiguration().locale));
	}

	@Override
	public void onDismissWizard(final boolean neverShow) {
		if (neverShow)
		{
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			Editor preferenceEditor = preferences.edit();
	    	
	    	preferenceEditor.putBoolean(getString(R.string.key_hide_wizard), true);
	    	preferenceEditor.commit();
		}
		findViewById(R.id.wizard_view).setVisibility(View.GONE);
	}
	
	private boolean isWizardShowing()
	{
		return View.GONE != findViewById(R.id.wizard_view).getVisibility();
	}
	
    private void showReleaseNotes(final String notesKey)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FrontMenuActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.release_notes, null);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String versionString = "1.1.001";
        try
        {
            versionString = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return;
        }

        builder.setTitle(getString(R.string.whats_new, versionString));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                preferences.edit().putBoolean(notesKey, true).commit();
                dialog.dismiss();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
 // Method to handle touch event like left to right swap and right to left swap
 	public boolean onTouchEvent(MotionEvent touchevent) 
 	{
 		switch (touchevent.getAction())
 		{
 			// when user first touches the screen to swap
 			case MotionEvent.ACTION_DOWN: 
 			{
 				if (!isWizardShowing())
 				{
 					return false;
 				}
 				
 				mWizardFlipperLastX = touchevent.getX();
 				return true;
 			}
 			case MotionEvent.ACTION_UP: 
 			{
 				if (!isWizardShowing())
 				{
 					return false;
 				}
 				
 				float currentX = touchevent.getX();
 	
 				// if left to right swipe on screen
 				if (mWizardFlipperLastX < currentX) 
 				{
 					// If no more View/Child to flip
 					if (mViewFlipper.getDisplayedChild() == 0)
 					{
 						break;
 					}
 	
 					// set the required Animation type to ViewFlipper
 					// The Next screen will come in form Left and current Screen will go OUT from Right 
 					mViewFlipper.setInAnimation(this, R.anim.in_from_left);
 					mViewFlipper.setOutAnimation(this, R.anim.out_to_right);
 					
 					mViewFlipper.getInAnimation().setAnimationListener(null);
 					mViewFlipper.getInAnimation().setAnimationListener(mFlipAction);
 					
 					// Setup an animation for the overlay if needed
 					updateOverlayFadeAnimation(false);
 					
 					mViewFlipper.showPrevious();
 				}
 	
 				// if right to left swipe on screen
 				if (mWizardFlipperLastX >= currentX)
 				{
 					if (mViewFlipper.getDisplayedChild() == mViewFlipper.getChildCount() -1)
 					{
 						mCloseWizardDialog.show();
 						break;
 					}
 					
 					// set the required Animation type to ViewFlipper
 					// The Next screen will come in form Right and current Screen will go OUT from Left 
 					mViewFlipper.setInAnimation(this, R.anim.in_from_right);
 					mViewFlipper.setOutAnimation(this, R.anim.out_to_left);

 					mViewFlipper.getInAnimation().setAnimationListener(null);
 					mViewFlipper.getInAnimation().setAnimationListener(mFlipAction);
 					
 					// Setup an animation for the overlay if needed
 					updateOverlayFadeAnimation(true);
 					
 					mViewFlipper.showNext();
 				}
 				break;
 			}
 		}
         return true;
     }
 	
 	// Setup an animation for the overlay based on the current page and the one we're moving to
 	private void updateOverlayFadeAnimation(boolean goingForward)
 	{
 		final boolean currentShowOverlay = mWizardPageParams[mViewFlipper.getDisplayedChild()].mShowOverlay;
 		final boolean nextShowOverlay = mWizardPageParams[mViewFlipper.getDisplayedChild()+(goingForward?1:-1)].mShowOverlay;
 		
 		mOverlayFade = (!currentShowOverlay && nextShowOverlay) ? FADE_IN : (currentShowOverlay && !nextShowOverlay) ? FADE_OUT : NO_FADE;
 	}
 	
 	private void updateDots()
 	{
 		LinearLayout dots =  (LinearLayout) findViewById(R.id.wizard_dots);
 		for (int i=0; i<DOT_COUNT; i++)
 		{
 			// Using i+1 because I want to call my dots #1 to #x and not start with dot 0
 			dots.getChildAt(i).setSelected(mWizardPageParams[mViewFlipper.getDisplayedChild()].mDotIndex==i+1);
 		}
 	}
}
