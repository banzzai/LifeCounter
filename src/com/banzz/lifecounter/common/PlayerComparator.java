package com.banzz.lifecounter.common;

import java.util.Comparator;

public class PlayerComparator implements Comparator<TournamentPlayer> {
	private boolean useTieBreakers = false;
	
	public PlayerComparator(boolean useTieBreakers) {
		this.useTieBreakers = useTieBreakers;
	}
	
	@Override
	public int compare(TournamentPlayer player1, TournamentPlayer player2) {
		if (useTieBreakers) {
			return (player2.getScore() == player1.getScore()) ? player2.getPercentage() - player1.getPercentage() : player2.getScore() - player1.getScore();
		}
		else {
			return player2.getScore() - player1.getScore();
		}
	}
}
