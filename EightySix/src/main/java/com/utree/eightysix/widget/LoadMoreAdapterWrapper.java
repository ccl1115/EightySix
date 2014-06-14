package com.utree.eightysix.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * @author simon
 */
class LoadMoreAdapterWrapper extends BaseAdapter {

  private static final int TYPE_LOAD_MORE = 0;

  private boolean mIsLoading;

  private ListAdapter mListAdapter;

  private LoadMoreCallback mCallback;


  public LoadMoreAdapterWrapper(ListAdapter adapter, LoadMoreCallback moreCallback) {
    mListAdapter = adapter;
    mCallback = moreCallback;
  }

  public LoadMoreAdapterWrapper(ListAdapter adapter) {
    mListAdapter = adapter;
  }

  public void setLoadMoreCallback(LoadMoreCallback callback) {
    mCallback = callback;
  }

  @Override
  public int getCount() {
    return (hasCallback() ? 1 : 0) + mListAdapter.getCount();
  }

  @Override
  public Object getItem(int position) {
    return hasCallback() ?
        (position == getCount() - 1 ? null : mListAdapter.getItem(position)) :
        mListAdapter.getItem(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (getItemViewType(position) == TYPE_LOAD_MORE) {
      if (convertView == null) convertView = mCallback.getLoadMoreView();

      if (mCallback.hasMore()) {
        convertView.setVisibility(View.VISIBLE);
        if (!mIsLoading) {
          mIsLoading = mCallback.onLoadMoreStart();
        }
      } else {
        convertView.setVisibility(View.GONE);
      }

      return convertView;
    } else {
      return mListAdapter.getView(position, convertView, parent);
    }
  }

  @Override
  public int getItemViewType(int position) {
    return hasCallback() ?
        position == getCount() - 1 ? TYPE_LOAD_MORE : mListAdapter.getItemViewType(position) :
        mListAdapter.getItemViewType(position);
  }

  @Override
  public int getViewTypeCount() {
    return (hasCallback() ? 1 : 0) + mListAdapter.getViewTypeCount();
  }

  public void stopLoadMore() {
    mIsLoading = false;
    notifyDataSetChanged();
  }

  private boolean hasCallback() {
    return mCallback == null;
  }
}
