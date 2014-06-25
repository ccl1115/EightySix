package com.utree.eightysix.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * @author simon
 */
class LoadMoreAdapterWrapper extends BaseAdapter {

  private boolean mIsLoading;

  private ListAdapter mListAdapter;

  private LoadMoreCallback mCallback;

  private int mLoadMoreType;


  public LoadMoreAdapterWrapper(ListAdapter adapter, LoadMoreCallback moreCallback) {
    this(adapter);
    mCallback = moreCallback;
  }

  public LoadMoreAdapterWrapper(ListAdapter adapter) {
    mListAdapter = adapter;
    mLoadMoreType = mListAdapter.getViewTypeCount();
  }

  public void setLoadMoreCallback(LoadMoreCallback callback) {
    mCallback = callback;
    notifyDataSetInvalidated();
  }

  @Override
  public int getCount() {
    return (hasCallback() ? 1 : 0) + mListAdapter.getCount();
  }

  @Override
  public Object getItem(int position) {
    if (hasCallback())
      if (position == getCount() - 1) return null;
      else return mListAdapter.getItem(position);
    else return mListAdapter.getItem(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (getItemViewType(position) == mLoadMoreType) {
      if (convertView == null) convertView = mCallback.getLoadMoreView();

      if (mCallback.hasMore()) {
        convertView.setVisibility(View.VISIBLE);
        if (!mIsLoading) {
          mIsLoading = mCallback.onLoadMoreStart();
        }
      } else {
        convertView.setVisibility(View.GONE);
      }

    } else {
      convertView = mListAdapter.getView(position, convertView, parent);
    }
    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    return hasCallback() ?
        (position == getCount() - 1 ? mLoadMoreType : mListAdapter.getItemViewType(position)) :
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
    return mCallback != null;
  }

  @Override
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    ((BaseAdapter) mListAdapter).notifyDataSetChanged();
  }

  @Override
  public void notifyDataSetInvalidated() {
    super.notifyDataSetInvalidated();
    ((BaseAdapter) mListAdapter).notifyDataSetInvalidated();
  }
}
