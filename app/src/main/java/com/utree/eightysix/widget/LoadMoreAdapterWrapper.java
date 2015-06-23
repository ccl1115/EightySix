package com.utree.eightysix.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.utree.eightysix.R;

/**
 * @author simon
 */
class LoadMoreAdapterWrapper extends BaseAdapter {

  private boolean mIsLoading;

  private ListAdapter mListAdapter;

  private LoadMoreCallback mCallback;

  private int mLoadMoreType;
  private boolean mIsError;


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
    return (hasMore() ? 1 : 0) + mListAdapter.getCount();
  }

  @Override
  public Object getItem(int position) {
    if (hasMore())
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
      if (convertView == null) convertView = mCallback.getLoadMoreView(parent);

      if (mIsError) {
        convertView.setVisibility(View.VISIBLE);
        convertView.findViewById(R.id.pb_loading).setVisibility(View.GONE);
        ((TextView) convertView.findViewById(R.id.tv_loading)).setText("点击加载更多");
        final View finalConvertView = convertView;
        finalConvertView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!mIsLoading) {
              finalConvertView.setVisibility(View.VISIBLE);
              finalConvertView.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
              ((TextView) finalConvertView.findViewById(R.id.tv_loading)).setText("正在加载更多");
              finalConvertView.setOnClickListener(null);
              mIsLoading = mCallback.onLoadMoreStart();
            }
          }
        });
      } else if (mCallback.hasMore()) {
        convertView.setVisibility(View.VISIBLE);
        convertView.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
        ((TextView) convertView.findViewById(R.id.tv_loading)).setText("正在加载更多");
        convertView.setOnClickListener(null);
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
    return hasMore() ?
        (position == getCount() - 1 ? mLoadMoreType : mListAdapter.getItemViewType(position)) :
        mListAdapter.getItemViewType(position);
  }

  private boolean hasMore() {
    return mCallback != null && mCallback.hasMore();
  }

  @Override
  public int getViewTypeCount() {
    return 1 + mListAdapter.getViewTypeCount();
  }

  public void stopLoadMore() {
    mIsLoading = false;
    mIsError = false;
    notifyDataSetChanged();
  }

  public void loadError() {
    mIsError = true;
    mIsLoading = false;
    notifyDataSetChanged();
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

  @Override
  public void registerDataSetObserver(DataSetObserver observer) {
    super.registerDataSetObserver(observer);
    mListAdapter.registerDataSetObserver(observer);
  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver observer) {
    super.unregisterDataSetObserver(observer);
    mListAdapter.unregisterDataSetObserver(observer);
  }

}
