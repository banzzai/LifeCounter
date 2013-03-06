package com.banzz.lifecounter.activities;

public class Player {
	String mId;
	String mName;
	private int mColor;
	private boolean mShowButtons;
	private boolean mShowWheel;
	private int mBackGroundId;

	public Player(String id, String name) {
		super();
		this.mId = id;
		this.mName = name;
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
}
