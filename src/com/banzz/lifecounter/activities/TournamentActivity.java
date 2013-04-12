package com.banzz.lifecounter.activities;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Game;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.commons.TournamentAdapter;
import com.banzz.lifecounter.commons.TournamentPlayer;
import com.banzz.lifecounter.utils.Utils.Constants;

public class TournamentActivity extends Activity {

	private ArrayList<TournamentPlayer> playerList = new ArrayList<TournamentPlayer>();
	private ListView mPlayers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tournament);
		
		TournamentPlayer Alex = new TournamentPlayer(0, "Alex", new ArrayList<Game>(){{add(new Game(5, 0, 0, 1));}});  
		playerList.add(Alex);
		TournamentPlayer Basile = new TournamentPlayer(1, "Basile", new ArrayList<Game>(){{add(new Game(4, 2, 0, 0));}});  
		playerList.add(Basile);
		TournamentPlayer David = new TournamentPlayer(2, "David", new ArrayList<Game>(){{add(new Game(3, 2, 1, 0));}});  
		playerList.add(David);
		TournamentPlayer Greg = new TournamentPlayer(3, "Greg", new ArrayList<Game>(){{add(new Game(2, 1, 2, 0));}});  
		playerList.add(Greg);
		TournamentPlayer Olivier = new TournamentPlayer(4, "Olivier", new ArrayList<Game>(){{add(new Game(1, 0, 2, 0));}});  
		playerList.add(Olivier);
		TournamentPlayer Tanisha = new TournamentPlayer(5, "Tanisha", new ArrayList<Game>(){{add(new Game(0, 0, 0, 1));}});  
		playerList.add(Tanisha);
		
//		TournamentPlayer Alex = new TournamentPlayer(0, "Alex", new ArrayList<Game>());  
//		playerList.add(Alex);
//		TournamentPlayer Basile = new TournamentPlayer(1, "Basile", new ArrayList<Game>());  
//		playerList.add(Basile);
//		TournamentPlayer David = new TournamentPlayer(2, "David", new ArrayList<Game>());  
//		playerList.add(David);
//		TournamentPlayer Greg = new TournamentPlayer(3, "Greg", new ArrayList<Game>());  
//		playerList.add(Greg);
//		TournamentPlayer Olivier = new TournamentPlayer(4, "Olivier", new ArrayList<Game>());  
//		playerList.add(Olivier);
//		TournamentPlayer Tanisha = new TournamentPlayer(5, "Tanisha", new ArrayList<Game>());  
//		playerList.add(Tanisha);
		
		mPlayers = (ListView) findViewById(R.id.player_list);
		Collections.sort(playerList, new PlayerComparator());
		mPlayers.setAdapter(new TournamentAdapter(this, playerList));
		
		Button nextRound = (Button) findViewById(R.id.next_round);
		nextRound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TournamentActivity.this, RoundActivity.class);
				intent.putExtra(Constants.KEY_TOURNAMENT_PLAYERS, playerList);
				startActivityForResult(intent, Constants.REQUEST_NEXT_ROUND);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
	        case Constants.REQUEST_NEXT_ROUND:
	        	if (resultCode == Activity.RESULT_OK) {
	        		playerList = intent.getParcelableArrayListExtra(Constants.KEY_TOURNAMENT_PLAYERS);
	        		mPlayers.setAdapter(new TournamentAdapter(this, playerList));
	        	}
	        	break;
		}
	}
}
