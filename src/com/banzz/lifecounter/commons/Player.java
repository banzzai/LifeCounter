package com.banzz.lifecounter.commons;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
	String mId;
	String mName;
	private int mColor;
	private boolean mShowButtons;
	private boolean mShowWheel;
	private int mBackGroundId;

	public Player() {
		this("id", "Player");
	}
	
	public Player(String id, String name) {
		this(id, name, -1, false, true, 0);
	}
	
	public Player(String mId, String mName, int mColor, boolean mShowButtons, boolean mShowWheel, int mBackGroundId) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mColor = mColor;
		this.mShowButtons = mShowButtons;
		this.mShowWheel = mShowWheel;
		this.mBackGroundId = mBackGroundId;
	}

	public Player(Player player) {
		super();
		this.mBackGroundId = player.getBackGroundId();
		this.mColor = player.getColor();
		this.mName = player.getName();
		this.mShowButtons = player.showButons();
		this.mShowWheel = player.showWheel();
	}

	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public int getColor() {
		return mColor;
	}

	public boolean showButons() {
		return mShowButtons;
	}

	public boolean showWheel() {
		return mShowWheel;
	}

	public int getBackGroundId() {
		return mBackGroundId;
	}

	public void setColor(int mColor) {
		this.mColor = mColor;
	}

	public void setShowButtons(boolean mShowButtons) {
		this.mShowButtons = mShowButtons;
	}

	public void setShowWheel(boolean mShowWheel) {
		this.mShowWheel = mShowWheel;
	}

	public void setBackGroundId(int mBackGroundId) {
		this.mBackGroundId = mBackGroundId;
	}

	@Override
	public int describeContents() {
		//Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		//http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents/2141166#2141166
		out.writeString(mId);
		out.writeString(mName);
		out.writeInt(mColor);
		out.writeInt(mBackGroundId);
		out.writeBooleanArray(new boolean[]{mShowButtons, mShowWheel});
	}
	
	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
    
    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Player(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mColor = in.readInt();
        mBackGroundId = in.readInt();
        boolean[] myVals = new boolean[2];
        in.readBooleanArray(myVals);
        
        mShowButtons = myVals[0];
        mShowWheel = myVals[1];
    }
}