/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.chat.content.ImageContent;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.RoundedButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author simon
 */
public class FChatAdapter extends BaseAdapter {


  private static final int TYPE_COUNT = 7;
  private static final int TYPE_INVALID = 0;
  private static final int TYPE_TEXT_FROM = 1;
  private static final int TYPE_TEXT_TO = 2;
  private static final int TYPE_INFO = 3;
  private static final int TYPE_TIMESTAMP = 4;
  private static final int TYPE_IMAGE_FROM = 5;
  private static final int TYPE_IMAGE_TO = 6;


  private List<FriendMessage> mMessages;
  private Comparator<FriendMessage> mMessageComparator;

  private String mMyPortraitUrl;
  private String mTargetPortraitUrl;
  private int mTargetViewId;

  {
    mMessageComparator = new Comparator<FriendMessage>() {
      @Override
      public int compare(FriendMessage lhs, FriendMessage rhs) {
        if (lhs.getTimestamp() > rhs.getTimestamp()) {
          return 1;
        } else if (lhs.getTimestamp() < rhs.getTimestamp()) {
          return -1;
        } else {
          return 0;
        }
      }
    };
  }

  public FChatAdapter() {
    mMessages = new ArrayList<FriendMessage>();
  }

  public FChatAdapter(FriendConversation conversation) {
    this();
    mMyPortraitUrl = conversation.getMyAvatar();
    mTargetPortraitUrl = conversation.getTargetAvatar();
    mTargetViewId = conversation.getViewId();
  }

  public void add(FriendMessage message) {
    if (message == null) {
      return;
    }

    if (!contains(message)) {
      mMessages.add(message);
    }
    addTimestampMessages();
    Collections.sort(mMessages, mMessageComparator);
    notifyDataSetChanged();
  }

  public void add(List<FriendMessage> messages) {
    mMessages.addAll(messages);
    addTimestampMessages();
    Collections.sort(mMessages, mMessageComparator);
    notifyDataSetChanged();
  }

  private boolean contains(FriendMessage message) {
    for (FriendMessage m : mMessages) {
      if (m.getId() != null && m.getId().equals(message.getId())) {
        return true;
      }
    }
    return false;
  }

  public void remove(FriendMessage message) {
    if (contains(message)) {
      mMessages.remove(message);
    }
    notifyDataSetChanged();
  }

  public FriendMessage get(int index) {
    return mMessages.get(index);
  }

  public int size() {
    return mMessages.size();
  }

  @Override
  public int getCount() {
    return mMessages.size();
  }

  @Override
  public FriendMessage getItem(int position) {
    return mMessages.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_TEXT_FROM:
        return getTextFromView(position, convertView, parent);
      case TYPE_TEXT_TO:
        return getTextToView(position, convertView, parent);
      case TYPE_IMAGE_FROM:
        return getImageFromView(position, convertView, parent);
      case TYPE_IMAGE_TO:
        return getImageToView(position, convertView, parent);
      case TYPE_INFO:
        return getInfoView(position, convertView, parent);
      case TYPE_TIMESTAMP:
        return getTimestampView(position, convertView, parent);
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    FriendMessage m = getItem(position);

    switch (m.getType()) {
      case MessageConst.TYPE_TXT: {
        if (m.getDirection() == MessageConst.DIRECTION_RECEIVE) {
          return TYPE_TEXT_FROM;
        } else if (m.getDirection() == MessageConst.DIRECTION_SEND) {
          return TYPE_TEXT_TO;
        }
        break;
      }
      case MessageConst.TYPE_IMAGE: {
        if (m.getDirection() == MessageConst.DIRECTION_RECEIVE) {
          return TYPE_IMAGE_FROM;
        } else if (m.getDirection() == MessageConst.DIRECTION_SEND) {
          return TYPE_IMAGE_TO;
        }
        break;
      }
      case MessageConst.TYPE_INFO: {
        return TYPE_INFO;
      }
      case MessageConst.TYPE_TIMESTAMP: {
        return TYPE_TIMESTAMP;
      }
    }

    return TYPE_INVALID;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  private View getTextFromView(int position, View convertView, ViewGroup parent) {
    FriendMessage message = getItem(position);

    View textView = getTextView(R.layout.item_friend_chat_text_from, convertView, parent, message);
    TextItemViewHolder holder = (TextItemViewHolder) textView.getTag();
    holder.mAivPortrait.setUrl(mTargetPortraitUrl);
    holder.mAivPortrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ProfileFragment.start(v.getContext(), mTargetViewId, "");
      }
    });

    MessageUtil.setText(holder.mTvText, message);
    return textView;
  }

  private View getTextToView(int position, View convertView, ViewGroup parent) {
    FriendMessage message = getItem(position);

    View textView = getTextView(R.layout.item_friend_chat_text_to, convertView, parent, message);
    TextItemViewHolder holder = (TextItemViewHolder) textView.getTag();
    holder.mAivPortrait.setUrl(mMyPortraitUrl);
    holder.mAivPortrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ProfileFragment.start(v.getContext());
      }
    });

    holder.mTvText.setText(message.getContent());
    return textView;
  }

  private View getTextView(int layout, View convertView, ViewGroup parent, FriendMessage message) {
    TextItemViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
      holder = new TextItemViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (TextItemViewHolder) convertView.getTag();
    }

    holder.mPbLoading.setVisibility(message.getStatus() == MessageConst.STATUS_IN_PROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.getStatus() == MessageConst.STATUS_FAILED ? View.VISIBLE : View.GONE);

    return convertView;
  }

  private View getImageFromView(int position, View convertView, ViewGroup parent) {
    View imageView = getImageView(R.layout.item_friend_chat_image_from, position, convertView, parent);
    ImageItemViewHolder holder = (ImageItemViewHolder) imageView.getTag();
    holder.mAivPortrait.setUrl(mTargetPortraitUrl);
    holder.mAivPortrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ProfileFragment.start(v.getContext(), mTargetViewId, "");
      }
    });
    return imageView;
  }

  private View getImageToView(int position, View convertView, ViewGroup parent) {
    View imageView = getImageView(R.layout.item_friend_chat_image_to, position, convertView, parent);
    ImageItemViewHolder holder = (ImageItemViewHolder) imageView.getTag();
    holder.mAivPortrait.setUrl(mMyPortraitUrl);
    holder.mAivPortrait.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ProfileFragment.start(v.getContext());
      }
    });
    return imageView;
  }

  private View getImageView(int layout, int position, View convertView, final ViewGroup parent) {
    ImageItemViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
      holder = new ImageItemViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ImageItemViewHolder) convertView.getTag();
    }

    final FriendMessage message = getItem(position);

    ImageContent content = U.getGson().fromJson(message.getContent(), ImageContent.class);
    if (content.localThumb != null) {
      holder.mIvImage.setUrl(content.localThumb);
    } else if (content.local != null) {
      holder.mIvImage.setUrl(content.local);
    }
    holder.mPbLoading.setVisibility(message.getStatus() == MessageConst.STATUS_IN_PROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.getStatus() == MessageConst.STATUS_FAILED ? View.VISIBLE : View.GONE);

    holder.mLlBubble.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ImageContent content = U.getGson().fromJson(message.getContent(), ImageContent.class);
        ImageViewerActivity.start(parent.getContext(), content.local, content.remote, content.secret);
      }
    });

    return convertView;
  }

  private View getInfoView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info, parent, false);
    }

    final FriendMessage message = getItem(position);
    EmojiconTextView textView = (EmojiconTextView) convertView.findViewById(R.id.rb_info);
    textView.setText(message.getContent());

    return convertView;
  }

  private View getTimestampView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_timestamp, parent, false);
    }

    FriendMessage message = getItem(position);
    ((RoundedButton) convertView.findViewById(R.id.rb_info)).setText(ChatUtils.timestamp(message.getTimestamp()));

    return convertView;
  }

  private void addTimestampMessages() {

    FriendMessage pre = null;

    List<FriendMessage> toBeAdded = new ArrayList<FriendMessage>();

    for (FriendMessage message : mMessages) {
      if (pre != null && (pre.getType() == MessageConst.TYPE_TXT || pre.getType() == MessageConst.TYPE_IMAGE) &&
          (message.getType() == MessageConst.TYPE_TXT || message.getType() == MessageConst.TYPE_IMAGE)) {
        if (message.getTimestamp() - pre.getTimestamp() > 300000) { // 5 minutes
          FriendMessage m = ChatUtils.infoFriendMsg(message.getChatId(), TimeUtil.getElapsed(message.getTimestamp()));
          m.setType(MessageConst.TYPE_TIMESTAMP);
          m.setTimestamp(message.getTimestamp() - 1);
          toBeAdded.add(m);
        }
      }
      pre = message;
    }

    mMessages.addAll(toBeAdded);
  }


  class TextItemViewHolder {

    @InjectView(R.id.aiv_portrait)
    AsyncImageViewWithRoundCorner mAivPortrait;

    @InjectView(R.id.pb_loading)
    ProgressBar mPbLoading;

    @InjectView(R.id.tv_text)
    TextView mTvText;

    @InjectView(R.id.iv_error)
    ImageView mIvError;

    TextItemViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  class ImageItemViewHolder {

    @InjectView(R.id.aiv_portrait)
    AsyncImageViewWithRoundCorner mAivPortrait;

    @InjectView(R.id.pb_loading)
    ProgressBar mPbLoading;

    @InjectView(R.id.iv_image)
    AsyncImageView mIvImage;

    @InjectView(R.id.iv_error)
    ImageView mIvError;

    @InjectView(R.id.ll_bubble)
    LinearLayout mLlBubble;

    ImageItemViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}
