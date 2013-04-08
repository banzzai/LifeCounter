package com.banzz.lifecounter.commons;

// This should be replaced by Game, which hasn't been designed well enough for that
public class Match {
	public TournamentPlayer player1;
	public TournamentPlayer player2;
	
	public Match(TournamentPlayer player1, TournamentPlayer player2) {
		this.player1 = player1;
		this.player2 = player2;
	}
}
