package com.banzz.lifecounter.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.TournamentPlayer;

public class TournamentAdapter implements ListAdapter {
	private ArrayList<TournamentPlayer> players = new ArrayList<TournamentPlayer>();
	private Context mContext;
	
	public TournamentAdapter(Context context, ArrayList<TournamentPlayer> players) {
		this.players = players;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return players.size();
	}

	@Override
	public Object getItem(int index) {
		return players.get(index);
	}

	@Override
	public long getItemId(int index) {
		return players.get(index) == null ? -1 : players.get(index).getId();
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
			view = inflater.inflate(R.layout.tournament_user_item, null);
		}

		TournamentPlayer player = players.get(index);
		
		EditText userName = (EditText) view.findViewById(R.id.player_name);
		String playerText = "#" + (index + 1) + ". " + player.getName() + " with " + player.getScore() + "pts." + "\n" 
							+ "matches: " + player.getWins() + "-" + player.getLosses() + (player.getDraws() != 0 ? "-" + player.getDraws():"") + " " 
							+ "games: " + player.getTotalWins() + "/" + player.getGameCount() + " (" + player.getPercentage() + "%)";
		userName.setText(playerText);
		
		int red = (int)(255*(index+1)/players.size());
		userName.setBackgroundColor(Color.argb(255, red, 255 - red, 0));
		
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
		return players == null || players.isEmpty();
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
