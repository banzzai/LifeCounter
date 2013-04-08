package com.banzz.lifecounter.commons;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {
	private int opponent;
	private int wins;
	private int losses;
	private int draws;
	
	public Game(int opponent, int wins, int losses, int draws) {
		super();
		this.opponent = opponent;
		this.wins = wins;
		this.losses = losses;
		this.draws = draws;
	}
	
	public int getOpponent() {
		return opponent;
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
	public int getMatchCount() {
		return wins+losses+draws;
	}
	
	public boolean isWin() {
		return wins > losses;
	}
	
	public boolean isLoss() {
		return losses > wins;
	}
	
	public boolean isDraw() {
		return losses == wins;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		//http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents/2141166#2141166
		out.writeInt(opponent);
		out.writeInt(wins);
		out.writeInt(losses);
		out.writeInt(draws);
	}
	
	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
    
    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Game(Parcel in) {
        opponent = in.readInt();
        wins = in.readInt();
        losses = in.readInt();
        draws = in.readInt();
    }
}