package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author simon
 */
public final class AdvancedListView extends ListView {

  private LoadMoreAdapterWrapper mAdapterWrapper;

  public AdvancedListView(Context context) {
    this(context, null, 0);
  }

  public AdvancedListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AdvancedListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

  }

  @Override
  public void setAdapter(ListAdapter adapter) {
    if (adapter != null) {
      mAdapterWrapper = new LoadMoreAdapterWrapper(adapter);
      super.setAdapter(mAdapterWrapper);
    } else {
      super.setAdapter(null);
    }
  }

  public void setLoadMoreCallback(LoadMoreCallback callback) {
    if (mAdapterWrapper != null) {
      mAdapterWrapper.setLoadMoreCallback(callback);
      mAdapterWrapper.notifyDataSetChanged();
    }
  }

  public void stopLoadMore() {
    if (mAdapterWrapper != null) {
      mAdapterWrapper.stopLoadMore();
    }
  }
}
