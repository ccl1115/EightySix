/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;

import java.util.List;

/**
 */
public class UserSearchAdapter extends BaseAdapter {

  List<Friend> mFriends;

  public UserSearchAdapter(List<Friend> friends) {
    mFriends = friends;
  }

  @Override
  public int getCount() {
    return mFriends.size();
  }

  @Override
  public Friend getItem(int position) {
    return mFriends.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.setData(getItem(position));

    return convertView;
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_username)
    public TextView mTvUsername;

    @InjectView(R.id.aiv_portrait)
    public AsyncImageViewWithRoundCorner mAivPortrait;

    @InjectView(R.id.aiv_level_icon)
    public AsyncImageView mAivLevelIcon;

    @InjectView(R.id.tv_source)
    public TextView mTvSource;

    @InjectView(R.id.tv_relation)
    public TextView mTvRelation;

    public void setData(Friend friend) {
      mTvUsername.setText(friend.userName);
      mTvSource.setText(friend.workinFactory);
      mTvRelation.setText(friend.relation);
      mAivLevelIcon.setUrl(friend.levelIcon);
      mAivPortrait.setUrl(friend.avatar);
    }

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
