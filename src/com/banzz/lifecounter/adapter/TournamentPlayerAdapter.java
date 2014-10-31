package com.banzz.lifecounter.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.TournamentPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jourdang on 10/7/13.
 */
public class TournamentPlayerAdapter implements ListAdapter
{
    private ArrayList<TournamentPlayer> players = new ArrayList<TournamentPlayer>();
    private TournamentPlayerListener listener;
    HashMap<Integer, EditText> nameFields = new HashMap<Integer, EditText>();

    public interface TournamentPlayerListener
    {
        void playerListChanged(ArrayList<TournamentPlayer> players);
    }

    public TournamentPlayerAdapter(TournamentPlayerListener listener, ArrayList<TournamentPlayer> players)
    {
        this.listener = listener;
        this.players = players;
    }

    @Override
    public int getCount()
    {
        return players.size();
    }

    @Override
    public Object getItem(int index)
    {
        return players.get(index);
    }

    @Override
    public long getItemId(int index)
    {
        return players.get(index) == null ? -1 : players.get(index).getId();
    }

    @Override
    public int getItemViewType(int arg0)
    {
        return 0;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup viewGroup)
    {
        //TODO make this less horrible (listener cast to Activity)
        LayoutInflater inflater = ((Activity)listener).getLayoutInflater();
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.start_tournament_player_item, null);
        }

        final TournamentPlayer player = players.get(index);

        EditText userName = (EditText) view.findViewById(R.id.start_player_name);
        if (nameFields.get(player.getId()) == null)
        {
            nameFields.put(player.getId(), userName);
        }
        else
        {
            userName = nameFields.get(player.getId());
        }

        userName.setText(player.getName());
        final int theIndex = index;
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void afterTextChanged(Editable editable) {
                players.get(theIndex).setName(editable.toString());
            }
        });

        return view;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty()
    {
        return players == null || players.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver arg0)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver arg0)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }
}
