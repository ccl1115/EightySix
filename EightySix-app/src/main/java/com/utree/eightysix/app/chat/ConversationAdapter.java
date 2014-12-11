/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.FontPortraitView;

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
    holder.mTvCircle.setText(conversation.getChatSource());
    holder.mTvLast.setText(conversation.getLastMsg());
    holder.mFpvPortrait.setText("\ue800");
    holder.mTvContent.setText(conversation.getPostContent());
    holder.mAivPostBg.setUrl(conversation.getBgUrl());

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


    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
