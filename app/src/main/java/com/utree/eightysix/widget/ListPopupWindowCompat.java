package com.utree.eightysix.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;

/**
 */
@SuppressLint ("NewApi")
public class ListPopupWindowCompat {

  public ListPopupWindow mListPopupWindow;

  public ListPopupWindowCompat(Context context) {
    mListPopupWindow = new ListPopupWindow(context);
  }

  public void setModal(boolean modal) {
    mListPopupWindow.setModal(modal);
  }

  public void setWidth(int width) {
    mListPopupWindow.setWidth(width);
  }

  public void setAdapter(ListAdapter adapter) {
    mListPopupWindow.setAdapter(adapter);
  }

  public void setDropDownGravity(int gravity) {
    mListPopupWindow.setDropDownGravity(gravity);
  }

  public void setAnchorView(View view) {
    mListPopupWindow.setAnchorView(view);
  }

  public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
    mListPopupWindow.setOnItemClickListener(listener);
  }

  public void dismiss() {
    mListPopupWindow.dismiss();
  }

  public void show() {
    mListPopupWindow.show();
  }
}
