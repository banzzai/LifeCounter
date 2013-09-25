package com.banzz.lifecounter.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.banzz.lifecounter.R;

public class EditWizardActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_wizard);

        Button close_wizard = (Button) findViewById(R.id.edit_close_wizard);
        close_wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        Button never_show_wizard = (Button) findViewById(R.id.edit_never_show);
        never_show_wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditWizardActivity.this);
                SharedPreferences.Editor preferenceEditor = preferences.edit();
                preferenceEditor.putBoolean(getString(R.string.key_hide_edit_wizard), true);
                preferenceEditor.commit();
                finish();
            }
        });
    }
}
