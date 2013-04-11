package com.banzz.lifecounter.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Game;
import com.banzz.lifecounter.commons.Match;
import com.banzz.lifecounter.commons.RoundAdapter;
import com.banzz.lifecounter.commons.TournamentPlayer;
import com.banzz.lifecounter.utils.Utils.Constants;

public class RoundActivity extends Activity {

	private ArrayList<TournamentPlayer> playerList = new ArrayList<TournamentPlayer>();
	private ArrayList<Match> matches = new ArrayList<Match>();
	private HashMap<Integer, Game> games = new HashMap<Integer, Game>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = getLayoutInflater().inflate(R.layout.activity_round, null);
		
		playerList = getIntent().getParcelableArrayListExtra(Constants.KEY_TOURNAMENT_PLAYERS);
		
		makePairings();
		for (Match match: matches) {
			games.put(match.player1.getId(), new Game(match.player2.getId(), 0, 0, 0));
			games.put(match.player2.getId(), new Game(match.player1.getId(), 0, 0, 0));
		}
		
		ListView players = (ListView) view.findViewById(R.id.round_list);
		players.setAdapter(new RoundAdapter(this, matches, games));
		
		Button okButton = (Button) view.findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				validateRound();
			}
		});
		
		setContentView(view);
	}
	
	private void validateRound() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
	    dialog.setTitle(this.getString(R.string.Validate));
	    String message = this.getString(R.string.validate_round);
	    	   
	    dialog.setMessage(message);
	    dialog.setCancelable(true);
	    dialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int buttonId) {
//	        	Intent applyIntent = new Intent();
//	        	applyIntent.putExtra(Constants.KEY_PLAYER_ONE, players[Constants.PLAYER_ONE]);
//	        	applyIntent.putExtra(Constants.KEY_PLAYER_TWO, players[Constants.PLAYER_TWO]);
//	        	setResult(Activity.RESULT_OK, applyIntent);
//				finish();
	        }
	    });
	    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int buttonId) {
	    		dialog.dismiss();        
	    	}
	    });

	    dialog.show();
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
