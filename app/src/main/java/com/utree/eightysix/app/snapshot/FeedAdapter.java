/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.snapshot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.utree.eightysix.R;
import com.utree.eightysix.app.feed.FeedPostView;
import com.utree.eightysix.data.Post;

import java.util.List;

/**
 */
public class FeedAdapter extends BaseAdapter {

  private List<Post> mPosts;

  public FeedAdapter(List<Post> posts) {
    mPosts = posts;
  }

  public void add(List<Post> posts) {
    mPosts.addAll(posts);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mPosts.size();
  }

  @Override
  public Post getItem(int position) {
    return mPosts.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_post, parent, false);
    }
    ((FeedPostView) convertView).setData(getItem(position));
    return convertView;
  }
}
