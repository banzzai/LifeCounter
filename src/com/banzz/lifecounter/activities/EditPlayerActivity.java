package com.banzz.lifecounter.activities;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.LifeAdapter;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.utils.SystemUiHider;
import com.banzz.lifecounter.utils.Utils.Constants;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class EditPlayerActivity extends Activity implements OnClickListener, LoadPlayerDialog.LoadPlayerDialogListener {
	public static int LIFE_START = 20;

	private int player0_back_number = 0;

	private int mSelectedPlayer = Constants.PLAYER_ONE;
	private Player player_1 = new Player();
	private Player player_2 = new Player();
	private Player[] players = {player_1, player_2};
	
	private int backgrounds_ids[] =
            new int[] {R.drawable.azorius, R.drawable.boros, R.drawable.dimir, R.drawable.golgari, R.drawable.rakdos,
						R.drawable.gruul, R.drawable.izzet, R.drawable.orzhov, R.drawable.selesnya, R.drawable.simic};
    
	private TextView mEditName0;
	
	private TextView mPlus0;
	private TextView mBigLife0;
	private TextView mMinus0;
	
	private WheelView player_zero_wheel;
	
	private ImageView player0_background;

	private EditText mTextBox;
	
	@Override
	protected void onPause() {
		super.onPause();
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);
		
        Button mImagePicker = (Button) findViewById(R.id.pick_image_button);
        mImagePicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(), ActionPickDemo.class));
			}
		});
        
//		Gson gson = new Gson();
//		String json = gson.toJson(knownPlayers);
//		String fileName = "players.JSON";
//		File externalDir = getExternalFilesDir(null);
//		
//		FileOutputStream fos;
//		try {
//			File image = new File(externalDir, fileName);
//			if (!image.exists()) {
//				image.createNewFile();
//			}	
//			
//			fos = new FileOutputStream(image);
//			//fos = openFileOutput(externalDir + fileName, Context.MODE_PRIVATE);
//			fos.write(json.getBytes());
//			fos.close();
//		} catch (Exception e) {
//			Toast.makeText(this, "JSON WRITE FAILED", Toast.LENGTH_LONG).show();
//			e.printStackTrace();
//		}
        
        Intent i = getIntent();
        players[Constants.PLAYER_ONE] = (Player) i.getParcelableExtra("player1");
        players[Constants.PLAYER_TWO] = (Player) i.getParcelableExtra("player2");
        
        mEditName0	= (TextView) findViewById(R.id.edit_player0);
        
        mBigLife0 	= (TextView) findViewById(R.id.big_life_0);
        mPlus0 		= (TextView) findViewById(R.id.plus_0);
        mPlus0.setOnClickListener(this);
        mMinus0		= (TextView) findViewById(R.id.minus_0);
        mMinus0.setOnClickListener(this);
        
        player_zero_wheel = (WheelView) findViewById(R.id.player_zero);
		player_zero_wheel.setViewAdapter(new LifeAdapter(this, Constants.LIFE_MIN, Constants.LIFE_MAX));
		
		player0_background = (ImageView) findViewById(R.id.background_player0);
		player0_background.setOnClickListener(this);
		
		mBigLife0.setText(""+LIFE_START);
		player_zero_wheel.addScrollingListener(new OnWheelScrollListener() {
			private int mStartScrolling0;
			@Override
			public void onScrollingStarted(WheelView wheel) {
				mStartScrolling0 = wheel.getCurrentItem();
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				setLife(player_zero_wheel, Constants.LIFE_MAX - wheel.getCurrentItem(), false);
				String sDelta = mStartScrolling0 - wheel.getCurrentItem() == 0 ? "" : (mStartScrolling0 - wheel.getCurrentItem() > 0 ? "+" : "") + (mStartScrolling0- wheel.getCurrentItem());
				if (!sDelta.isEmpty()) {
					Toast.makeText(EditPlayerActivity.this, sDelta, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//EDIT PART
		RadioGroup mGroup = (RadioGroup) findViewById(R.id.edit_radio);
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				mSelectedPlayer = (radioId == R.id.radio_edit_1) ? Constants.PLAYER_ONE : Constants.PLAYER_TWO;
				updateUI();
			}
		});
		
		mTextBox = (EditText) findViewById(R.id.edit_name);
		mTextBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mTextBox.setImeActionLabel("Go", EditorInfo.IME_ACTION_DONE);
		mTextBox.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView text, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					players[mSelectedPlayer].setName(text.getText().toString());
					updateUI();
		            return false;
		        }
				return false;
			}
		});
		
		Button colorButton = (Button) findViewById(R.id.color_button);
		colorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent editColor = new Intent(getApplicationContext(), SettingsActivity.class);
				editColor.putExtra(Constants.KEY_PLAYER_TARGET, mSelectedPlayer);
				startActivity(editColor);
			}
		});
		
		updateUI();
	}

	//This function should just update what shows on screen, and not change any value. This is not starting a new game!
	private void updateUI() {
		Player player = players[mSelectedPlayer];
		int color = player.getColor() != -1 ? player.getColor() : getResources().getColor(R.color.lifeText);
		boolean showButtons = player.showButons();
		boolean showWheel = player.showWheel();
		int background_id = player.getBackGroundId();

		mBigLife0.setTextColor(color);
		mMinus0.setTextColor(color);
		mPlus0.setTextColor(color);
		mEditName0.setTextColor(color);
		mEditName0.setText(player.getName());
		
		if (showButtons) {
			mPlus0.setVisibility(View.VISIBLE);
			mMinus0.setVisibility(View.VISIBLE);
        } else {
        	mPlus0.setVisibility(View.GONE);
        	mMinus0.setVisibility(View.GONE);
        }
		
		if (showWheel) {
        	player_zero_wheel.setVisibility(View.VISIBLE);
        } else {
        	player_zero_wheel.setVisibility(View.GONE);
        }
		
		player0_background.setImageResource(backgrounds_ids[background_id]);
		player0_background.setScaleType(ScaleType.CENTER_CROP);
	
		//Adjusting editor values
		mTextBox.setText(player.getName());
	}

	@Override
	public void onClick(View clickedView) {
		if (clickedView.equals(mBigLife0)) {
			//Clicking on player 0 life total
		} else if (clickedView.equals(mPlus0)) {
			//Clicking on player 1 + button
			setLife(player_zero_wheel, getLife(player_zero_wheel) + 1, true);
		}  else if (clickedView.equals(mMinus0)) {
			//Clicking on player 1 - button
			setLife(player_zero_wheel, getLife(player_zero_wheel) - 1, true);
		}  else if (clickedView.equals(player0_background)) {
			rollBackground(Constants.PLAYER_ZERO);
		}
	}
	
	private void rollBackground(int player) {
    	player0_back_number = (player0_back_number + 1) % backgrounds_ids.length;
    	players[mSelectedPlayer].setBackGroundId(player0_back_number);
    	updateUI();
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
    	
    	if (adjustWheel) {
    		player.setCurrentItem(Constants.LIFE_MAX - life);
    	}
    		
    	if (player.equals(player_zero_wheel)) {
    		mBigLife0.setText(""+life);
    	}
	}

	@Override
	public void onValidateLoad(Player player, int player_slot) {
		players[player_slot] = new Player(player);
		updateUI();
	}
}
