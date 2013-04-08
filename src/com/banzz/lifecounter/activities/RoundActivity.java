package com.banzz.lifecounter.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Match;
import com.banzz.lifecounter.commons.RoundAdapter;
import com.banzz.lifecounter.commons.TournamentPlayer;
import com.banzz.lifecounter.utils.Utils.Constants;

public class RoundActivity extends Activity {

	private ArrayList<TournamentPlayer> playerList = new ArrayList<TournamentPlayer>();
	private ArrayList<Match> matches = new ArrayList<Match>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_round);
		
		playerList = getIntent().getParcelableArrayListExtra(Constants.KEY_TOURNAMENT_PLAYERS);
		
		makePairings();
		
		ListView players = (ListView) findViewById(R.id.round_list);
		players.setAdapter(new RoundAdapter(this, matches));
	}
	
	private void makePairings() {
		Collections.sort(playerList, new Comparator<TournamentPlayer>() {
				@Override
				public int compare(TournamentPlayer player1, TournamentPlayer player2) {
					return (player2.getScore() == player1.getScore()) ? player2.getPercentage() - player1.getPercentage() : player2.getScore() - player1.getScore();
				}
			}
		);
		
		// Not sure what to do when we looped; for now we'll stop
		int attempt = 0;
		
		if (playerList.size() % 2 == 1) {
			// Giving a bye to the last player
			playerList.remove(playerList.size() -1);
		}
		
		int pairingNeeded = playerList.size() / 2;
		
		while (matches.size() < pairingNeeded && attempt <= playerList.size()) {
			matchUp();
			
			attempt++;
		}
	}
	
	private void matchUp() {
		TournamentPlayer currentPlayer = playerList.get(0);
		int opponentIndex = 1; 

		while (opponentIndex < playerList.size()) {
			if (!currentPlayer.getPastOpponents().contains(playerList.get(opponentIndex).getId())) 
			{
				matches.add(new Match(playerList.get(0), playerList.get(opponentIndex)));
				playerList.remove(opponentIndex);
				playerList.remove(0);
				return;
			}
			opponentIndex++;
		}
	}
}
