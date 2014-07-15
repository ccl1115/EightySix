package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.utree.eightysix.R;
import java.util.Random;

/**
 * @author simon
 */
public class RandomSceneTextView extends TextView {

  private static final int[] RES = {
      R.drawable.scene_1,
      R.drawable.scene_2,
      R.drawable.scene_3,
      R.drawable.scene_4,
      R.drawable.scene_5,
      R.drawable.scene_6
  };

  public RandomSceneTextView(Context context) {
    this(context, null);
  }

  public RandomSceneTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

    setCompoundDrawablesWithIntrinsicBounds(0, RES[new Random().nextInt(6)], 0, 0);
    setGravity(Gravity.CENTER);
    setTextSize(18);
  }
}
