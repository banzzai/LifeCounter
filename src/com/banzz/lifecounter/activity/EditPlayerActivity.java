package com.banzz.lifecounter.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.Player;
import com.banzz.lifecounter.common.Utils;
import com.banzz.lifecounter.common.Utils.Constants;
import com.banzz.lifecounter.dialog.PickColorDialog;
import com.google.gson.Gson;

public class EditPlayerActivity extends FullScreenActivity implements OnClickListener, PickColorDialog.PickColorDialogListener
{
    private static final String TAG = EditPlayerActivity.class.getName();

    public static int LIFE_START = 20;
    private int player0_back_number = 0;

    private int mSelectedPlayer = Constants.PLAYER_ONE;
    private Player player_1 = new Player();
    private Player player_2 = new Player();
    private Player[] players = { player_1, player_2 };
    private int mOrientation = -1;

    private int backgrounds_ids[] = new int[] { R.drawable.azorius, R.drawable.boros, R.drawable.dimir, R.drawable.golgari,
	    R.drawable.rakdos, R.drawable.gruul, R.drawable.izzet, R.drawable.orzhov, R.drawable.selesnya, R.drawable.simic };

    private TextView mEditName0;
    private TextView mPlus0;
    private TextView mBigLife0;
    private TextView mMinus0;

    private ImageView player0_background;

    private EditText mTextBox;
    private Player[] mUsers;
    private RelativeLayout wizardLayout;

    private boolean mShowPlaque = true;

    @Override
    protected void onResume()
    {
	super.onResume();
	checkOrientation(getResources().getConfiguration().orientation);
	updateUI(true);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_edit_player);

	Button mImagePicker = (Button) findViewById(R.id.images_button);
	mImagePicker.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View arg0)
	    {
		Intent pickIntent = new Intent(getApplicationContext(), PickImageActivity.class);
		pickIntent.putExtra(Constants.KEY_PLAYER_TARGET, players[mSelectedPlayer]);

		startActivityForResult(pickIntent, Constants.REQUEST_PICK_IMAGES);
	    }
	});

	Intent i = getIntent();
	players[Constants.PLAYER_ONE] = (Player) i.getParcelableExtra(Constants.KEY_PLAYER_ONE);
	players[Constants.PLAYER_TWO] = (Player) i.getParcelableExtra(Constants.KEY_PLAYER_TWO);

	mEditName0 = (TextView) findViewById(R.id.edit_player0);

	mBigLife0 = (TextView) findViewById(R.id.big_life_0);
	mPlus0 = (TextView) findViewById(R.id.plus_0);
	mPlus0.setOnClickListener(this);
	mMinus0 = (TextView) findViewById(R.id.minus_0);
	mMinus0.setOnClickListener(this);

	player0_background = (ImageView) findViewById(R.id.background_player0);
	player0_background.setOnClickListener(this);

	mBigLife0.setText("" + LIFE_START);

	// EDIT PART
	RadioGroup mGroup = (RadioGroup) findViewById(R.id.edit_radio);
	mGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener()
	{
	    @Override
	    public void onCheckedChanged(RadioGroup arg0, int radioId)
	    {
		mSelectedPlayer = (radioId == R.id.radio_edit_1) ? Constants.PLAYER_ONE : Constants.PLAYER_TWO;
		updateUI();
	    }
	});

	mTextBox = (EditText) findViewById(R.id.edit_name);
	mTextBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
	mTextBox.setImeActionLabel(getString(R.string.keyboard_go), EditorInfo.IME_ACTION_DONE);
	mTextBox.setOnEditorActionListener(new OnEditorActionListener()
	{

	    @Override
	    public boolean onEditorAction(TextView text, int actionId, KeyEvent event)
	    {
		if (actionId == EditorInfo.IME_ACTION_DONE)
		{
		    // Change name in UI -> save name in object
		    players[mSelectedPlayer].setName(text.getText().toString());
		    updateUI();
		    return false;
		}
		return false;
	    }
	});
	mTextBox.addTextChangedListener(new TextWatcher()
	{
	    @Override
	    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
	    {
	    }

	    @Override
	    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
	    {
	    }

	    @Override
	    public void afterTextChanged(Editable editable)
	    {
		if (!editable.toString().equals(players[mSelectedPlayer].getName()))
		{
		    players[mSelectedPlayer].setName(editable.toString());
		    updateUI(editable.toString());
		}
	    }
	});

	Button colorButton = (Button) findViewById(R.id.color_button);
	colorButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		PickColorDialog colorDialog = new PickColorDialog(EditPlayerActivity.this, mSelectedPlayer);
		colorDialog.setListener(EditPlayerActivity.this);
		colorDialog.show(getFragmentManager(), getString(R.string.pick_color));
	    }
	});

	Button loadButton = (Button) findViewById(R.id.load_button);
	loadButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		// LoadPlayerDialog loadDialog = new LoadPlayerDialog();
		// loadDialog.setListener(EditPlayerActivity.this);
		// loadDialog.show(getFragmentManager(),
		// getString(R.string.load_player));
	    }
	});

	Button saveButton = (Button) findViewById(R.id.save_button);
	saveButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		validateSave(mSelectedPlayer);
	    }
	});

	Button deleteButton = (Button) findViewById(R.id.delete_button);
	deleteButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		startActivityForResult(new Intent(getApplicationContext(), DeletePlayerActivity.class),
			Constants.REQUEST_DELETE_PLAYERS);
	    }
	});

	Button applyButton = (Button) findViewById(R.id.apply_button);
	applyButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		AlertDialog dialog = new AlertDialog.Builder(EditPlayerActivity.this).create();
		dialog.setTitle(getString(R.string.apply));
		String message = getString(R.string.apply_confirmation);

		dialog.setMessage(message);
		dialog.setCancelable(true);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(android.R.string.ok),
			new DialogInterface.OnClickListener()
		{
		    public void onClick(DialogInterface dialog, int buttonId)
		    {
			Intent applyIntent = new Intent();
			applyIntent.putExtra(Constants.KEY_PLAYER_ONE, players[Constants.PLAYER_ONE]);
			applyIntent.putExtra(Constants.KEY_PLAYER_TWO, players[Constants.PLAYER_TWO]);
			setResult(Activity.RESULT_OK, applyIntent);
			finish();
		    }
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, EditPlayerActivity.this.getString(R.string.cancel),
			new DialogInterface.OnClickListener()
		{
		    public void onClick(DialogInterface dialog, int buttonId)
		    {
			dialog.dismiss();
		    }
		});

		dialog.show();
	    }
	});

	loadSavedProfiles();
	updateUI(true);

	wizardLayout = (RelativeLayout) findViewById(R.id.edit_wizard_layout);

	Button close_wizard = (Button) findViewById(R.id.edit_close_wizard);
	close_wizard.setOnClickListener(new View.OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		wizardLayout.setVisibility(View.GONE);
	    }
	});

	Button never_show_wizard = (Button) findViewById(R.id.edit_never_show);
	never_show_wizard.setOnClickListener(new View.OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		Utils.setBooleanPreference(getString(R.string.key_hide_edit_wizard), true);
		wizardLayout.setVisibility(View.GONE);
	    }
	});

	checkWizard();
    }

    private void checkWizard()
    {
	if (!Utils.getBooleanPreference(getString(R.string.key_hide_edit_wizard), false))
	{
	    wizardLayout.setVisibility(View.VISIBLE);
	}
    }

    private void validateSave(final int selectedPlayer)
    {
	AlertDialog dialog;

	if (players[selectedPlayer].getName() == null || players[selectedPlayer].getName().isEmpty())
	{
	    dialog = new AlertDialog.Builder(EditPlayerActivity.this).create();
	    dialog.setTitle(EditPlayerActivity.this.getString(R.string.Save));
	    final String message = EditPlayerActivity.this.getString(R.string.save_error_empty_name);
	    dialog.setMessage(message);
	    dialog.setCancelable(true);
	    dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(android.R.string.yes),
		    new DialogInterface.OnClickListener()
	    {
		public void onClick(DialogInterface dialog, int buttonId)
		{
		}
	    });
	} else
	{
	    dialog = new AlertDialog.Builder(EditPlayerActivity.this).create();
	    dialog.setTitle(EditPlayerActivity.this.getString(R.string.Save));
	    String message = EditPlayerActivity.this.getString(R.string.save_confirmation);

	    int replaceIndex = -1;
	    for (int i = 0; mUsers != null && i < mUsers.length; i++)
	    {
		if (players[selectedPlayer].getName().equals(mUsers[i].getName()))
		{
		    replaceIndex = i;
		    message += EditPlayerActivity.this.getString(R.string.save_overwrite) + players[selectedPlayer].getName();
		    break;
		}
	    }

	    final int fReplaceIndex = replaceIndex;
	    dialog.setMessage(message);
	    dialog.setCancelable(true);
	    dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(android.R.string.yes),
		    new DialogInterface.OnClickListener()
	    {
		public void onClick(DialogInterface dialog, int buttonId)
		{
		    EditPlayerActivity.this.onValidateSave(fReplaceIndex);
		}
	    });
	    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, EditPlayerActivity.this.getString(android.R.string.cancel),
		    new DialogInterface.OnClickListener()
	    {
		public void onClick(DialogInterface dialog, int buttonId)
		{
		}
	    });
	}
	dialog.show();
    }

    private void loadSavedProfiles()
    {
	String fileName = Constants.PROFILES_FILE_NAME;
	// Bad bad casts here. Then again, this is not meant to be adaptable code, used in x different activities;
	// Worse case scenario it crashes and it'll serve as a reminder to set a listener...
	FileInputStream fis = null;
	try
	{
	    fis = new FileInputStream(Utils.getAppStoragePath() + fileName);

	} catch (Exception e)
	{
	    Log.e(TAG, "Couldn't load file " + fileName);
	}

	Gson gson = new Gson();
	if (fis == null)
	{
	    Log.e(TAG, "Empty Json");
	} else
	{
	    Reader reader = new InputStreamReader(fis);
	    mUsers = gson.fromJson(reader, Player[].class);
	}
    }

    private Player[] addPlayer(final Player player, final Player[] players)
    {
	int length = players == null ? 0 : players.length;
	Player[] newPlayerList = new Player[length + 1];

	for (int i = 0; i < length; i++)
	{
	    newPlayerList[i] = players[i];
	}

	newPlayerList[length] = new Player(player);
	return newPlayerList;
    }

    public void onValidateSave(final int mReplaceIndex)
    {
	if (mReplaceIndex == -1)
	{
	    Toast.makeText(this, "Saving profile " + players[mSelectedPlayer].getName(), Toast.LENGTH_LONG).show();
	    mUsers = addPlayer(players[mSelectedPlayer], mUsers);
	} else
	{
	    Toast.makeText(this, "Replacing profile " + players[mSelectedPlayer].getName(), Toast.LENGTH_LONG).show();
	    mUsers[mReplaceIndex] = new Player(players[mSelectedPlayer]);
	}

	savePlayers();
    }

    private void savePlayers()
    {
	Gson gson = new Gson();
	String json = gson.toJson(mUsers);
	String fileName = Constants.PROFILES_FILE_NAME;

	FileOutputStream fos;
	try
	{
	    File image = new File(Utils.getAppStoragePath(), fileName);
	    if (!image.exists())
	    {
		image.createNewFile();
	    }

	    fos = new FileOutputStream(image);
	    // fos = openFileOutput(externalDir + fileName,
	    // Context.MODE_PRIVATE);
	    fos.write(json.getBytes());
	    fos.close();
	} catch (Exception e)
	{
	    Toast.makeText(this, "JSON WRITE FAILED", Toast.LENGTH_LONG).show();
	    e.printStackTrace();
	}
    }

    private void updateUI(final boolean checkPlaque)
    {
	updateUI(null, checkPlaque);
    }

    private void updateUI()
    {
	updateUI(null, false);
    }

    private void updateUI(final String newPlayerName)
    {
	updateUI(newPlayerName, false);
    }

    // This function should just update what shows on screen, and not change any
    // value. This is not starting a new game!
    @SuppressWarnings("deprecation")
    private void updateUI(final String newPlayerName, final boolean checkPlaques)
    {
	Player player = players[mSelectedPlayer];

	int defaultColor = getResources().getColor(R.color.lifeText);
	final String colorKey = getString(mSelectedPlayer == Constants.PLAYER_ONE ? R.string.key_color_p1 : R.string.key_color_p2);
	final int color = Utils.getIntPreference(colorKey, defaultColor);

	// There is no callback when coming from settings, so we're going to set
	// the player object color property from settings whenever we refresh
	// display.
	// At least the reducancy is used for both UI and save data now. Other
	// values are saved in the object when they are actually being set.
	player.setColor(color);

	if (checkPlaques)
	{
	    mShowPlaque = Utils.getBooleanPreference(getString(R.string.key_show_plaque), true);
	}

	int background_id = player.getBackGroundId();

	mBigLife0.setTextColor(color);
	mMinus0.setTextColor(color);
	mPlus0.setTextColor(color);
	mEditName0.setTextColor(color);
	mEditName0.setText(player.getName());

	String tallUrl = player.getTallBgUrl();
	String largeUrl = player.getLargeBgUrl();
	if (tallUrl != null || largeUrl != null)
	{
	    player0_background.setImageURI(Uri
		    .parse(mOrientation == Configuration.ORIENTATION_PORTRAIT ? tallUrl != null ? tallUrl : largeUrl
			    : largeUrl != null ? largeUrl : tallUrl));
	    player0_background.setScaleType(ScaleType.FIT_XY);
	} else
	{
	    player0_background.setImageResource(backgrounds_ids[background_id]);
	    player0_background.setScaleType(ScaleType.CENTER_CROP);
	}

	// Adjusting editor values
	if (newPlayerName == null)
	{
	    mTextBox.setText(player.getName());
	}

	if (mShowPlaque)
	{
	    mBigLife0.setBackgroundResource(R.drawable.box);
	} else
	{
	    if (Build.VERSION.SDK_INT < 16)
	    {
		mBigLife0.setBackgroundDrawable(null);
	    } else
	    {
		mBigLife0.setBackground(null);
	    }
	}
    }

    private boolean checkOrientation(int orientation)
    {
	if (orientation != mOrientation)
	{
	    mOrientation = orientation;
	    return true;
	}
	return false;
    }

    @Override
    public void onClick(View clickedView)
    {
	if (clickedView.equals(mBigLife0))
	{
	    // Clicking on player 0 life total
	} else if (clickedView.equals(mPlus0))
	{
	    // Clicking on player 1 + button
	} else if (clickedView.equals(mMinus0))
	{
	    // Clicking on player 1 - button
	} else if (clickedView.equals(player0_background))
	{
	    rollBackground(Constants.PLAYER_ZERO);
	}
    }

    private void rollBackground(int player)
    {
	player0_back_number = (player0_back_number + 1) % backgrounds_ids.length;
	// Change background in UI -> save background in object
	players[mSelectedPlayer].setBackGroundId(player0_back_number);
	updateUI();
    }

    // @Override
    // public void onValidateLoad(Player player, int player_slot) {
    // mSelectedPlayer = player_slot;
    // players[player_slot] = new Player(player);
    // updateUI();
    // }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
	if (resultCode == Activity.RESULT_CANCELED)
	{
	    return;
	}
	switch (requestCode)
	{
	    case Constants.REQUEST_PICK_IMAGES:
		players[mSelectedPlayer] = (Player) intent.getParcelableExtra(Constants.KEY_PLAYER_TARGET);
		break;
	}
    }

    @Override
    public void onBackPressed()
    {
	AlertDialog dialog = new AlertDialog.Builder(this).create();
	dialog.setTitle(getString(R.string.Save));
	String message = getString(R.string.save_goback_confirmation);

	dialog.setMessage(message);
	dialog.setCancelable(true);
	dialog.setButton(DialogInterface.BUTTON_POSITIVE, EditPlayerActivity.this.getString(R.string.go_back),
		new DialogInterface.OnClickListener()
	{
	    public void onClick(DialogInterface dialog, int buttonId)
	    {
		setResult(Activity.RESULT_CANCELED, new Intent());
		finish();
	    }
	});
	dialog.setButton(DialogInterface.BUTTON_NEGATIVE, EditPlayerActivity.this.getString(R.string.cancel),
		new DialogInterface.OnClickListener()
	{
	    public void onClick(DialogInterface dialog, int buttonId)
	    {
		dialog.dismiss();
	    }
	});

	dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
	super.onConfigurationChanged(newConfig);

	if (checkOrientation(newConfig.orientation))
	{
	    updateUI();
	}
    }

    @Override
    public void onValidateColor(int color)
    {
	String key = getString(mSelectedPlayer == Constants.PLAYER_TWO ? R.string.key_color_p2 : R.string.key_color_p1);
	Utils.setIntPreference(key, getResources().getColor(color));

	updateUI();
    }
}
