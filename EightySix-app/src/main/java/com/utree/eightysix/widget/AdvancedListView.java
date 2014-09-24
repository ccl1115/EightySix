package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;

/**
 * @author simon
 */
public final class AdvancedListView extends ListView {

  private LoadMoreAdapterWrapper mAdapterWrapper;

  private LoadMoreCallback mLoadMoreCallback;

  public AdvancedListView(Context context) {
    this(context, null, 0);
  }

  public AdvancedListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AdvancedListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setCacheColorHint(0x00000000);
    setSelector(getResources().getDrawable(R.drawable.transparent));
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
      mAdapterWrapper = null;
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

  @Subscribe
  public void onAdapterDataSetChangedEvent(AdapterDataSetChangedEvent event) {
    if (mAdapterWrapper != null) {
      mAdapterWrapper.notifyDataSetChanged();
    }
  }
}
