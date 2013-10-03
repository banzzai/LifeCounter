package com.banzz.lifecounter.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

//import kankan.wheel.widget.OnWheelScrollListener;
//import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.commons.Player;
import com.banzz.lifecounter.utils.SystemUiHider;
import com.banzz.lifecounter.utils.Utils.Constants;
import com.google.gson.Gson;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class EditPlayerActivity extends Activity implements OnClickListener,
        LoadPlayerDialog.LoadPlayerDialogListener, PickColorDialog.PickColorDialogListener {
	public static int LIFE_START = 20;
	
	private int player0_back_number = 0;

	private int mSelectedPlayer = Constants.PLAYER_ONE;
	private Player player_1 = new Player();
	private Player player_2 = new Player();
	private Player[] players = {player_1, player_2};
	private int mOrientation = -1;
	
	private int backgrounds_ids[] =
            new int[] {R.drawable.azorius, R.drawable.boros, R.drawable.dimir, R.drawable.golgari, R.drawable.rakdos,
						R.drawable.gruul, R.drawable.izzet, R.drawable.orzhov, R.drawable.selesnya, R.drawable.simic};
    
	private TextView mEditName0;
	
	private TextView mPlus0;
	private TextView mBigLife0;
	private TextView mMinus0;
	
	//private WheelView player_zero_wheel;
	
	private ImageView player0_background;

	private EditText mTextBox;
//	private CheckBox check_wheels;
//	private CheckBox check_buttons;

	private Player[] mUsers;
    private RelativeLayout wizardLayout;

    @Override
	protected void onPause() {
		super.onPause();
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		checkOrientation(getResources().getConfiguration().orientation);
		updateUI();
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_edit_player);
		
        Button mImagePicker = (Button) findViewById(R.id.images_button);
        mImagePicker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent pickIntent = new Intent(getApplicationContext(), PickImageActivity.class);
				pickIntent.putExtra(Constants.KEY_PLAYER_TARGET, players[mSelectedPlayer]);
				
				startActivityForResult(pickIntent, Constants.REQUEST_PICK_IMAGES);
			}
		});

//		Gson gson = new Gson();
//		String json = gson.toJson(knownPlayers);
//		String fileName = Constants.PROFILES_FILE_NAME;
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
        players[Constants.PLAYER_ONE] = (Player) i.getParcelableExtra(Constants.KEY_PLAYER_ONE);
        players[Constants.PLAYER_TWO] = (Player) i.getParcelableExtra(Constants.KEY_PLAYER_TWO);
        
        mEditName0	= (TextView) findViewById(R.id.edit_player0);
        
        mBigLife0 	= (TextView) findViewById(R.id.big_life_0);
        mPlus0 		= (TextView) findViewById(R.id.plus_0);
        mPlus0.setOnClickListener(this);
        mMinus0		= (TextView) findViewById(R.id.minus_0);
        mMinus0.setOnClickListener(this);
        
//        player_zero_wheel = (WheelView) findViewById(R.id.player_zero);
//		player_zero_wheel.setViewAdapter(new LifeAdapter(this, Constants.LIFE_MIN, Constants.LIFE_MAX));
		
		player0_background = (ImageView) findViewById(R.id.background_player0);
		player0_background.setOnClickListener(this);
		
		mBigLife0.setText(""+LIFE_START);
//		player_zero_wheel.addScrollingListener(new OnWheelScrollListener() {
//			private int mStartScrolling0;
//			@Override
//			public void onScrollingStarted(WheelView wheel) {
//				mStartScrolling0 = wheel.getCurrentItem();
//			}
//			@Override
//			public void onScrollingFinished(WheelView wheel) {
//				setLife(player_zero_wheel, Constants.LIFE_MAX - wheel.getCurrentItem(), false);
//				String sDelta = mStartScrolling0 - wheel.getCurrentItem() == 0 ? "" : (mStartScrolling0 - wheel.getCurrentItem() > 0 ? "+" : "") + (mStartScrolling0- wheel.getCurrentItem());
//				if (!sDelta.isEmpty()) {
//					Toast.makeText(EditPlayerActivity.this, sDelta, Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
		
		//EDIT PART
		RadioGroup mGroup = (RadioGroup) findViewById(R.id.edit_radio);
		mGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				mSelectedPlayer = (radioId == R.id.radio_edit_1) ? Constants.PLAYER_ONE : Constants.PLAYER_TWO;
				updateUI();
			}
		});
		
		mTextBox = (EditText) findViewById(R.id.edit_name);
		mTextBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mTextBox.setImeActionLabel(getString(R.string.keyboard_go), EditorInfo.IME_ACTION_DONE);
		mTextBox.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView text, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Change name in UI -> save name in object
                    players[mSelectedPlayer].setName(text.getText().toString());
                    updateUI();
                    return false;
                }
                return false;
            }
        });
        mTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(players[mSelectedPlayer].getName()))
                {
                    players[mSelectedPlayer].setName(editable.toString());
                    updateUI(editable.toString());
                }
            }
        });

//		check_buttons = (CheckBox) findViewById(R.id.check_buttons);
//		check_buttons.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				players[mSelectedPlayer].setShowButtons(isChecked);
//				updateUI();
//			}
//		});
//
//		check_wheels = (CheckBox) findViewById(R.id.check_wheels);
//		check_wheels.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				players[mSelectedPlayer].setShowWheel(isChecked);
//				updateUI();
//			}
//		});
		
		Button colorButton = (Button) findViewById(R.id.color_button);
		colorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                PickColorDialog colorDialog = new PickColorDialog(EditPlayerActivity.this, mSelectedPlayer);
                colorDialog.setListener(EditPlayerActivity.this);
                colorDialog.show(getFragmentManager(), getString(R.string.pick_color));
			}
		});
		
		Button loadButton = (Button) findViewById(R.id.load_button);
		loadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LoadPlayerDialog loadDialog = new LoadPlayerDialog();
				loadDialog.setListener(EditPlayerActivity.this);
			    loadDialog.show(getFragmentManager(), getString(R.string.load_player));
			}
		});

		Button saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				validateSave(mSelectedPlayer);
			}
		});
		
		Button deleteButton = (Button) findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getApplicationContext(), DeletePlayerActivity.class), Constants.REQUEST_DELETE_PLAYERS);
			}
		});
		
		Button applyButton = (Button) findViewById(R.id.apply_button);
		applyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog dialog = new AlertDialog.Builder(EditPlayerActivity.this).create();
			    dialog.setTitle(getString(R.string.apply));
			    String message = getString(R.string.apply_confirmation);
			    	   
			    dialog.setMessage(message);
			    dialog.setCancelable(true);
			    dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int buttonId) {
			        	Intent applyIntent = new Intent();
			        	applyIntent.putExtra(Constants.KEY_PLAYER_ONE, players[Constants.PLAYER_ONE]);
			        	applyIntent.putExtra(Constants.KEY_PLAYER_TWO, players[Constants.PLAYER_TWO]);
			        	setResult(Activity.RESULT_OK, applyIntent);
						finish();
			        }
			    });
			    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, EditPlayerActivity.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int buttonId) {
			    		dialog.dismiss();        
			    	}
			    });

			    dialog.show();
			}
		});
		
		loadSavedProfiles();
		updateUI();

        wizardLayout = (RelativeLayout) findViewById(R.id.edit_wizard_layout);

        Button close_wizard = (Button) findViewById(R.id.edit_close_wizard);
        close_wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                wizardLayout.setVisibility(View.GONE);
            }
        });

        Button never_show_wizard = (Button) findViewById(R.id.edit_never_show);
        never_show_wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditPlayerActivity.this);
                SharedPreferences.Editor preferenceEditor = preferences.edit();
                preferenceEditor.putBoolean(getString(R.string.key_hide_edit_wizard), true);
                preferenceEditor.commit();
                wizardLayout.setVisibility(View.GONE);
            }
        });

        checkWizard();
	}

    private void checkWizard()
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(getString(R.string.key_hide_edit_wizard), false)) {
            wizardLayout.setVisibility(View.VISIBLE);
        }
    }

	private void validateSave(int selectedPlayer) {
		AlertDialog dialog;

		if (players[selectedPlayer].getName() == null || players[selectedPlayer].getName().isEmpty()) {
			dialog = new AlertDialog.Builder(EditPlayerActivity.this).create();
		    dialog.setTitle(EditPlayerActivity.this.getString(R.string.Save));
		    String message = EditPlayerActivity.this.getString(R.string.save_error_empty_name);
		    dialog.setMessage(message);
		    dialog.setCancelable(true);
		    dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int buttonId) {
		        }
		    });
		} else {
			dialog = new AlertDialog.Builder(EditPlayerActivity.this).create();
		    dialog.setTitle(EditPlayerActivity.this.getString(R.string.Save));
		    String message = EditPlayerActivity.this.getString(R.string.save_confirmation);
		    
		    int replaceIndex = -1;
		    for(int i=0; mUsers != null && i<mUsers.length; i++) {
		    	if (players[selectedPlayer].getName().equals(mUsers[i].getName())) {
		    		replaceIndex = i;
		    		message += EditPlayerActivity.this.getString(R.string.save_overwrite) + players[selectedPlayer].getName();
		    		break;
		    	}
		    }
		   
		    final int fReplaceIndex = replaceIndex;
		    dialog.setMessage(message);
		    dialog.setCancelable(true);
		    dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int buttonId) {
		            EditPlayerActivity.this.onValidateSave(fReplaceIndex);
		        }
		    });
		    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, EditPlayerActivity.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int buttonId) {
		        }
		    });
		}
	    dialog.show();
	}
	
	private void loadSavedProfiles() {
	    String fileName = Constants.PROFILES_FILE_NAME;
	    //Bad bad casts here. Then again, this is not meant to be adaptable code, used in x different activities;
	    //worse case scenario it crashes and it'll serve as a reminder to set a listener...
	    File externalDir = getExternalFilesDir(null);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(externalDir + fileName);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		if (fis == null) {
			//Empty list
		} else {
			Reader reader = new InputStreamReader(fis);
			mUsers = gson.fromJson(reader, Player[].class);
		}
	}

	//TODO Remove this abomination and use ArrayList or something (it was to make it easier on json and probably a bad idea; now there's [] all over the place)
	private Player[] addPlayer(Player player, Player[] players) {
		int length = players == null ? 0 : players.length;
		Player[] newPlayerList = new Player[length+1];
		
		for (int i=0; i < length; i++) {
			newPlayerList[i] = players[i];
		}
		
		newPlayerList[length] = new Player(player);
		return newPlayerList;
	}
	
	public void onValidateSave(int mReplaceIndex) {
		if (mReplaceIndex == -1) {
			Toast.makeText(this, "Saving profile " + players[mSelectedPlayer].getName(), Toast.LENGTH_LONG).show();
			mUsers = addPlayer(players[mSelectedPlayer], mUsers);
		} else {
			Toast.makeText(this, "Replacing profile " + players[mSelectedPlayer].getName(), Toast.LENGTH_LONG).show();
			mUsers[mReplaceIndex] = new Player(players[mSelectedPlayer]);
		}
		
		savePlayers();
	}
	
	private void savePlayers() {
		Gson gson = new Gson();
		String json = gson.toJson(mUsers);
		String fileName = Constants.PROFILES_FILE_NAME;
		File externalDir = getExternalFilesDir(null);
		
		FileOutputStream fos;
		try {
			File image = new File(externalDir, fileName);
			if (!image.exists()) {
				image.createNewFile();
			}	
			
			fos = new FileOutputStream(image);
			//fos = openFileOutput(externalDir + fileName, Context.MODE_PRIVATE);
			fos.write(json.getBytes());
			fos.close();
		} catch (Exception e) {
			Toast.makeText(this, "JSON WRITE FAILED", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

    private void updateUI()
    {
        updateUI(null);
    }

	//This function should just update what shows on screen, and not change any value. This is not starting a new game!
	private void updateUI(String newPlayerName) {
		Player player = players[mSelectedPlayer];
		//int color = player.getColor() != -1 ? player.getColor() : getResources().getColor(R.color.lifeText);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultColor = getResources().getColor(R.color.lifeText);
        int color = preferences.getInt(getString(mSelectedPlayer == Constants.PLAYER_ONE ? R.string.key_color_p1 : R.string.key_color_p2), defaultColor);
        //There is no callback when coming from settings, so we're going to set the player object color property from settings whenever we refresh display.
		//At least the reducancy is used for both UI and save data now. Other values are saved in the object when they are actually being set.
		player.setColor(color);
		
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
		
//		if (showWheel) {
//        	player_zero_wheel.setVisibility(View.VISIBLE);
//        } else {
//        	player_zero_wheel.setVisibility(View.GONE);
//        }
		
		String tallUrl = player.getTallBgUrl();
		String largeUrl = player.getLargeBgUrl();
		if (tallUrl != null || largeUrl != null) {
			player0_background.setImageURI(Uri.parse(mOrientation == Configuration.ORIENTATION_PORTRAIT ? 
					tallUrl != null ? tallUrl : largeUrl
					: largeUrl != null ? largeUrl : tallUrl));
			player0_background.setScaleType(ScaleType.FIT_XY);
		} else {
			player0_background.setImageResource(backgrounds_ids[background_id]);
			player0_background.setScaleType(ScaleType.CENTER_CROP);
		}
		
		//Adjusting editor values
		if (newPlayerName == null)
        {
            mTextBox.setText(player.getName());
        }

//		check_buttons.setChecked(player.showButons());
//		check_wheels.setChecked(player.showWheel());
	}

	private boolean checkOrientation(int orientation) {
		if (orientation != mOrientation) {
			mOrientation = orientation;
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View clickedView) {
		if (clickedView.equals(mBigLife0)) {
			//Clicking on player 0 life total
		} else if (clickedView.equals(mPlus0)) {
			//Clicking on player 1 + button
			//setLife(player_zero_wheel, getLife(player_zero_wheel) + 1, true);
		}  else if (clickedView.equals(mMinus0)) {
			//Clicking on player 1 - button
			//setLife(player_zero_wheel, getLife(player_zero_wheel) - 1, true);
		}  else if (clickedView.equals(player0_background)) {
			rollBackground(Constants.PLAYER_ZERO);
		}
	}
	
	private void rollBackground(int player) {
    	player0_back_number = (player0_back_number + 1) % backgrounds_ids.length;
    	//Change background in UI -> save background in object
    	players[mSelectedPlayer].setBackGroundId(player0_back_number);
    	updateUI();
	}

	//SINCE WE ARE HACKING A WHEEL THAT WORKS BACKWARDS OF INDEX ALWAYS USE THIS GETTER
//	private int getLife(WheelView player) {
//		return Constants.LIFE_MAX - player.getCurrentItem();
//	}
	
	//SINCE WE ARE HACKING A WHEEL THAT WORKS BACKWARDS OF INDEX ALWAYS USE THIS SETTER
//	private void setLife(WheelView player, int life, boolean adjustWheel) {
//    	if (life < 0 || life > Constants.LIFE_MAX) {
//    		return;
//    	}
//
////    	if (adjustWheel) {
////    		player.setCurrentItem(Constants.LIFE_MAX - life);
////    	}
//
////    	if (player.equals(player_zero_wheel)) {
////    		mBigLife0.setText(""+life);
////    	}
//	}

	@Override
	public void onValidateLoad(Player player, int player_slot) {
		mSelectedPlayer = player_slot;
		players[player_slot] = new Player(player);
		updateUI();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_CANCELED)
        {
            return;
        }
        switch (requestCode) {
           case Constants.REQUEST_PICK_IMAGES:
        	   players[mSelectedPlayer] = (Player) intent.getParcelableExtra(Constants.KEY_PLAYER_TARGET);
        	   break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
	    dialog.setTitle(getString(R.string.Save));
	    String message = getString(R.string.save_goback_confirmation);
	    	   
	    dialog.setMessage(message);
	    dialog.setCancelable(true);
	    dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(R.string.go_back), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int buttonId) {
	        	setResult(Activity.RESULT_CANCELED, new Intent());
				finish();
	        }
	    });
	    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, EditPlayerActivity.this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int buttonId) {
	    		dialog.dismiss();        
	    	}
	    });

	    dialog.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  
	  if (checkOrientation(newConfig.orientation)) {
		  updateUI();
	  }
	}

    @Override
    public void onValidateColor(int color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();

        String key = getString(mSelectedPlayer == Constants.PLAYER_TWO ? R.string.key_color_p2 : R.string.key_color_p1);
        edit.putInt(key, getResources().getColor(color));
        edit.commit();
        updateUI();
    }
}
