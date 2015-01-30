package com.utree.eightysix.widget.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.ImageUtils;

/**
 * @author simon
 */
@SuppressLint("ViewConstructor")
public class ItemView extends ImageView implements View.OnClickListener {

  private Item mItem;

  private Drawable mDrawable;

  public ItemView(Context context, Item item) {
    super(context);
    mItem = item;

    setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    U.getBus().post(mItem);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    TypedValue value = mItem.getValue();
    if (value.type == TypedValue.TYPE_INT_COLOR_ARGB8) {
      mDrawable = new ColorDrawable(value.data);
      setImageDrawable(mDrawable);
    } else if(value.type == TypedValue.TYPE_STRING) {
      Picasso.with(getContext()).load(value.string.toString()).resize(U.dp2px(48), U.dp2px(48)).into(this);
    } else if (value.type == TypedValue.TYPE_REFERENCE) {
      String imageUrl = U.getCloudStorage().getUrl(U.getBgBucket(),
          "",
          getResources().getResourceEntryName(value.resourceId) + ".jpg");
      mDrawable = new BitmapDrawable(getResources(),
          ImageUtils.syncLoadResourceBitmapThumbnail(value.resourceId, ImageUtils.getUrlHash(imageUrl)));
      setImageDrawable(mDrawable);
    }

  }
}
