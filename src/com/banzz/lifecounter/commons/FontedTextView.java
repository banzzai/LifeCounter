package com.banzz.lifecounter.commons;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.banzz.lifecounter.R;
import com.banzz.lifecounter.utils.Utils;

public class FontedTextView extends TextView {
	public FontedTextView(final Context context)
    {
        super(context);
    }

    public FontedTextView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        setFont(attrs);
    }

    public FontedTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        setFont(attrs);
    }
    
    private void setFont(final AttributeSet attrs)
    {
    	final TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.FontedTextView);
    	final String fontValue = styledAttributes.getString(R.styleable.FontedTextView_font);
        
    	if (Utils.Constants.STRING_HELVETICA_NUEUE.equals(fontValue))
        {
        	setTypeface(Utils.Constants.FONT_HELVETICA_NUEUE);
        }
    	else if (Utils.Constants.STRING_HELVETICA_NUEUE_CONDENSED.equals(fontValue))
        {
        	setTypeface(Utils.Constants.FONT_HELVETICA_NUEUE_CONDENSED);
        }
        
        styledAttributes.recycle();
    }
}
