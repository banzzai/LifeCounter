package com.banzz.lifecounter.common;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
	private String mId;
	private String mName;
	private int mColor;
	private int mBackGroundId;
	private String mTallBgUrl;
	private String mLargeBgUrl;
	private String mThumbnailUrl;
		
	public Player() {
		this(null, "Player 0", -1, 0, null, null, null);
	}
	
	public Player(String mId, String mName, int mColor, int mBackGroundId, String tallUrl, String largeUrl, String thumbUrl) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mColor = mColor;
		this.mBackGroundId = mBackGroundId;
		this.mTallBgUrl = tallUrl;
		this.mLargeBgUrl = largeUrl;
		this.mThumbnailUrl = thumbUrl;
	}

	public Player(Player player) {
		super();
		this.mBackGroundId = player.getBackGroundId();
		this.mColor = player.getColor();
		this.mName = player.getName();
		this.mTallBgUrl = player.getTallBgUrl();
		this.mLargeBgUrl = player.getLargeBgUrl();
		this.mThumbnailUrl = player.getThumbnailUrl();
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

	public int getBackGroundId() {
		return mBackGroundId;
	}

	public void setColor(int mColor) {
		this.mColor = mColor;
	}

	public void setBackGroundId(int mBackGroundId) {
		this.mBackGroundId = mBackGroundId;
	}

	public String getTallBgUrl() {
		return mTallBgUrl;
	}

	public void setTallBgUrl(String mTallBgUrl) {
		this.mTallBgUrl = mTallBgUrl;
	}

	public String getLargeBgUrl() {
		return mLargeBgUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.mThumbnailUrl = thumbnailUrl;
	}

	public String getThumbnailUrl() {
		return mThumbnailUrl;
	}
	
	public void setLargeBgUrl(String mLargeBgUrl) {
		this.mLargeBgUrl = mLargeBgUrl;
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
		out.writeString(mTallBgUrl);
		out.writeString(mLargeBgUrl);
		out.writeString(mThumbnailUrl);
		out.writeInt(mColor);
		out.writeInt(mBackGroundId);
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
        mTallBgUrl = in.readString();
        mLargeBgUrl = in.readString();
        mThumbnailUrl = in.readString();
        mColor = in.readInt();
        mBackGroundId = in.readInt();
    }
}
