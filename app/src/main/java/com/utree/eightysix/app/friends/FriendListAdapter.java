/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.view.SectionedBaseAdapter;
import com.utree.eightysix.widget.AsyncImageView;

import java.util.*;

/**
 */
public class FriendListAdapter extends SectionedBaseAdapter {

  private SortedMap<String, List<Friend>> mSections = new TreeMap<String, List<Friend>>();

  private String[] mSectionKeys;
  private int[] mSectionPositions;

  public FriendListAdapter(List<Friend> friends) {
    for (Friend friend : friends) {
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
    mSectionPositions = new int[mSections.keySet().size()];

    int index = 0;
    int size = 0;
    mSectionPositions[index] = 0;
    for (Map.Entry<String, List<Friend>> entry : mSections.entrySet()) {
      index += 1;
      if (index >= mSectionPositions.length) break;
      size = size + (entry.getValue().size() + 1);
      mSectionPositions[index] = size;
    }
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

    final Friend friend = mSections.get(mSectionKeys[section]).get(position);
    holder.setData(friend);

    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle args = new Bundle();
        args.putInt("viewId", friend.viewId);
        args.putBoolean("isVisitor", true);
        args.putString("userName", friend.userName);
        FragmentHolder.start(v.getContext(), ProfileFragment.class, args);
      }
    });

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

  public int getSectionIndex(int index) {
    return mSectionPositions[index];
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
    public void onIvChatClicked(View v) {
      ChatUtils.startFriendChat((BaseActivity) v.getContext(), mFriend.viewId);
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
