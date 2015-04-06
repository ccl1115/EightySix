/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.CounterView;

import java.util.List;

/**
 */
public class FConversationAdapter extends BaseAdapter {

  private List<FriendConversation> mConversations;

  public FConversationAdapter(List<FriendConversation> conversations) {
    mConversations = conversations;
  }

  public void add(List<FriendConversation> conversations) {
    mConversations.addAll(conversations);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mConversations.size();
  }

  @Override
  public FriendConversation getItem(int position) {
    return mConversations.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fconversation, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.setData(getItem(position));

    return convertView;
  }

  @Keep
  static class ViewHolder {

    @InjectView(R.id.tv_name)
    TextView mTvName;

    @InjectView(R.id.tv_last)
    TextView mTvLastMsg;

    @InjectView(R.id.tv_timestamp)
    TextView mTvTimestamp;

    @InjectView(R.id.tv_count)
    CounterView mCvNew;

    @InjectView(R.id.aiv_portrait)
    AsyncImageViewWithRoundCorner mAivPortrait;

    private FriendConversation mConversation;

    void setData(FriendConversation conversation) {
      mConversation = conversation;

      mAivPortrait.setUrl(mConversation.getTargetAvatar());
      mTvName.setText(mConversation.getTargetName());
      mTvLastMsg.setText(mConversation.getLastMsg());
      mTvTimestamp.setText(TimeUtil.getElapsed(mConversation.getTimestamp()));
      mCvNew.setText(String.valueOf(mConversation.getUnreadCount()));
    }

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
