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
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.CounterView;

import java.util.Comparator;
import java.util.List;

/**
 */
public class FConversationAdapter extends BaseAdapter {

  private List<FriendConversation> mConversations;

  private static Comparator<FriendConversation> sComparator = new Comparator<FriendConversation>() {
    @Override
    public int compare(FriendConversation lhs, FriendConversation rhs) {
      if (lhs.getTimestamp() == null || rhs.getTimestamp() == null) {
        return 0;
      }
      return rhs.getTimestamp().compareTo(lhs.getTimestamp());
    }
  };

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

  @Subscribe
  public void onFriendChatEvent(FriendChatEvent event) {
    switch (event.getStatus()) {
      case FriendChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE: {
        FriendConversation obj = (FriendConversation) event.getObj();
        if (obj == null) return;
        for (int i = 0; i < mConversations.size(); i++) {
          FriendConversation conversation = mConversations.get(i);
          if (conversation.getId().equals(obj.getId())) {
            mConversations.set(i, obj);
            //Collections.sort(mConversations, sComparator);
            notifyDataSetChanged();
            return;
          }
        }

        mConversations.add(obj);
        //Collections.sort(mConversations, sComparator);
        notifyDataSetChanged();
        break;
      }
      case ChatEvent.EVENT_CONVERSATION_UPDATE: {
        FriendConversation obj = (FriendConversation) event.getObj();
        if (obj == null) return;
        for (int i = 0; i < mConversations.size(); i++) {
          FriendConversation conversation = mConversations.get(i);
          if (conversation.getId().equals(obj.getId())) {
            mConversations.set(i, obj);
            //Collections.sort(mConversations, sComparator);
            notifyDataSetChanged();
            return;
          }
        }
        break;
      }
      case ChatEvent.EVENT_CONVERSATIONS_RELOAD: {
        mConversations = FConversationUtil.getConversations();
        notifyDataSetChanged();
        break;
      }
      case ChatEvent.EVENT_CONVERSATION_REMOVE: {
        removeByChatId(((String) event.getObj()));
        break;
      }
    }
  }

  public void removeByChatId(String chatId) {
    for (FriendConversation conversation : mConversations) {
      if (conversation.getChatId().equals(chatId)) {
        mConversations.remove(conversation);
        break;
      }
    }
    notifyDataSetChanged();
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
      mTvTimestamp.setText(ChatUtils.timestamp(mConversation.getTimestamp()));
      mCvNew.setCount(mConversation.getUnreadCount().intValue());
    }

    ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
