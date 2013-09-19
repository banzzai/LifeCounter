package com.banzz.lifecounter.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.Utils;

public class PickColorDialog extends DialogFragment {
    private final int mSelectedPlayer;
    PickColorDialogListener mListener;

    //TODO fix so that id 6 exists, annoying because it's all hard coded in pick_color.xml
    private int mColor_button_ids[] =
            new int[] {R.id.color_1, R.id.color_2, R.id.color_3, R.id.color_4, R.id.color_5,
                    R.id.color_7, R.id.color_8, R.id.color_9, R.id.color_10, R.id.color_11};
    private int mColor_colors[] =
            new int[] {R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4, R.color.color_5,
                    R.color.color_6, R.color.color_7, R.color.color_8, R.color.color_9, R.color.color_10};
    private int mColors_count = 10;
    private int mCurrentPickedColor = -1;
    private TextView mPreviewText;
    private Context mContext;

    public PickColorDialog(Context context, int selectedPlayer) {
        mContext = context;
        mSelectedPlayer = selectedPlayer;
    }

    public void setListener(PickColorDialogListener mListener) {
		this.mListener = mListener;
	}

	public interface PickColorDialogListener {
		public void onValidateColor(int color);
	}

    private void setCurrentColor(int color)
    {
        mCurrentPickedColor = color;
        mPreviewText.setTextColor(getResources().getColor(color));
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.pick_color, null);
        mPreviewText = (TextView) view.findViewById(R.id.pick_color_preview);

        for (int i=0; i< mColors_count; i++)
        {
            final int thisCount = i;
            Button thisButton = (Button) view.findViewById(mColor_button_ids[thisCount]);
            thisButton.setBackgroundColor(getResources().getColor(mColor_colors[thisCount]));
            thisButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCurrentColor(mColor_colors[thisCount]);
                }
            });
        }

	    Button moreColorButton = (Button) view.findViewById(R.id.more_colors);
        moreColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editColor = new Intent(mContext, SettingsActivity.class);
				editColor.putExtra(Utils.Constants.KEY_PLAYER_TARGET, mSelectedPlayer);
				startActivity(editColor);
                dismiss();
            }
        });
	    //mSelectUser = (ListView) view.findViewById(R.id.player_select);
	    
	    /*final Player[] mUsers;
	    String fileName = Constants.PROFILES_FILE_NAME;
	    boolean empty = true;
	    //Bad bad casts here. Then again, this is not meant to be adaptable code, used in x different activities;
	    //worse case scenario it crashes and it'll serve as a reminder to set a listener...
	    File externalDir = ((Context)mListener).getExternalFilesDir(null);
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
		
			if (mUsers != null && mUsers.length > 0) {
				empty = false;
				mSelectUser.setAdapter(new UserListAdapter(mUsers, getActivity()));
			    mSelectUser.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						mListener.onValidateLoad(mUsers[position], (mRadio.getCheckedRadioButtonId() == R.id.radio_player_1 ? Constants.PLAYER_ONE : Constants.PLAYER_TWO));
						dismiss();
					}
				});
			}
		}
			
		if (empty) {
			mSelectUser.setVisibility(View.GONE);
			view.findViewById(R.id.no_saved_players).setVisibility(View.VISIBLE);
		}*/
		
	    builder.setView(view)
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   PickColorDialog.this.getDialog().cancel();
	               }
	           })
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onValidateColor(mCurrentPickedColor);
                       PickColorDialog.this.getDialog().dismiss();
                   }
               });
	    return builder.create();
	}
}
