package com.utree.eightysix.widget.panel;

import android.content.Context;
import android.view.View;

/**
 * @author simon
 */
public class ItemView extends View {
  private Item mItem;

  public ItemView(Context context, Item item) {
    super(context);
    mItem = item;
  }
}
