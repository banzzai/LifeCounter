package com.banzz.lifecounter.commons;

import java.util.ArrayList;
import java.util.HashMap;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.banzz.lifecounter.R;

@SuppressLint("UseSparseArrays")
public class RoundAdapter implements ListAdapter {
	private ArrayList<Match> matches = new ArrayList<Match>();
	// Should be refactor to merge matches and games
	HashMap<Integer, Game> mGames;
	HashMap<Integer, TextView> ScoreFields = new HashMap<Integer, TextView>();
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
		TextView userName2 = (TextView) view.findViewById(R.id.player2_name);
		userName2.setText("Vs. " + player2.getName());
		// I don't like it, but it's not easy having values of a row in an adapter modified from another row
		final TextView scoreCount2 = (TextView) view.findViewById(R.id.player2_score);
		if (ScoreFields.get(player2.getId()) == null) {
			ScoreFields.put(player2.getId(), scoreCount2);
			scoreCount2.setText("0-0");
		}
		
		// FIRST PLAYER
		TextView userName = (TextView) view.findViewById(R.id.player1_name);
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

				writeScoreWithLosses(mGames.get(player2.getId()), newValue, ScoreFields.get(player2.getId()));
			}
		});
		lossWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mGames.get(player1.getId()).setLosses(newValue);
				mGames.get(player2.getId()).setWins(newValue);
				
				writeScoreWithWins(mGames.get(player2.getId()), newValue, ScoreFields.get(player2.getId()));
			}
		});
		drawWheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mGames.get(player1.getId()).setDraws(newValue);
				mGames.get(player2.getId()).setDraws(newValue);
				
				writeScoreWithDraws(mGames.get(player2.getId()), newValue, ScoreFields.get(player2.getId()));
			}
		});
		
		return view;
	}
	
	private int getColor(int wins, int losses) {
		return wins > losses ? mContext.getResources().getColor(R.color.poison) : losses > wins ? mContext.getResources().getColor(R.color.reddism) : mContext.getResources().getColor(R.color.lifeText);
	}
	
	private void writeScore(int wins, int losses, int draws, TextView scoreHolder) {
		scoreHolder.setText(wins+"-"+losses+(draws!=0?"-"+draws:""));
		scoreHolder.setTextColor(getColor(wins, losses));
	}
	
	private void writeScoreWithWins(Game game, int newValue, TextView scoreHolder) {
		writeScore(newValue, game.getLosses(), game.getDraws(), scoreHolder);
	}
	
	private void writeScoreWithLosses(Game game, int newValue, TextView scoreHolder) {
		writeScore(game.getWins(), newValue, game.getDraws(), scoreHolder);
	}
	
	private void writeScoreWithDraws(Game game, int newValue, TextView scoreHolder) {
		writeScore(game.getWins(), game.getLosses(), newValue, scoreHolder);
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
	    
	    @SuppressLint("DefaultLocale")
		@Override
	    protected CharSequence getItemText(int index) {
	        return "" + (mMinimum + index);
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
