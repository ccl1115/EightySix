package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.utree.eightysix.drawable.GearsDrawable;
import com.utree.eightysix.drawable.SmallGearsDrawable;

/**
 * @author simon
 */
public class GearsView extends View {
  public GearsView(Context context) {
    this(context, null, 0);
  }

  public GearsView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public GearsView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    setBackgroundDrawable(new SmallGearsDrawable());
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }
}
