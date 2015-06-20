/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.snapshot;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedPostView;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;

import java.util.List;

/**
 */
public class FeedAdapter extends BaseAdapter {

  private List<BaseItem> mPosts;

  public FeedAdapter(List<BaseItem> posts) {
    mPosts = posts;
  }

  public void add(List<BaseItem> posts) {
    mPosts.addAll(posts);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mPosts.size();
  }

  @Override
  public BaseItem getItem(int position) {
    return mPosts.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new FeedPostView(parent.getContext());
    }
    ((FeedPostView) convertView).setData((Post) getItem(position));

    int p = U.dp2px(3);
    if (position == getCount() - 1) {
      convertView.setPadding(0, p, 0, p);
    } else {
      convertView.setPadding(0, p, 0, 0);
    }
    return convertView;
  }
}
