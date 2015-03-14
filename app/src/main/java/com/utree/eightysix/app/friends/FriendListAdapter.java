/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.view.SectionedBaseAdapter;
import com.utree.eightysix.widget.AsyncImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class FriendListAdapter extends SectionedBaseAdapter {

  private static String[] INITIALS = {
      "a", "b", "c", "d", "e", "f",
      "g", "h", "i", "j", "k", "l",
      "m", "n", "o", "p", "q", "r",
      "s", "t", "u", "v", "w", "x",
      "y", "z"
  };

  private List<Friend> mFriends;

  private Map<String, List<Friend>> mSections = new HashMap<String, List<Friend>>(26);

  private String[] mSectionKeys;

  public FriendListAdapter(List<Friend> friends) {

    mFriends = friends;

    for (Friend friend : mFriends) {
      String initial = friend.initial;

      List<Friend> section = mSections.get(initial);
      if (section == null) {
        section = new ArrayList<Friend>();
        mSections.put(initial, section);
      }

      section.add(friend);
    }


    mSectionKeys = new String[mSections.keySet().size()];
    mSections.keySet().toArray(mSectionKeys);
  }

  @Override
  public Friend getItem(int section, int position) {
    return mSections.get(mSectionKeys[section]).get(position);
  }

  @Override
  public long getItemId(int section, int position) {
    return section * position;
  }

  @Override
  public int getSectionCount() {
    return mSectionKeys.length;
  }

  @Override
  public int getCountForSection(int section) {
    return mSections.get(mSectionKeys[section]).size();
  }

  @Override
  public View getItemView(int section, int position, View convertView, ViewGroup parent) {
    FriendViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
      holder = new FriendViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (FriendViewHolder) convertView.getTag();
    }

    holder.setData(mSections.get(mSectionKeys[section]).get(position));

    return convertView;
  }

  @Override
  public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head, parent, false);
    }
    ((TextView) convertView.findViewById(R.id.tv_head)).setText(mSectionKeys[section]);
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
      if (!TextUtils.isEmpty(friend.avatar)) {
        mAivPortrait.setUrl(friend.avatar);
      }
      mTvCircleName.setText(friend.workinFactory);
      mTvName.setText(friend.userName);
      mTvSignature.setText(friend.signature);
    }

    FriendViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
