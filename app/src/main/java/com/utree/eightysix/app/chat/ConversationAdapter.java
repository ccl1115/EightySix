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
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.FontPortraitView;
import com.utree.eightysix.widget.RoundedButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 */
public class ConversationAdapter extends BaseAdapter {

  private List<Conversation> mConversations = new ArrayList<Conversation>();

  private static Comparator<Conversation> sComparator = new Comparator<Conversation>() {
    @Override
    public int compare(Conversation lhs, Conversation rhs) {
      if (lhs.getTimestamp() == null || rhs.getTimestamp() == null) {
        return 0;
      }
      return lhs.getTimestamp().compareTo(rhs.getTimestamp());
    }
  };

  public ConversationAdapter() {
    mConversations = ChatUtils.ConversationUtil.getConversations();
  }

  public Conversation getByChatId(String chatId) {
    for (Conversation conversation : mConversations) {
      if (conversation.getChatId().equals(chatId)) {
        return conversation;
      }
    }
    return null;
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

    final Conversation conversation = getItem(position);

    if (conversation.getFavorite()) {
      convertView.setBackgroundColor(parent.getResources().getColor(R.color.apptheme_primary_light_color_100));
    } else {
      convertView.setBackgroundColor(0x00000000);
    }

    holder.mTvName.setText(conversation.getRelation());
    holder.mTvCircle.setText(conversation.getPostSource());
    holder.mTvLast.setText(conversation.getLastMsg());

    String portrait = conversation.getPortrait();
    if (portrait != null) {
      if ("\ue800".equals(portrait)) {
        holder.mFpvPortrait.setEmotion(' ');
        holder.mFpvPortrait.setBackgroundResource(R.drawable.host_portrait);
      } else {
        holder.mFpvPortrait.setEmotion(portrait.charAt(0));
        holder.mFpvPortrait.setEmotionColor(ColorUtil.strToColor(conversation.getPortraitColor()));
      }
    }

    holder.mTvContent.setText(conversation.getPostContent());
    int unread = conversation.getUnreadCount() == null ? 0 : conversation.getUnreadCount().intValue();
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
    holder.mTvTime.setText(ChatUtils.timestamp(conversation.getTimestamp() == null ? 0 : conversation.getTimestamp()));

    holder.mTvStatus.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(2), Color.GREEN));

    holder.mFlPost.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        PostActivity.start(view.getContext(), conversation.getPostId());
      }
    });

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
          Collections.sort(mConversations, sComparator);
          notifyDataSetChanged();
          return;
        }
      }

      mConversations.add(obj);
      Collections.sort(mConversations, sComparator);
      notifyDataSetChanged();
    } else if (event.getStatus() == ChatEvent.EVENT_CONVERSATIONS_RELOAD) {
      mConversations = ChatUtils.ConversationUtil.getConversations();
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

    @InjectView(R.id.fl_post)
    public FrameLayout mFlPost;


    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
