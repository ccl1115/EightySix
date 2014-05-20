package com.utree.eightysix.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.utree.eightysix.R;

/**
 */
public class TopBar extends RelativeLayout {

    public TopBar(Context context) {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.topBarStyle);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
