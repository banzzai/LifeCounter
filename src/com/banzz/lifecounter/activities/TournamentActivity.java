package com.banzz.lifecounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.TournamentAdapter;
import com.banzz.lifecounter.commons.TournamentPlayer;
import com.banzz.lifecounter.utils.Utils;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TournamentActivity extends Activity {

	private ArrayList<TournamentPlayer> playerList = new ArrayList<TournamentPlayer>();
	private ListView mPlayers;
	private int mRound = 0;
    private TextView mRoundText;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tournament);
        playerList = getIntent().getParcelableArrayListExtra(Constants.KEY_TOURNAMENT_PLAYERS);
        saveAsLastPlayerList();
		
		mPlayers = (ListView) findViewById(R.id.player_list);
		Collections.sort(playerList, new PlayerComparator(true));
		mPlayers.setAdapter(new TournamentAdapter(this, playerList));
		
		mRoundText = (TextView) findViewById(R.id.tournament_round);
		mRoundText.setText(getText(R.string.Round).toString() + " " + mRound);
 		
		Button nextRound = (Button) findViewById(R.id.next_round);
		nextRound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TournamentActivity.this, RoundActivity.class);
				intent.putExtra(Constants.KEY_TOURNAMENT_PLAYERS, playerList);
				startActivityForResult(intent, Constants.REQUEST_NEXT_ROUND);
			}
		});

        Button saveRound = (Button) findViewById(R.id.save_round);
        saveRound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRound();
            }
        });

        Button loadRound = (Button) findViewById(R.id.load_round);
        loadRound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRound();
            }
        });
		
		if (mRound >= playerList.size() - 1) {
			nextRound.setVisibility(View.GONE);
		}
	}

    private void saveARound(int key, boolean saveRoundNumber)
    {
        final Gson gson = new Gson();
        final String json = gson.toJson(playerList);
        
        Utils.setStringPreference(getString(key), json);
        if (saveRoundNumber)
        {
            Utils.setIntPreference(getString(R.string.key_saved_round), mRound);
        }
    }

    private void saveRound()
    {
        saveARound(R.string.key_current_tournament, true);
    }

    private void saveAsLastPlayerList()
    {
        saveARound(R.string.key_last_tournament, false);
    }

    private void loadRound()
    {
        final String load = Utils.getStringPreference(getString(R.string.key_current_tournament), null);

        if (load != null)
        {
            final Gson gson = new Gson();
            final TournamentPlayer[] bob = gson.fromJson(load, (Class<TournamentPlayer[]>) TournamentPlayer[].class);
            playerList = new ArrayList<TournamentPlayer>(Arrays.asList(bob));
            mPlayers.setAdapter(new TournamentAdapter(this, playerList));
            mRound = Utils.getIntPreference(getString(R.string.key_saved_round), 0);
            mRoundText.setText(getText(R.string.Round).toString() + " " + mRound);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
	        case Constants.REQUEST_NEXT_ROUND:
	        	if (resultCode == Activity.RESULT_OK) {
	        		playerList = intent.getParcelableArrayListExtra(Constants.KEY_TOURNAMENT_PLAYERS);
	        		
	        		Collections.sort(playerList, new PlayerComparator(true));
	        		mPlayers.setAdapter(new TournamentAdapter(this, playerList));
	        		
	        		mRound++;
	        		
	        		final TextView roundText = (TextView) findViewById(R.id.tournament_round);
	        		roundText.setText(getText(R.string.Round).toString() + " " + mRound);
	        	}
	        	break;
		}
	}
}
