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

  private LoadMoreCallback mLoadMoreCallback;

  public AdvancedListView(Context context) {
    super(context);
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
      if (mLoadMoreCallback != null) {
        mAdapterWrapper.setLoadMoreCallback(mLoadMoreCallback);
      }
      super.setAdapter(mAdapterWrapper);
    } else {
      super.setAdapter(null);
    }
  }

  public void setLoadMoreCallback(LoadMoreCallback callback) {
    mLoadMoreCallback = callback;
    if (mAdapterWrapper != null) {
      mAdapterWrapper.setLoadMoreCallback(callback);
      super.setAdapter(mAdapterWrapper);
    }
  }

  public void stopLoadMore() {
    if (mAdapterWrapper != null) {
      mAdapterWrapper.stopLoadMore();
    }
  }
}
