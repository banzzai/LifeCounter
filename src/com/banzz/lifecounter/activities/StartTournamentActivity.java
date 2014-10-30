package com.banzz.lifecounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Game;
import com.banzz.lifecounter.commons.TournamentPlayer;
import com.banzz.lifecounter.utils.TournamentPlayerAdapter;
import com.banzz.lifecounter.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class StartTournamentActivity extends Activity implements TournamentPlayerAdapter.TournamentPlayerListener
{
    TextView mNumberOfPlayers;
    ArrayList<TournamentPlayer> playerlist;
    private ListView mPlayers;
    private int nextId = 7;
    private int MAX_DRAFT_PLAYERS = 10;
    private boolean mPlayerOverload = false;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.start_tournament, null);

        loadLastKnownPlayerList();

        mPlayers = (ListView) view.findViewById(R.id.player_list);
        mPlayers.setAdapter(new TournamentPlayerAdapter(this, playerlist));

        mNumberOfPlayers = (TextView) view.findViewById(R.id.number_of_players);
        updatePlayerNumber();

        Button mValidateButton = (Button) view.findViewById(R.id.validate_players);
        mValidateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(StartTournamentActivity.this, TournamentActivity.class);
                intent.putExtra(Utils.Constants.KEY_TOURNAMENT_PLAYERS, playerlist);
                startActivityForResult(intent, Utils.Constants.REQUEST_NEW_TOURNAMENT);
            }
        });

        setContentView(view);
    }

    private void loadLastKnownPlayerList()
    {
        final String load = Utils.getStringPreference(getString(R.string.key_last_tournament), null);

        int playerTarget = getIntent().getIntExtra(getString(R.string.extra_player_number), -1);
        if (playerTarget > MAX_DRAFT_PLAYERS)
        {
            mPlayerOverload = true;
            playerTarget = MAX_DRAFT_PLAYERS;
        }

        if (load == null || playerTarget != -1)
        {
            if (playerTarget == -1)
            {
                playerTarget = 6;
            }
            playerlist = new ArrayList<TournamentPlayer>();
            for (int i=0; i<playerTarget; i++)
            {
                playerlist.add(new TournamentPlayer(i, "player" + i, new ArrayList<Game>()));
            }
        }
        else
        {
            Gson gson = new Gson();
            TournamentPlayer[] bob = gson.fromJson(load, (Class<TournamentPlayer[]>) TournamentPlayer[].class);
            playerlist = new ArrayList<TournamentPlayer>(Arrays.asList(bob));
        }
    }

    private void updatePlayerNumber()
    {
        mNumberOfPlayers.setText(String.format(getString(R.string.number_of_players), playerlist.size()));
    }

    @Override
    public void playerListChanged(ArrayList<TournamentPlayer> players)
    {
        mPlayers.setAdapter(new TournamentPlayerAdapter(this, playerlist));
        updatePlayerNumber();
    }
}