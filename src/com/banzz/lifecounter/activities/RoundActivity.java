package com.banzz.lifecounter.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

@SuppressLint("UseSparseArrays")
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
	    dialog.setTitle(this.getString(R.string.Submit));
	    String message = this.getString(R.string.validate_round);
	    
	    for (Match match: matches) {
	    	Game game1 = games.get(match.player1.getId());
	    	Game game2 = games.get(match.player2.getId());
	    	message += "\n" + match.player1.getName() + "("+game1.getWins()+"-"+game1.getLosses()+(game1.getDraws()!=0?"-"+game1.getDraws():"")+")"
	    			+" / " + match.player2.getName() + "("+game2.getWins()+"-"+game2.getLosses()+(game2.getDraws()!=0?"-"+game2.getDraws():"")+")";
	    }
	    
	    dialog.setMessage(message);
	    dialog.setCancelable(true);
	    dialog.setButton(DialogInterface.BUTTON_POSITIVE, this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int buttonId) {
	        	Intent submitIntent = new Intent();
	        	for (TournamentPlayer player: playerList) {
	        		player.addResult(games.get(player.getId()));
	        	}
	        	submitIntent.putParcelableArrayListExtra(Constants.KEY_TOURNAMENT_PLAYERS, playerList);
	        	setResult(Activity.RESULT_OK, submitIntent);
				finish();
	        }
	    });
	    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int buttonId) {
	    		dialog.dismiss();        
	    	}
	    });

	    dialog.show();
	}
	
	@SuppressWarnings("unchecked")
	private void makePairings() {
		ArrayList<TournamentPlayer> tempList = (ArrayList<TournamentPlayer>) playerList.clone();
		
		//For the first round, randomize
		if (tempList.get(0).getPastOpponents().isEmpty()) {
			Collections.shuffle(tempList);
		} else {
			Collections.sort(tempList, new PlayerComparator(false));
		}
		
		// Not sure what to do when we looped; for now we'll stop
		int attempt = 0;
		
		if (tempList.size() % 2 == 1) {
			// Giving a bye to the last player
			Log.e("PAIRING", "Giving a bye to " + tempList.get(tempList.size() -1).getName());
			tempList.remove(tempList.size() -1);
		}
		
		int pairingNeeded = tempList.size() / 2;
		
		while (matches.size() < pairingNeeded && attempt <= tempList.size()) {
			Log.e("PAIRING", "Attempt #" + attempt);
			matchUp(tempList);
			
			attempt++;
		}
	}
	
	private void matchUp(ArrayList<TournamentPlayer> tempList) {
		if (tempList.size() == 2) {
			Log.e("PAIRING", "No choice left, picking " + tempList.get(0).getName() + " and " + tempList.get(1).getName());
			if (tempList.get(0).getPastOpponents().contains(tempList.get(1).getId())) {
				Log.e("PAIRING", "Though they already played");
			}
			matches.add(new Match(tempList.get(0), tempList.get(1)));
			tempList.remove(1);
			tempList.remove(0);
			return;
		}
		
		TournamentPlayer currentPlayer = tempList.get(0);
		//int opponentIndex = 1;
		int targetScore = currentPlayer.getScore();
		boolean picked = false;
		
		Log.e("PAIRING", "Picking for " + currentPlayer.getName() + ",  score " + currentPlayer.getScore());
		
		while (!picked) {
			Log.e("PAIRING", "Checking out score " + targetScore);	
			ArrayList<TournamentPlayer> candidates = new ArrayList<TournamentPlayer>();
			for (TournamentPlayer otherPlayer: tempList) {
				if (otherPlayer.getScore() == targetScore) {
					if (!currentPlayer.getPastOpponents().contains(otherPlayer.getId()) && !otherPlayer.equals(currentPlayer)) {
						Log.e("PAIRING", otherPlayer.getName() + " good candidate.");
						candidates.add(otherPlayer);
					}
					else {
						Log.e("PAIRING", otherPlayer.getName() + " already played against.");
					}
				}
				else
				{
					if (otherPlayer.getScore() > targetScore) {
						// We have already moved on to lower scores
						continue;
					}
					else if (candidates.isEmpty()) {
						Log.e("PAIRING", "New target score " + otherPlayer.getScore());
						targetScore = otherPlayer.getScore();
					}
					break;
				}
			}
			
			if (!candidates.isEmpty()) {
				// Should be able to take a pick now
				Collections.shuffle(candidates);
				TournamentPlayer pickedPlayer = candidates.get(0);
				Log.e("PAIRING", "Picking at random out of " + candidates.size() + ": choosing " + pickedPlayer.getName());
				matches.add(new Match(currentPlayer, pickedPlayer));
				tempList.remove(pickedPlayer);
				tempList.remove(currentPlayer);
				picked = true;
			}
		}
		
//		while (opponentIndex < tempList.size()) {
//			if (!currentPlayer.getPastOpponents().contains(tempList.get(opponentIndex).getId())) 
//			{
//				matches.add(new Match(tempList.get(0), tempList.get(opponentIndex)));
//				tempList.remove(opponentIndex);
//				tempList.remove(0);
//				return;
//			}
//			opponentIndex++;
//		}
	}
}
