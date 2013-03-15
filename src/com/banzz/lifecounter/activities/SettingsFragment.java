package com.banzz.lifecounter.activities;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.Utils.Constants;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        
        // Load the preferences from an XML resource
        int target = arguments.getInt(Constants.KEY_PLAYER_TARGET, Constants.PLAYER_ZERO);
		addPreferencesFromResource(target == Constants.PLAYER_ONE ? R.xml.player1_pref : target == Constants.PLAYER_TWO ? R.xml.player2_pref : R.xml.preferences);
    }
}
