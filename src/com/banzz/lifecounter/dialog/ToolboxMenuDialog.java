package com.banzz.lifecounter.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.common.Utils;

import java.util.Random;

public class ToolboxMenuDialog extends DialogFragment {
    private Context mContext;
    private ToolBoxDialogListener mListener;

    public ToolboxMenuDialog(final Context context, final ToolBoxDialogListener listener) {
        //super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        mContext = context;
        mListener = listener;
    }

    //Don't want to bother with save/load instances right now...
    public ToolboxMenuDialog() {
        //super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dismiss();
    }

    public interface ToolBoxDialogListener {
        public void onPickAction(final int action);
    }

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.toolbox_menu, null);

	    Button moreColorButton = (Button) view.findViewById(R.id.option_restart);
        moreColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListener.onPickAction(Utils.Constants.ACTION_RESTART);
                dismiss();
            }
        });

        Button coinButton = (Button) view.findViewById(R.id.option_coin);
        coinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                flip_coin();
            }
        });

        Button DiceButton = (Button) view.findViewById(R.id.option_dice);
        DiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                roll_dice(20);
            }
        });

        Button loadButton = (Button) view.findViewById(R.id.option_load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListener.onPickAction(Utils.Constants.ACTION_LOAD_PLAYERS);
                dismiss();
            }
        });

        Button rotateButton = (Button) view.findViewById(R.id.option_rotate);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListener.onPickAction(Utils.Constants.ACTION_FLIP_PLAYER_2);
                dismiss();
            }
        });
		
	    builder.setView(view);
	    return builder.create();
	}

    private void roll_dice(final int max_value) {
        Random random = new Random();
        //Both random and % seems to like negative values, hence the double trick...
        int dice_value = (((random.nextInt() % max_value) + max_value) % max_value) + 1;
        Toast dice_rolled = Toast.makeText(mContext, dice_value + (dice_value == 1 ? " :(" : dice_value == max_value ? "!!" : ""), Toast.LENGTH_SHORT);
        dice_rolled.show();
    }


    private void flip_coin() {
        Random random = new Random();
        Toast coin_tossed = Toast.makeText(mContext, random.nextInt() % 2 == 0 ? "HEADS" : "TAILS", Toast.LENGTH_SHORT);
        coin_tossed.show();
    }
}
