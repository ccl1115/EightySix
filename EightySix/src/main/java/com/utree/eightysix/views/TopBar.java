package com.utree.eightysix.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.utree.eightysix.R;

/**
 */
public class TopBar extends RelativeLayout {

    private TextView mTitle;

    public TopBar(Context context) {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.topBarStyle);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View.inflate(context, R.layout.widget_top_bar, this);

        mTitle = (TextView) findViewById(R.id.top_bar_title);
    }

    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    public String getTitle() {
        if (mTitle != null) {
            return mTitle.getText().toString();
        }
        return null;
    }
}
