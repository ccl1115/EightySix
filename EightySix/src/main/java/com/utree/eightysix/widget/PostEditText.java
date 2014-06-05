package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import com.utree.eightysix.R;

/**
 */
public class PostEditText extends EditText {
    public PostEditText(Context context) {
        this(context, null);
    }

    public PostEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.postEditTextStyle);
    }

    public PostEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
