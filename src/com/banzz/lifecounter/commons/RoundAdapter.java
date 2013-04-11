package com.banzz.lifecounter.commons;

import java.util.ArrayList;
import java.util.HashMap;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.banzz.lifecounter.R;

public class RoundAdapter implements ListAdapter {
	private ArrayList<Match> matches = new ArrayList<Match>();
	// Should be refactor to merge matches and games
	HashMap<Integer, Game> mGames;
	HashMap<Integer, EditText> ScoreFields = new HashMap<Integer, EditText>();
	private Context mContext;
	
	public RoundAdapter(Context context, ArrayList<Match> matches, HashMap<Integer, Game> games) {
		// This is obviously a missuse of the adapter, but heh
		this.matches = matches;
		this.mContext = context;
		this.mGames = games;
	}
	
	@Override
	public int getCount() {
		return matches.size();
	}

	@Override
	public Game getItem(int index) {
		return mGames.get(0);
	}

	@Override
	public long getItemId(int index) {
		return matches.get(index) == null ? -1 : matches.get(index).player1.getId();
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup viewGroup) {
		LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.round_user_item, null);
		}
		
		final TournamentPlayer player1 = matches.get(index).player1;
		final TournamentPlayer player2 = matches.get(index).player2;
		
		// SECOND PLAYER
		EditText userName2 = (EditText) view.findViewById(R.id.player2_name);
		userName2.setText("Vs. " + player2.getName());
		// I don't like it, but it's not easy having values of a row in an adapter modified from another row
		final EditText scoreCount2 = (EditText) view.findViewById(R.id.player2_score);
		if (ScoreFields.get(player2.getId()) == null) {
			ScoreFields.put(player2.getId(), scoreCount2);
			scoreCount2.setText("0-0-0");
		}
		
		// FIRST PLAYER
		EditText userName = (EditText) view.findViewById(R.id.player1_name);
		userName.setText(player1.getName());
		final WheelView winWheel = (WheelView) view.findViewById(R.id.player1_wins);
		winWheel.setViewAdapter(new RoundWheelAdapter(mContext, 0, 2, "W"));
		final WheelView lossWheel = (WheelView) view.findViewById(R.id.player1_losses);
		lossWheel.setViewAdapter(new RoundWheelAdapter(mContext, 0, 2, "L"));
		final WheelView drawWheel = (WheelView) view.findViewById(R.id.player1_draws);
		drawWheel.setViewAdapter(new RoundWheelAdapter(mContext, 0, 9, "D"));
		
		winWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mGames.get(player1.getId()).setWins(newValue);
				mGames.get(player2.getId()).setLosses(newValue);
				
				ScoreFields.get(player2.getId()).setText(getScoreWithLosses(mGames.get(player2.getId()), newValue));
			}
		});
		lossWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mGames.get(player1.getId()).setLosses(newValue);
				mGames.get(player2.getId()).setWins(newValue);
				
				ScoreFields.get(player2.getId()).setText(getScoreWithWins(mGames.get(player2.getId()), newValue));
			}
		});
		drawWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mGames.get(player1.getId()).setDraws(newValue);
				mGames.get(player2.getId()).setDraws(newValue);
				
				ScoreFields.get(player2.getId()).setText(getScoreWithDraws(mGames.get(player2.getId()), newValue));
			}
		});
		
		return view;
	}
	
	private CharSequence getScoreWithWins(Game game, int newValue) {
		return newValue+"-"+game.getLosses()+"-"+game.getDraws();
	}
	
	private CharSequence getScoreWithLosses(Game game, int newValue) {
		return game.getWins()+"-"+newValue+"-"+game.getDraws();
	}
	
	private CharSequence getScoreWithDraws(Game game, int newValue) {
		return game.getWins()+"-"+game.getLosses()+"-"+newValue;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		return matches == null || matches.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		// TODO Auto-generated method stub
	}

	private class RoundWheelAdapter extends AbstractWheelTextAdapter {
		private final int mMinimum;
		private final int mMaximum;
		private final String mBonusText;
		
		public RoundWheelAdapter(Context context, int minValue, int maxValue, String bonusText) {
			super(context, R.layout.round_wheel_item, NO_RESOURCE);
	        mMinimum = minValue;
	        mMaximum = maxValue;
	        mBonusText = bonusText;
	        setItemTextResource(R.id.round_wheel_count);
		}
		
		@Override
	    public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
	        TextView bonusText = (TextView) view.findViewById(R.id.round_wheel_text);
	        bonusText.setText(mBonusText);
	        
	        return view;
		}
		
		@Override
	    public int getItemsCount() {
	        return mMaximum - mMinimum + 1;
	    }
	    
	    @Override
	    protected CharSequence getItemText(int index) {
	        return String.format("%d", mMinimum + index);
	    }
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
