package com.utree.eightysix.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;

/**
 */
public class TagView extends TextView {

  public TagView(Context context, AttributeSet attrs) {
    super(context, attrs);

  }


  @Override
  public void setText(CharSequence text, BufferType type) {
    super.setText(text, type);

    if (TextUtils.isEmpty(text)) {
      setBackgroundDrawable(null);
      setPadding(0, 0, 0, 0);
    } else {
      setBackgroundDrawable(new RoundRectDrawable(U.dp2px(4), getResources().getColorStateList(R.color.apptheme_primary_transparent_btn)));
      final int p = U.dp2px(4);
      setPadding(p, 0, p, 0);
    }

  }

}
