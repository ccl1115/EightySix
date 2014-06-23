package com.utree.eightysix.widget.panel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class ItemView extends View implements View.OnClickListener {

  private Item mItem;

  private List<Drawable> mDrawables = new ArrayList<Drawable>();

  private StateListDrawable mSelectDrawableList;
  private Drawable mSelectDrawable;

  public ItemView(Context context, Item item) {
    super(context);
    mItem = item;

    for (TypedValue value : mItem.getValues()) {
      if (value.type == TypedValue.TYPE_INT_COLOR_ARGB8) {
        mDrawables.add(new ColorDrawable(value.data));
      }
    }

    mSelectDrawableList = (StateListDrawable) getResources().getDrawable(R.drawable.apptheme_transparent_bg);
    mSelectDrawable = mSelectDrawableList.getCurrent();

    setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    U.getBus().post(mItem);
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    mSelectDrawableList.setState(getDrawableState());
    mSelectDrawable = mSelectDrawableList.getCurrent();
    invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    for (Drawable drawable : mDrawables) {
      drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    mSelectDrawableList.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    mSelectDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
  }

  @Override
  protected void onDraw(Canvas canvas) {
    for (Drawable drawable : mDrawables) {
      drawable.draw(canvas);
    }

    mSelectDrawable.draw(canvas);
  }
}
