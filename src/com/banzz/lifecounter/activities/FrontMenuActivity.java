package com.banzz.lifecounter.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.banzz.lifecounter.R;

public class FrontMenuActivity extends android.app.Activity {

    private static final int DEFAULT_PLAYER_NUMBER = 6;

    @Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String currentNotesKey = getString(R.string.release_notes_key) + getString(R.string.version_code);
        boolean releaseNotesShown = preferences.getBoolean(currentNotesKey, false);
        if (!releaseNotesShown)
        {
            showReleaseNotes(currentNotesKey);
        }
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
}
