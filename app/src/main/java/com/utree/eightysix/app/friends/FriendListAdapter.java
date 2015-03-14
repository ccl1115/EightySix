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
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.widget.AsyncImageView;

import java.util.List;

/**
 */
public class FriendListAdapter extends BaseAdapter {

  private List<Friend> mFriends;

  public FriendListAdapter(List<Friend> friends) {

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
    FriendViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
      holder = new FriendViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (FriendViewHolder) convertView.getTag();
    }

    Friend friend = getItem(position);

    holder.setData(friend);

    return convertView;
  }

  public static class FriendViewHolder {

    @InjectView(R.id.aiv_portrait)
    public AsyncImageView mAivPortrait;

    @InjectView(R.id.tv_name)
    public TextView mTvName;

    @InjectView(R.id.tv_signature)
    public TextView mTvSignature;

    @InjectView(R.id.tv_circle_name)
    public TextView mTvCircleName;

    private Friend mFriend;

    @OnClick(R.id.iv_chat)
    public void onIvChatClicked() {
    }

    public void setData(Friend friend) {
      mFriend = friend;
      mAivPortrait.setUrl(friend.avatar);
      mTvCircleName.setText(friend.workinFactory);
      mTvName.setText(friend.userName);
      mTvSignature.setText(friend.signature);
    }

    FriendViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
