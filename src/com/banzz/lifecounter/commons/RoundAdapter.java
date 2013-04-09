package com.banzz.lifecounter.commons;

import java.util.ArrayList;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.banzz.lifecounter.R;

public class RoundAdapter implements ListAdapter {
	private ArrayList<Match> matches = new ArrayList<Match>();
	private Context mContext;
	
	public RoundAdapter(Context context, ArrayList<Match> matches) {
		this.matches = matches;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return matches.size();
	}

	@Override
	public Object getItem(int index) {
		return matches.get(index);
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

		TournamentPlayer player1 = matches.get(index).player1;
		TournamentPlayer player2 = matches.get(index).player2;
		
		// FIRST PLAYER
		EditText userName = (EditText) view.findViewById(R.id.player1_name);
		userName.setText(player1.getName());

		WheelView winWheel = (WheelView) view.findViewById(R.id.player1_wins);
		winWheel.setViewAdapter(new RoundWheelAdapter(mContext, 0, 3, "W"));
		
		WheelView lossWheel = (WheelView) view.findViewById(R.id.player1_losses);
		lossWheel.setViewAdapter(new RoundWheelAdapter(mContext, 0, 3, "L"));
		
		WheelView drawWheel = (WheelView) view.findViewById(R.id.player1_draws);
		drawWheel.setViewAdapter(new RoundWheelAdapter(mContext, 0, 9, "D"));
		
		// SECOND PLAYER
		EditText userName2 = (EditText) view.findViewById(R.id.player2_name);
		userName2.setText("Vs. " + player2.getName());
		
		WheelView winWheel2 = (WheelView) view.findViewById(R.id.player2_wins);
		winWheel2.setViewAdapter(new RoundWheelAdapter(mContext, 0, 3, "W"));
		
		WheelView lossWheel2 = (WheelView) view.findViewById(R.id.player2_losses);
		lossWheel2.setViewAdapter(new RoundWheelAdapter(mContext, 0, 3, "L"));
		
		WheelView drawWheel2 = (WheelView) view.findViewById(R.id.player2_draws);
		drawWheel2.setViewAdapter(new RoundWheelAdapter(mContext, 0, 9, "D"));
		
		return view;

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

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int arg0) {
		return true;
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
}
