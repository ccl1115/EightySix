/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.FontPortraitView;
import com.utree.eightysix.widget.RoundedButton;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ConversationAdapter extends BaseAdapter {

  private List<Conversation> mConversations = new ArrayList<Conversation>();

  public ConversationAdapter() {
    mConversations = DaoUtils.getConversationDao().loadAll();
  }

  @Override
  public int getCount() {
    return mConversations.size();
  }

  @Override
  public Conversation getItem(int i) {
    return mConversations.get(i);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Conversation conversation = getItem(position);

    holder.mTvName.setText(conversation.getRelation());
    holder.mTvCircle.setText(conversation.getPostSource());
    holder.mTvLast.setText(conversation.getLastMsg());
    holder.mFpvPortrait.setText("\ue800");
    holder.mTvContent.setText(conversation.getPostContent());
    int unread = conversation.getUnreadCount().intValue();
    if (unread == 0) {
      holder.mRbUnread.setVisibility(View.INVISIBLE);
    } else {
      holder.mRbUnread.setVisibility(View.VISIBLE);
      if (unread > 99) {
        holder.mRbUnread.setText("99+");
      } else {
        holder.mRbUnread.setText(String.valueOf(unread));
      }
    }
    if (TextUtils.isEmpty(conversation.getBgUrl())) {
      holder.mAivPostBg.setBackgroundColor(ColorUtil.strToColor(conversation.getBgColor()));
      holder.mAivPostBg.setUrl(null);
    } else {
      holder.mAivPostBg.setUrl(conversation.getBgUrl());
    }
    holder.mTvLast.setText(conversation.getLastMsg());
    holder.mTvTime.setText(ChatUtils.timestamp(conversation.getTimestamp()));

    holder.mFpvPortrait.setTextColor(Color.BLUE);
    holder.mFpvPortrait.setBackgroundDrawable(new RoundRectDrawable(Integer.MAX_VALUE, ColorUtil.lighten(Color.BLUE)));

    holder.mTvStatus.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(2), Color.GREEN));

    return convertView;
  }

  public void removeByChatId(String chatId) {
    for (Conversation conversation : mConversations) {
      if (conversation.getChatId().equals(chatId)) {
        mConversations.remove(conversation);
        break;
      }
    }
    notifyDataSetChanged();
  }

  @Subscribe
  public void onChatEvent(ChatEvent event) {
    if (event.getStatus() == ChatEvent.EVENT_CONVERSATION_UPDATE) {
      Conversation obj = (Conversation) event.getObj();
      for (int i = 0; i < mConversations.size(); i++) {
        Conversation conversation = mConversations.get(i);
        if (conversation.getId().equals(obj.getId())) {
          mConversations.set(i, obj);
          notifyDataSetChanged();
          break;
        }
      }
    } else if (event.getStatus() == ChatEvent.EVENT_CONVERSATIONS_RELOAD) {
      mConversations = DaoUtils.getConversationDao().loadAll();
      notifyDataSetChanged();
    }
  }

  public void remove(Conversation conversation) {
    mConversations.remove(conversation);
    notifyDataSetChanged();
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_name)
    public TextView mTvName;

    @InjectView(R.id.tv_time)
    public TextView mTvTime;

    @InjectView(R.id.tv_circle)
    public TextView mTvCircle;

    @InjectView(R.id.tv_status)
    public TextView mTvStatus;

    @InjectView(R.id.tv_last)
    public TextView mTvLast;

    @InjectView(R.id.fpv_portrait)
    public FontPortraitView mFpvPortrait;

    @InjectView(R.id.tv_content)
    public TextView mTvContent;

    @InjectView(R.id.aiv_post_bg)
    public AsyncImageView mAivPostBg;

    @InjectView(R.id.rb_unread)
    public RoundedButton mRbUnread;


    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
