package com.utree.eightysix.widget.panel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.AsyncImageDrawable;
import com.utree.eightysix.utils.ImageUtils;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class ItemView extends View implements View.OnClickListener {

  private Item mItem;

  private Drawable mDrawable;

  private StateListDrawable mSelectDrawableList;

  public ItemView(Context context, Item item) {
    super(context);
    mItem = item;

    mSelectDrawableList = (StateListDrawable) getResources().getDrawable(R.drawable.apptheme_transparent_bg);
    mSelectDrawableList.setCallback(this);

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
    invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (mDrawable != null) {
      mDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
    mSelectDrawableList.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());

  }

  @Override
  protected boolean verifyDrawable(Drawable who) {
    return who == mDrawable || who == mSelectDrawableList || super.verifyDrawable(who);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    if (mDrawable != null) {
      mDrawable.draw(canvas);
    }
    mSelectDrawableList.draw(canvas);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    TypedValue value = mItem.getValue();
    if (value.type == TypedValue.TYPE_INT_COLOR_ARGB8) {
      mDrawable = new ColorDrawable(value.data);
    } else if(value.type == TypedValue.TYPE_STRING) {
      mDrawable = new AsyncImageDrawable(getResources(), value.string.toString());
    } else if (value.type == TypedValue.TYPE_REFERENCE) {
      String imageUrl = U.getCloudStorage().getUrl(U.getConfig("storage.bg.bucket.name"),
          "",
          getResources().getResourceEntryName(value.resourceId) + ".png");
      mDrawable = new BitmapDrawable(getResources(),
          ImageUtils.syncLoadResourceBitmapThumbnail(value.resourceId, ImageUtils.getUrlHash(imageUrl)));
    }
    if (mDrawable != null) {
      U.getBus().register(mDrawable);
      mDrawable.setCallback(this);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    if (mDrawable != null) {
      U.getBus().unregister(mDrawable);
    }
    super.onDetachedFromWindow();
  }

}
