package com.banzz.lifecounter.commons;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;

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
		
		EditText userName = (EditText) view.findViewById(R.id.player1_name);
		userName.setText(player1.getName());
		
		EditText userName2 = (EditText) view.findViewById(R.id.player2_name);
		userName2.setText(player2.getName());
		
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

}
