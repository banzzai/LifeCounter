package com.banzz.lifecounter.activities;

import java.util.Random;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.SystemUiHider;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.banzz.lifecounter.commons.LifeAdapter;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class TwoPlayerActivity extends Activity implements OnClickListener, LoadPlayerDialog.LoadPlayerDialogListener {
	public static int LIFE_START = 20;
	public static int PLAYER_NUMBER = 2;
	
	private int player1_back_number = 0;
	private int player2_back_number = 1;
	private int[] playerBacks = {-1, -1};
	
	private Player player_1;
	private Player player_2;
	private Player[] players = {null, null};
	
	private int backgrounds_ids[] =
            new int[] {R.drawable.azorius, R.drawable.boros, R.drawable.dimir, R.drawable.golgari, R.drawable.rakdos,
						R.drawable.gruul, R.drawable.izzet, R.drawable.orzhov, R.drawable.selesnya, R.drawable.simic};
    
	private Player[] knownPlayers = new Player[6];
	
	private EditText mEditName1;
	private EditText mEditName2;
	private EditText[] editNames = {null, null};
	
	private TextView mBigLife1;
	private TextView mBigLife2;
	private TextView[] bigLifes = {null, null};

	private TextView mPlus1;
	private TextView mPlus2;
	private TextView[] pluses = {null, null};

	private TextView mMinus1;
	private TextView mMinus2;
	private TextView[] minuses = {null, null};
	
	private WheelView player_one_wheel;
	private WheelView player_two_wheel;
	private WheelView[] wheels = {null, null};
	
	private ImageView player1_background;
	private ImageView player2_background;
	private ImageView[] backgrounds = {null, null};
	
	private LinearLayout player2_screen;
	
	private WakeLock wl;

	@Override
	protected void onPause() {
		super.onPause();
		if (wl != null && wl.isHeld()) {
			wl.release();	
		}
	};
	
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
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
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);
        
        //TODO add save player profile
        knownPlayers[0] = new Player("id1", "Alex", getResources().getColor(R.color.lifeText), true, false, 7);
        knownPlayers[1] = new Player("id2", "Charles-Basile", getResources().getColor(R.color.lifeText), true, false, 4);
        knownPlayers[2] = new Player("id3", "David", getResources().getColor(R.color.lifeText), true, false, 0);
   		knownPlayers[3] = new Player("id4", "Greg", getResources().getColor(R.color.lifeText), true, false, 6);
		knownPlayers[4] = new Player("id5", "Olivier", getResources().getColor(R.color.lifeText), true, false, 1);
		knownPlayers[5] = new Player("id6", "Tanisha", getResources().getColor(R.color.lifeText), true, false, 8);
        
        mEditName1	= (EditText) findViewById(R.id.edit_player1);
        /*mEditName1.setImeActionLabel("OK", KeyEvent.KEYCODE_ENTER);
        mEditName1.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int actionId, KeyEvent event) {
				if (actionId == android.view.inputmethod.EditorInfo.IME_NULL  
					      && event.getAction() == KeyEvent.ACTION_DOWN) { 
					Toast.makeText(TwoPlayerActivity.this, mEditName1.getText(), Toast.LENGTH_SHORT).show();
					   }
					   return true;
			}
		});*/
        
        mBigLife1 	= (TextView) findViewById(R.id.big_life_1);
        mBigLife1.setOnClickListener(this);
        mPlus1 		= (TextView) findViewById(R.id.plus_1);
        mPlus1.setOnClickListener(this);
        mMinus1		= (TextView) findViewById(R.id.minus_1);
        mMinus1.setOnClickListener(this);
        
        mEditName2	= (EditText) findViewById(R.id.edit_player2);
        mBigLife2 	= (TextView) findViewById(R.id.big_life_2);
        mBigLife2.setOnClickListener(this);
        mPlus2 		= (TextView) findViewById(R.id.plus_2);
        mPlus2.setOnClickListener(this);
        mMinus2		= (TextView) findViewById(R.id.minus_2);
        mMinus2.setOnClickListener(this);
        
        player_one_wheel = (WheelView) findViewById(R.id.player_one);
		player_one_wheel.setViewAdapter(new LifeAdapter(this, Constants.LIFE_MIN, Constants.LIFE_MAX));
		
		player1_background = (ImageView) findViewById(R.id.background_player1);
		player1_background.setOnClickListener(this);
		player2_background = (ImageView) findViewById(R.id.background_player2);
		player2_background.setOnClickListener(this);
		
		player2_screen = (LinearLayout) findViewById(R.id.player_two_screen);
		
		mBigLife1.setText(""+LIFE_START);
		player_one_wheel.addScrollingListener(new OnWheelScrollListener() {
			private int mStartScrolling1;
			@Override
			public void onScrollingStarted(WheelView wheel) {
				mStartScrolling1 = wheel.getCurrentItem();
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				setLife(player_one_wheel, Constants.LIFE_MAX - wheel.getCurrentItem(), false);
				String sDelta = mStartScrolling1 - wheel.getCurrentItem() == 0 ? "" : (mStartScrolling1 - wheel.getCurrentItem() > 0 ? "+" : "") + (mStartScrolling1 - wheel.getCurrentItem());
				if (!sDelta.isEmpty()) {
					Toast.makeText(TwoPlayerActivity.this, sDelta, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		player_two_wheel = (WheelView) findViewById(R.id.player_two);
		player_two_wheel.setViewAdapter(new LifeAdapter(this, Constants.LIFE_MIN, Constants.LIFE_MAX));
		
		mBigLife2.setText(""+LIFE_START);
		player_two_wheel.addScrollingListener(new OnWheelScrollListener() {
			private int mStartScrolling2;
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				setLife(player_two_wheel, Constants.LIFE_MAX - wheel.getCurrentItem(), false);
				String sDelta = mStartScrolling2 - wheel.getCurrentItem() == 0 ? "" : (mStartScrolling2 - wheel.getCurrentItem() > 0 ? "+" : "") + (mStartScrolling2 - wheel.getCurrentItem());
				if (!sDelta.isEmpty()) {
					Toast.makeText(TwoPlayerActivity.this, sDelta, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		player1_back_number = preferences.getInt(getString(R.string.key_back1), 0);
		player2_back_number = preferences.getInt(getString(R.string.key_back2), 1);
		setLife(player_one_wheel, preferences.getInt(getString(R.string.key_life1), 20), true);
		setLife(player_two_wheel, preferences.getInt(getString(R.string.key_life2), 20), true);
		player2_rotate(preferences.getBoolean(getString(R.string.key_rotate_player2), false));
		
		initArrays();
		
		updateUI();
	}


	private void initArrays() {
		playerBacks[0] = player1_back_number;
		playerBacks[1] = player2_back_number;

		/*Player dude = new Player("id", "SpongeBob", R.color.black_overlay, false, true, 6);
		Player dude2 = new Player("id", "Patrick", R.color.lifeText, true, false, 3);
		List<Player> list = new ArrayList<Player>();
		list.add(dude);
		list.add(dude2);
		
		Gson gson = new Gson();
		String json = gson.toJson(list);
		players = gson.fromJson(json, Player[].class);*/ 

		players[0] = player_1;
		players[1] = player_2;

		editNames[0] = mEditName1;
		editNames[1] = mEditName2;

		bigLifes[0] = mBigLife1;
		bigLifes[1] = mBigLife2;
		
		pluses[0] = mPlus1;
		pluses[1] = mPlus2;

		minuses[0] = mMinus1;
		minuses[1] = mMinus2;
		
		wheels[0] = player_one_wheel;
		wheels[1] = player_two_wheel;
		
		backgrounds[0] = player1_background;
		backgrounds[1] = player2_background;
	}

	//This function should just update what shows on screen, and not change any value. This is not starting a new game!
	private void updateUI() {
		for (int i=0; i<PLAYER_NUMBER; i++) {
			updatePlayerUI(i);
		}
	}
	
	private void updatePlayerUI(int player_number) {
		if (player_number > PLAYER_NUMBER) {
			return;
		}
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int defaultColor = preferences.getInt(getString(R.string.key_color), R.color.lifeText);
		boolean defaultShowButtons  = preferences.getBoolean(getString(R.string.key_show_buttons), false);
		boolean defaultShowWheel  = preferences.getBoolean(getString(R.string.key_show_wheels), true);

		boolean loadPlayer = players[player_number] != null;
		Player player = loadPlayer ? players[player_number] : null;
		int color = loadPlayer && player.getColor() != -1 ? player.getColor() : defaultColor;
		boolean showButtons = loadPlayer ? player.showButons() : defaultShowButtons;
		boolean showWheel = loadPlayer ? player.showWheel(): defaultShowWheel;
		int background_id = loadPlayer ? player.getBackGroundId() : playerBacks[player_number];

		bigLifes[player_number].setTextColor(color);
		minuses[player_number].setTextColor(color);
		pluses[player_number].setTextColor(color);
		editNames[player_number].setTextColor(color);
		editNames[player_number].setText(loadPlayer ? player.getName() : "Player");
		
		if (showButtons) {
			pluses[player_number].setVisibility(View.VISIBLE);
			minuses[player_number].setVisibility(View.VISIBLE);
        } else {
        	pluses[player_number].setVisibility(View.GONE);
			minuses[player_number].setVisibility(View.GONE);
        }
		
		if (showWheel) {
        	wheels[player_number].setVisibility(View.VISIBLE);
        } else {
        	wheels[player_number].setVisibility(View.GONE);
        }
		
		backgrounds[player_number].setImageResource(backgrounds_ids[background_id]);
		backgrounds[player_number].setScaleType(ScaleType.CENTER_CROP);
	}

	@Override
	public void onClick(View clickedView) {
		if (clickedView.equals(mBigLife1)) {
			//Clicking on player 1 life total
		} else if (clickedView.equals(mBigLife2)) {
			//Clicking on player 2 life total
		}  else if (clickedView.equals(mPlus1)) {
			//Clicking on player 1 + button
			setLife(player_one_wheel, getLife(player_one_wheel) + 1, true);
		}  else if (clickedView.equals(mPlus2)) {
			//Clicking on player 2 + button
			setLife(player_two_wheel, getLife(player_two_wheel) + 1, true);
		}  else if (clickedView.equals(mMinus1)) {
			//Clicking on player 1 - button
			setLife(player_one_wheel, getLife(player_one_wheel) - 1, true);
		}  else if (clickedView.equals(mMinus2)) {
			//Clicking on player 2 - button
			setLife(player_two_wheel, getLife(player_two_wheel) - 1, true);
		}  else if (clickedView.equals(player1_background)) {
			rollBackground(Constants.PLAYER_ONE);
		}  else if (clickedView.equals(player2_background)) {
			rollBackground(Constants.PLAYER_TWO);
		}
	}
	
	private void rollBackground(int player) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	Editor preferenceEditor = preferences.edit();
    	
    	switch(player) {
    		case Constants.PLAYER_ONE:
    			player1_back_number = (player1_back_number + 1) % backgrounds_ids.length;
    			preferenceEditor.putInt(getString(R.string.key_back1), player1_back_number);
    			break;
    		case Constants.PLAYER_TWO:
    			player2_back_number = (player2_back_number + 1) % backgrounds_ids.length;
    			preferenceEditor.putInt(getString(R.string.key_back2), player2_back_number);
    			break;
    	}
    	updateUI();
    	preferenceEditor.commit();
	}

	//SINCE WE ARE HACKING A WHEEL THAT WORKS BACKWARDS OF INDEX ALWAYS USE THIS GETTER
	private int getLife(WheelView player) {
		return Constants.LIFE_MAX - player.getCurrentItem();
	}
	
	//SINCE WE ARE HACKING A WHEEL THAT WORKS BACKWARDS OF INDEX ALWAYS USE THIS SETTER
	private void setLife(WheelView player, int life, boolean adjustWheel) {
    	if (life < 0 || life > Constants.LIFE_MAX) {
    		return;
    	}
    	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	Editor preferenceEditor = preferences.edit();
    	
    	if (adjustWheel) {
    		player.setCurrentItem(Constants.LIFE_MAX - life);
    	}
    		
    	if (player.equals(player_one_wheel)) {
    		mBigLife1.setText(""+life);
    		preferenceEditor.putInt(getString(R.string.key_life1), life);
    	} else if (player.equals(player_two_wheel)) {
    		mBigLife2.setText(""+life);
    		preferenceEditor.putInt(getString(R.string.key_life2), life);
    	}
    	
    	preferenceEditor.commit();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	if (R.id.action_restart == item.getItemId()) {
    		restart_game();
    	} else if (R.id.action_coin == item.getItemId()) {
    		flip_coin();
    	} else if (R.id.action_dice == item.getItemId()) {
    		roll_dice(20);
    	} else if (R.id.action_settings == item.getItemId()) {
    		startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    	} else if (R.id.action_flip == item.getItemId()) {
    		player2_rotate(player2_screen.getRotation() == 180 ? false : true);
    	} else if (R.id.action_load == item.getItemId()) {
    		LoadPlayerDialog loadDialog = new LoadPlayerDialog();
    		loadDialog.setUsers(knownPlayers);
    		loadDialog.setListener(this);
    	    loadDialog.show(getFragmentManager(), getString(R.string.load_player));
    	}/* else if (R.id.action_crop) {
    		cropImage();
    	}*/
    	
    	return super.onMenuItemSelected(featureId, item);
    }
    
    private void cropImage() {
    	/*final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setData(uriOfImageToCrop);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("output", Uri.fromFile(someOutputFile));
        startActivityForResult(intent, SOME_RANDOM_REQUEST_CODE);*/
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
		setLife(player_one_wheel, LIFE_START, true);
		setLife(player_two_wheel, LIFE_START, true);
    }


	@Override
	public void onValidateLoad(Player player, int player_slot) {
		players[player_slot] = player;
		updatePlayerUI(player_slot);
	}
}
