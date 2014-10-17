package com.banzz.lifecounter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.utils.SystemUiHider;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.banzz.lifecounter.utils.VerticalSeekBar;

import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class TwoPlayerActivity extends Activity implements OnClickListener, LoadPlayerDialog.LoadPlayerDialogListener,
                                                            ToolboxMenuDialog.ToolBoxDialogListener {
	private static final String TAG = TwoPlayerActivity.class.getName();
	
	public static int LIFE_START = 20;
	public static int PLAYER_NUMBER = 2;
	
	public static String SELECT_PLAYERS_EXTRA = "select_players";
	
	private int player1_back_number = 0;
	private int player2_back_number = 1;
	private int[] playerBacks = {-1, -1};
	
	private Player player_1;
	private Player player_2;
	private Player[] players = {null, null};
	
	private int backgrounds_ids[] =
            new int[] {R.drawable.azorius, R.drawable.boros, R.drawable.dimir, R.drawable.golgari, R.drawable.rakdos,
						R.drawable.gruul, R.drawable.izzet, R.drawable.orzhov, R.drawable.selesnya, R.drawable.simic};
    
	private TextView mEditName1;
	private TextView mEditName2;
	private TextView[] editNames = {null, null};
	
	private TextView mBigLife1;
	private TextView mBigLife2;
	private TextView[] bigLifes = {null, null};

	private TextView mPlus1;
	private TextView mPlus2;
	private TextView[] pluses = {null, null};

	private TextView mMinus1;
	private TextView mMinus2;
	private TextView[] minuses = {null, null};
	
	private ImageView player1_background;
	private ImageView player2_background;
	private ImageView[] backgrounds = {null, null};
	
	private RelativeLayout player2_screen;
	
	private WakeLock wl;
	private boolean mBackGroundLock = false;
	private boolean mShowPoison = true;
    private boolean mShowPlaque = true;
	private int mOrientation = -1;
	
	private VerticalSeekBar poisonBar1;
	private VerticalSeekBar poisonBar2;
	private TextView poisonCount1;
	private TextView poisonCount2;
	private int poisonValue1 = 0;
	private int poisonValue2 = 0;
    private ToolboxMenuDialog mToolBoxDialog;
    
    @Override
	protected void onPause() {
		super.onPause();
		if (wl != null && wl.isHeld()) {
			wl.release();	
		}
	};
		
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mShowPoison = preferences.getBoolean(getString(R.string.key_show_poison), false);
        mShowPlaque		= preferences.getBoolean(getString(R.string.key_show_plaque), true);

        try {
			LIFE_START = Integer.valueOf(preferences.getString(getString(R.string.key_life_start), "20"));
			Integer.valueOf(preferences.getString(getString(R.string.key_life_start), "20"));
		} catch (NumberFormatException e) {
			String val = preferences.getString(getString(R.string.key_life_start), "20");
			Toast punny = Toast.makeText(this, "Oh you want " + val + " life, huh? You'll get 20 :p", Toast.LENGTH_SHORT);
			punny.show();
			Toast serious = Toast.makeText(this, "Seriously though, check your settings", Toast.LENGTH_LONG);
			serious.show();
		}
		
		checkOrientation(getResources().getConfiguration().orientation);
		updateUI();
		
		if (wl == null) {
			//Locking the screen ON
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "LifeCounter lock");
		}
		
		if (wl == null) {
			return;
		} else if (!wl.isHeld()) {
			wl.acquire();
		}
	};
	
	private boolean checkOrientation(int orientation) {
		if (orientation != mOrientation) {
			mOrientation = orientation;
			return true;
		}
		return false;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_two_player);
		
        mEditName1	= (TextView) findViewById(R.id.edit_player1);
        mEditName1.setOnClickListener(this);

        mBigLife1 	= (TextView) findViewById(R.id.big_life_1);
        mBigLife1.setOnClickListener(this);
        mPlus1 		= (TextView) findViewById(R.id.plus_1);
        mPlus1.setOnClickListener(this);
        mMinus1		= (TextView) findViewById(R.id.minus_1);
        mMinus1.setOnClickListener(this);
        
        mEditName2	= (TextView) findViewById(R.id.edit_player2);
        mEditName2.setOnClickListener(this);

        mBigLife2 	= (TextView) findViewById(R.id.big_life_2);
        mBigLife2.setOnClickListener(this);
        mPlus2 		= (TextView) findViewById(R.id.plus_2);
        mPlus2.setOnClickListener(this);
        mMinus2		= (TextView) findViewById(R.id.minus_2);
        mMinus2.setOnClickListener(this);
        
		player1_background = (ImageView) findViewById(R.id.background_player1);
		player1_background.setOnClickListener(this);
		player2_background = (ImageView) findViewById(R.id.background_player2);
		player2_background.setOnClickListener(this);
		
		player2_screen = (RelativeLayout) findViewById(R.id.player_two_screen);
		
		mBigLife1.setText(""+LIFE_START);
		mBigLife2.setText(""+LIFE_START);
		
		poisonBar1 = (VerticalSeekBar) findViewById(R.id.poisonBar1);
		poisonBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
                poisonValue1 = seekBar.getProgress();
				poisonCount1.setText(""+seekBar.getProgress());
				return;
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				return;
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				return;				
			}
		});
		poisonCount1 = (TextView) findViewById(R.id.poisonCount1);
		poisonCount1.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (poisonValue1 < 10)
                {
                    poisonValue1++;
                }
                poisonBar1.setProgressAndThumb(poisonValue1);
                poisonCount1.setText(""+poisonValue1);
            }
        });

		poisonBar2 = (VerticalSeekBar) findViewById(R.id.poisonBar2);
		poisonBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
                poisonValue2 = seekBar.getProgress();
				poisonCount2.setText(""+seekBar.getProgress());
				return;
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				return;
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				return;				
			}
		});
		poisonCount2 = (TextView) findViewById(R.id.poisonCount2);
        poisonCount2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (poisonValue2 < 10)
                {
                    poisonValue2++;
                }
                poisonBar2.setProgressAndThumb(poisonValue2);
                poisonCount2.setText(""+poisonValue2);
            }
        });
		
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mBackGroundLock = preferences.getBoolean(getString(R.string.key_background_lock), false);
		mShowPoison		= preferences.getBoolean(getString(R.string.key_show_poison), false);
        mShowPlaque		= preferences.getBoolean(getString(R.string.key_show_plaque), true);
        player1_back_number = preferences.getInt(getString(R.string.key_back1), 0);
		player2_back_number = preferences.getInt(getString(R.string.key_back2), 1);
		setLife(Constants.PLAYER_ONE, preferences.getInt(getString(R.string.key_life1), 20));
		setLife(Constants.PLAYER_TWO, preferences.getInt(getString(R.string.key_life2), 20));
		player2_rotate(preferences.getBoolean(getString(R.string.key_rotate_player2), false));
		
		initArrays();
		updateUI();
		
		//if (savedInstanceState != null && savedInstanceState.getBoolean(SELECT_PLAYERS_EXTRA, false))
		{
			final LoadPlayerDialog loadDialog = new LoadPlayerDialog(TwoPlayerActivity.this);
	        loadDialog.setListener(TwoPlayerActivity.this);
	        loadDialog.show();
		}
	        
        mToolBoxDialog = new ToolboxMenuDialog(TwoPlayerActivity.this, TwoPlayerActivity.this);
        Button menuButton = (Button) findViewById(R.id.central_button);
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolBoxDialog.show(getFragmentManager(), getString(R.string.pick_color));
            }
        });
    }

	private void initArrays() {
		playerBacks[Constants.PLAYER_ONE] = player1_back_number;
		playerBacks[Constants.PLAYER_TWO] = player2_back_number;

		players[Constants.PLAYER_ONE] = player_1;
		players[Constants.PLAYER_TWO] = player_2;

		editNames[Constants.PLAYER_ONE] = mEditName1;
		editNames[Constants.PLAYER_TWO] = mEditName2;

		bigLifes[Constants.PLAYER_ONE] = mBigLife1;
		bigLifes[Constants.PLAYER_TWO] = mBigLife2;
		
		pluses[Constants.PLAYER_ONE] = mPlus1;
		pluses[Constants.PLAYER_TWO] = mPlus2;

		minuses[Constants.PLAYER_ONE] = mMinus1;
		minuses[Constants.PLAYER_TWO] = mMinus2;
		
		backgrounds[Constants.PLAYER_ONE] = player1_background;
		backgrounds[Constants.PLAYER_TWO] = player2_background;
	}

	//This function should just update what shows on screen, and not change any value. This is not starting a new game!
	private void updateUI() {
		Log.d(TAG, "updateUI()");
		if (mShowPoison) {
			poisonBar1.setVisibility(View.VISIBLE);
			poisonBar2.setVisibility(View.VISIBLE);
			poisonCount1.setVisibility(View.VISIBLE);
			poisonCount2.setVisibility(View.VISIBLE);
			poisonCount1.setText(""+poisonValue1);
			poisonCount2.setText(""+poisonValue2);
		} else {
			poisonBar1.setVisibility(View.GONE);
			poisonBar2.setVisibility(View.GONE);
			poisonCount1.setVisibility(View.GONE);
			poisonCount2.setVisibility(View.GONE);
		}

        if (mShowPlaque)
        {
            mBigLife1.setBackgroundResource(R.drawable.box);
            mBigLife2.setBackgroundResource(R.drawable.box);
        }
        else
        {
            if (Build.VERSION.SDK_INT < 16)
            {
                mBigLife1.setBackgroundDrawable(null);
                mBigLife2.setBackgroundDrawable(null);
            }
            else
            {
                mBigLife1.setBackground(null);
                mBigLife2.setBackground(null);
            }
        }
		
		for (int i=0; i<PLAYER_NUMBER; i++) {
			updatePlayerUI(i);
		}
	}
	
	private void updatePlayerUI(int player_number) {
		if (player_number > PLAYER_NUMBER) {
			return;
		}
		
		boolean loadPlayer = players[player_number] != null;
		if (players[player_number] == null) {
			//Loading default into Player x
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			players[player_number] = new Player();
            int defaultColor = getResources().getColor(R.color.lifeText);
			players[player_number].setColor(preferences.getInt(getString(R.string.key_color), defaultColor));
			players[player_number].setShowButtons(preferences.getBoolean(getString(R.string.key_show_buttons), true));
			players[player_number].setShowWheel(preferences.getBoolean(getString(R.string.key_show_wheels), false));
			players[player_number].setBackGroundId(player_number);
			players[player_number].setName("Player " + (player_number + 1));
		}
		
		int color = players[player_number].getColor();

		bigLifes[player_number].setTextColor(color);
		minuses[player_number].setTextColor(color);
		pluses[player_number].setTextColor(color);
		editNames[player_number].setTextColor(color);
		editNames[player_number].setText(loadPlayer ? players[player_number].getName() : "Player");
		
		if (players[player_number].showButons()) {
			pluses[player_number].setVisibility(View.VISIBLE);
			minuses[player_number].setVisibility(View.VISIBLE);
        } else {
        	pluses[player_number].setVisibility(View.GONE);
			minuses[player_number].setVisibility(View.GONE);
        }
		
		String tallUrl = players[player_number].getTallBgUrl();
		String largeUrl = players[player_number].getLargeBgUrl();
		if (tallUrl != null || largeUrl != null) {
			backgrounds[player_number].setImageURI(Uri.parse(mOrientation == Configuration.ORIENTATION_PORTRAIT ? 
					tallUrl != null ? tallUrl : largeUrl
					: largeUrl != null ? largeUrl : tallUrl));
			backgrounds[player_number].setScaleType(ScaleType.FIT_XY);
		} else {
			backgrounds[player_number].setImageResource(backgrounds_ids[players[player_number].getBackGroundId()]);
			backgrounds[player_number].setScaleType(ScaleType.CENTER_CROP);
		}
	}

	@Override
	public void onClick(View clickedView) {
		if (clickedView.equals(mBigLife1)) {
			//Clicking on player 1 life total
		} else if (clickedView.equals(mBigLife2)) {
			//Clicking on player 2 life total
		}  else if (clickedView.equals(mPlus1) || clickedView.equals(mEditName1)) {
			//Clicking on player 1 + button
			setLife(Constants.PLAYER_ONE, getLife(Constants.PLAYER_ONE) + 1);
		}  else if (clickedView.equals(mPlus2) || clickedView.equals(mEditName2)) {
			//Clicking on player 2 + button
            setLife(Constants.PLAYER_TWO, getLife(Constants.PLAYER_TWO) + 1);
		}  else if (clickedView.equals(mMinus1)) {
			//Clicking on player 1 - button
            setLife(Constants.PLAYER_ONE, getLife(Constants.PLAYER_ONE) - 1);
		}  else if (clickedView.equals(mMinus2)) {
			//Clicking on player 2 - button
            setLife(Constants.PLAYER_TWO, getLife(Constants.PLAYER_TWO) - 1);
		}  else if (clickedView.equals(player1_background)) {
			rollBackground(Constants.PLAYER_ONE);
		}  else if (clickedView.equals(player2_background)) {
			rollBackground(Constants.PLAYER_TWO);
		}
	}
	
	private void rollBackground(int player) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	
		if (!preferences.getBoolean(getString(R.string.key_background_lock), true)) {
			Editor preferenceEditor = preferences.edit();
	    	
	    	playerBacks[player] = (playerBacks[player] + 1) % backgrounds_ids.length;
	    	players[player].setBackGroundId(playerBacks[player]);
	    	preferenceEditor.putInt(getString(player==Constants.PLAYER_ONE?R.string.key_back1:R.string.key_back2), playerBacks[player]);
	    	preferenceEditor.commit();
	    	updatePlayerUI(player);
		}
	}

    private int getLife(int player) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt(getString(player == Constants.PLAYER_ONE ? R.string.key_life1 : R.string.key_life2), 20);
    }

    private void setLife(int player, int life) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	Editor preferenceEditor = preferences.edit();

        if (player == Constants.PLAYER_ONE) {
    		mBigLife1.setText(""+life);
    		preferenceEditor.putInt(getString(R.string.key_life1), life);
    	} else if (player == Constants.PLAYER_TWO) {
    		mBigLife2.setText(""+life);
    		preferenceEditor.putInt(getString(R.string.key_life2), life);
    	}

    	preferenceEditor.commit();
    }

    @Override
    public void onPickAction(int action) {
        if (R.id.action_restart == action) {
            restart_game();
        } else if (R.id.action_flip == action) {
            player2_rotate(player2_screen.getRotation() == 180 ? false : true);
        } else if (R.id.action_load == action) {
            LoadPlayerDialog loadDialog = new LoadPlayerDialog(this);
            loadDialog.setListener(this);
            loadDialog.show();
        } else if (R.id.action_edit == action) {
            //Save current player colors in the settings used for setColor options (trick comes from the fact the color picker widget is a preference menu)
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Editor preferenceEditor = preferences.edit();
            preferenceEditor.putInt(getString(R.string.key_color_p1), players[Constants.PLAYER_ONE].getColor());
            preferenceEditor.putInt(getString(R.string.key_color_p2), players[Constants.PLAYER_TWO].getColor());
            preferenceEditor.commit();

            //Launch edit player activity with both players
            Intent editIntent = new Intent(getApplicationContext(), EditPlayerActivity.class);
            editIntent.putExtra(Constants.KEY_PLAYER_ONE, players[Constants.PLAYER_ONE]);
            editIntent.putExtra(Constants.KEY_PLAYER_TWO, players[Constants.PLAYER_TWO]);

            startActivityForResult(editIntent, Constants.REQUEST_EDIT_PLAYERS);
        }
    }
    
    private void switchBackgroundLock() {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	mBackGroundLock  = !mBackGroundLock;
    	Editor preferenceEditor = preferences.edit();
    	
    	preferenceEditor.putBoolean(getString(R.string.key_background_lock), mBackGroundLock);
    	preferenceEditor.commit();

    	Toast.makeText(this, "Background images "+(mBackGroundLock?"":"un")+"locked", Toast.LENGTH_LONG).show();
	}

	private void player2_rotate(boolean doRotate) {
    	Editor preferenceEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    	
    	player2_screen.setRotation(doRotate ? 180 : 0);
    	preferenceEditor.putBoolean(getString(R.string.key_rotate_player2), doRotate);

    	preferenceEditor.commit();
	}

	private void roll_dice(int max_value) {
    	Random random = new Random();
    	//Both random and % seems to like negative values, hence the double trick...
    	int dice_value = (((random.nextInt() % max_value) + max_value) % max_value) + 1;
		Toast dice_rolled = Toast.makeText(this, dice_value + (dice_value == 1 ? " :(" : dice_value == max_value ? "!!" : ""), Toast.LENGTH_SHORT);
		dice_rolled.show();
	}


	private void flip_coin() {
    	Random random = new Random();
		Toast coin_tossed = Toast.makeText(this, random.nextInt() % 2 == 0 ? "HEADS" : "TAILS", Toast.LENGTH_SHORT);
		coin_tossed.show();
    }


	public void restart_game() {
		setLife(Constants.PLAYER_ONE, LIFE_START);
        setLife(Constants.PLAYER_TWO, LIFE_START);
    }


	@Override
	public void onValidateLoad(Player player1, Player player2) {
		players[Constants.PLAYER_ONE] = new Player(player1);
		players[Constants.PLAYER_TWO] = new Player(player2);
		updatePlayerUI(Constants.PLAYER_ONE);
		updatePlayerUI(Constants.PLAYER_TWO);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
           case Constants.REQUEST_EDIT_PLAYERS:
        	   if (resultCode == Activity.RESULT_OK) {
	        	   players[Constants.PLAYER_ONE] = (Player) intent.getParcelableExtra(Constants.KEY_PLAYER_ONE);
	        	   players[Constants.PLAYER_TWO] = (Player) intent.getParcelableExtra(Constants.KEY_PLAYER_TWO);
	        	   updateUI();
        	   }
        	   break;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  
	  if (checkOrientation(newConfig.orientation)) {
		  updateUI();
	  }
	}
}
