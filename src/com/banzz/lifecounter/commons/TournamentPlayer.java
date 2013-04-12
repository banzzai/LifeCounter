package com.banzz.lifecounter.commons;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class TournamentPlayer implements Parcelable {
	private int id;
	private String name;
	private ArrayList<Game> results = new ArrayList<Game>();
	private int gameCount = 0;
	private float percentage = 0f;
	private int score = 0;
	private int wins = 0;
	private int losses = 0;
	private int draws = 0;
	
	public TournamentPlayer(int id, String name, ArrayList<Game> results) {
		super();
		this.id = id;
		this.name = name;
		this.results = results;
		calculate();
	}
	
	public ArrayList<Game> getResults() {
		return results;
	}
	public void setResults(ArrayList<Game> results) {
		this.results = results;
		calculate();
	}
	public void addResult(Game game) {
		this.results.add(game);
		calculate();
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getGameCount() {
		return gameCount;
	}
	public int getPercentage() {
		return (int)percentage;
	}
	public int getScore() {
		return score;
	}
	public int getWins() {
		return wins;
	}
	public int getLosses() {
		return losses;
	}
	public int getDraws() {
		return draws;
	}
	public ArrayList<Integer> getPastOpponents() {
		ArrayList<Integer> opponents = new ArrayList<Integer>();
		for(Game game: results) {
			opponents.add(game.getOpponent());
		}
		return opponents;
	}
	
	private void calculate() {
		// Number of games played
		int gCount = 0;
		// Number of games won
		int gWins = 0;
		
		// Total score
		int mScore = 0;
		int mWins = 0;
		int mLosses = 0;
		int mDraws = 0;
		
		for (Game game: results) {
			gCount += game.getMatchCount();
			gWins += game.getWins();
			// Total
			mScore += game.isWin() ? 3 : game.isDraw() ? 1 : 0;
			mWins += game.isWin() ? 1 : 0;
			mLosses += game.isLoss() ? 1 : 0;
			mDraws += game.isDraw() ? 1 : 0;
		}
		
		this.wins = mWins;
		this.losses = mLosses;
		this.draws = mDraws;
		this.score  = mScore;
		this.gameCount = gCount;
		this.percentage  = gCount == 0 ? 100 : 100*gWins / gCount;
	}
	
	public int getMatchCount() {
		return results == null ? 0 : results.size();
	}

	public int getTotalWins() {
		int gWins = 0;
		
		for (Game game: results) {
			gWins += game.getWins();
		}
		return gWins;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		//http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents/2141166#2141166
		out.writeInt(id);
		out.writeString(name);
		out.writeList(results);
	}
	
	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<TournamentPlayer> CREATOR = new Parcelable.Creator<TournamentPlayer>() {
        public TournamentPlayer createFromParcel(Parcel in) {
            return new TournamentPlayer(in);
        }

        public TournamentPlayer[] newArray(int size) {
            return new TournamentPlayer[size];
        }
    };
    
    // example constructor that takes a Parcel and gives you an object populated with it's values
    private TournamentPlayer(Parcel in) {
        id = in.readInt();
        name = in.readString();
        in.readList(results, Game.class.getClassLoader());
        calculate();
    }
}