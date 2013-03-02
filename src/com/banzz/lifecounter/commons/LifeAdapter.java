package com.banzz.lifecounter.commons;

import com.banzz.lifecounter.R;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LifeAdapter extends AbstractWheelTextAdapter {
    private final int mMinimum;
    private final int mMaximum;
    
    /**
     * Constructor
     */
    public LifeAdapter(Context context, int min, int max) {
        super(context, R.layout.life_layout, NO_RESOURCE);
        mMinimum = min;
        mMaximum = max;
        
        setItemTextResource(R.id.life_count);
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        ImageView img = (ImageView) view.findViewById(R.id.flag);
        TextView text = (TextView) view.findViewById(R.id.life_count);
        
        if (index == mMaximum - mMinimum) {
        	img.setImageResource(R.drawable.death);
            img.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        } else {
        	img.setVisibility(View.GONE);
        	text.setVisibility(View.VISIBLE);
        }
        return view;
    }
    
    @Override
    public int getItemsCount() {
        return mMaximum - mMinimum + 1;
    }
    
    @Override
    protected CharSequence getItemText(int index) {
        return String.format("%02d", mMaximum - index);
    }
}
