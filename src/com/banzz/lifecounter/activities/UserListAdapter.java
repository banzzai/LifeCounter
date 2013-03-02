package com.banzz.lifecounter.activities;

import java.util.List;

import com.banzz.lifecounter.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class UserListAdapter implements ListAdapter {

	private Player[] mUsers;
	private Context mContext;

	public UserListAdapter(Player[] users, Context context) {
		mUsers = users;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mUsers.length;
	}

	@Override
	public Object getItem(int position) {
		return mUsers[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((TwoPlayerActivity)mContext).getLayoutInflater();
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.user_list_item, null);
		}

		TextView userName = (TextView) view.findViewById(R.id.user_name);
		userName.setText(mUsers[position].getName());
		
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
		return mUsers == null || mUsers.length == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}
}
