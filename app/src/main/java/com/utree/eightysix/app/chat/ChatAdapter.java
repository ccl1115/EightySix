package com.utree.eightysix.app.chat;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.content.ImageContent;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.FontPortraitView;
import com.utree.eightysix.widget.RoundedButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author simon
 */
public class ChatAdapter extends BaseAdapter {


  private static final int TYPE_COUNT = 9;
  private static final int TYPE_INVALID = 0;
  private static final int TYPE_TEXT_FROM = 1;
  private static final int TYPE_TEXT_TO = 2;
  private static final int TYPE_INFO = 3;
  private static final int TYPE_POST = 4;
  private static final int TYPE_COMMENT = 5;
  private static final int TYPE_TIMESTAMP = 6;
  private static final int TYPE_IMAGE_FROM = 7;
  private static final int TYPE_IMAGE_TO = 8;

  private Conversation mConversation;

  private List<Message> mMessages;
  private Comparator<Message> mMessageComparator;

  private String mMyPortrait;
  private int mMyPortraitColor;
  private String mTargetPortrait;
  private int mTargetPortraitColor;

  {
    mMessageComparator = new Comparator<Message>() {
      @Override
      public int compare(Message lhs, Message rhs) {
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

  public ChatAdapter() {
    mMessages = new ArrayList<Message>();
  }

  public ChatAdapter(Conversation conversation) {
    this();
    mMyPortrait = conversation.getMyPortrait();
    mMyPortraitColor = ColorUtil.strToColor(conversation.getMyPortraitColor());
    mTargetPortrait = conversation.getPortrait();
    mTargetPortraitColor = ColorUtil.strToColor(conversation.getPortraitColor());
    mConversation = conversation;
  }
  public ChatAdapter(String myPortrait,
                     int myPortraitColor,
                     String targetPortrait,
                     int targetPortraitColor) {
    this();
    mMyPortrait = myPortrait;
    mMyPortraitColor = myPortraitColor;
    mTargetPortrait = targetPortrait;
    mTargetPortraitColor = targetPortraitColor;
  }

  public void add(Message message) {
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

  public void add(List<Message> messages) {
    mMessages.addAll(messages);
    addTimestampMessages();
    Collections.sort(mMessages, mMessageComparator);
    notifyDataSetChanged();
  }

  private boolean contains(Message message) {
    for (Message m : mMessages) {
      if (m.getId() != null && m.getId().equals(message.getId())) {
        return true;
      }
    }
    return false;
  }

  public void remove(Message message) {
    if (contains(message)) {
      mMessages.remove(message);
    }
    notifyDataSetChanged();
  }

  public Message get(int index) {
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
  public Message getItem(int position) {
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
      case TYPE_POST:
        return getPostView(position, convertView, parent);
      case TYPE_COMMENT:
        return getPostCommentInfoView(position, convertView, parent);
      case TYPE_TIMESTAMP:
        return getTimestampView(position, convertView, parent);
      case TYPE_INVALID:
        return getInvalid(convertView, parent);
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    Message m = getItem(position);

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
      case MessageConst.TYPE_POST: {
        return TYPE_POST;
      }
      case MessageConst.TYPE_COMMENT: {
        return TYPE_COMMENT;
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

  private View getInvalid(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new View(parent.getContext());
    }
    return convertView;
  }

  private View getTextFromView(int position, View convertView, ViewGroup parent) {
    View textView = getTextView(R.layout.item_chat_text_from, position, convertView, parent);
    TextItemViewHolder holder = (TextItemViewHolder) textView.getTag();
    if (mTargetPortrait.equals("\ue800")) {
      holder.mFpvPortrait.setEmotion(' ');
      holder.mFpvPortrait.setBackgroundResource(R.drawable.ic_host_portrait);
    } else {
      holder.mFpvPortrait.setEmotion(mTargetPortrait.charAt(0)
      );
      holder.mFpvPortrait.setEmotionColor(mTargetPortraitColor);
    }
    return textView;
  }

  private View getTextToView(int position, View convertView, ViewGroup parent) {
    View textView = getTextView(R.layout.item_chat_text_to, position, convertView, parent);
    TextItemViewHolder holder = (TextItemViewHolder) textView.getTag();
    if (mMyPortrait.equals("\ue800")) {
      holder.mFpvPortrait.setEmotion(' ');
      holder.mFpvPortrait.setBackgroundResource(R.drawable.ic_host_portrait);
    } else {
      holder.mFpvPortrait.setEmotion(mMyPortrait.charAt(0));
      holder.mFpvPortrait.setEmotionColor(mMyPortraitColor);
    }
    return textView;
  }

  private View getTextView(int layout, int position, View convertView, ViewGroup parent) {
    TextItemViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
      holder = new TextItemViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (TextItemViewHolder) convertView.getTag();
    }

    Message message = getItem(position);

    holder.mTvText.setText(message.getContent());
    holder.mPbLoading.setVisibility(message.getStatus() == MessageConst.STATUS_IN_PROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.getStatus() == MessageConst.STATUS_FAILED ? View.VISIBLE : View.GONE);

    return convertView;
  }

  private View getImageFromView(int position, View convertView, ViewGroup parent) {
    View imageView = getImageView(R.layout.item_chat_image_from, position, convertView, parent);
    ImageItemViewHolder holder = (ImageItemViewHolder) imageView.getTag();
    if (mTargetPortrait.equals("\ue800")) {
      holder.mFpvPortrait.setEmotion(' ');
      holder.mFpvPortrait.setBackgroundResource(R.drawable.ic_host_portrait);
    } else {
      holder.mFpvPortrait.setEmotion(mTargetPortrait.charAt(0));
      holder.mFpvPortrait.setEmotionColor(mTargetPortraitColor);
    }
    return imageView;
  }

  private View getImageToView(int position, View convertView, ViewGroup parent) {
    View imageView = getImageView(R.layout.item_chat_image_to, position, convertView, parent);
    ImageItemViewHolder holder = (ImageItemViewHolder) imageView.getTag();
    if (mMyPortrait.equals("\ue800")) {
      holder.mFpvPortrait.setEmotion(' ');
      holder.mFpvPortrait.setBackgroundResource(R.drawable.ic_host_portrait);
    } else {
      holder.mFpvPortrait.setEmotion(mMyPortrait.charAt(0));
      holder.mFpvPortrait.setEmotionColor(mMyPortraitColor);
    }
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

    final Message message = getItem(position);

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

    final Message message = getItem(position);
    EmojiconTextView textView = (EmojiconTextView) convertView.findViewById(R.id.rb_info);
    textView.setText(message.getContent());

    return convertView;
  }

  private View getPostCommentInfoView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info, parent, false);
    }

    final Message message = getItem(position);
    EmojiconTextView textView = (EmojiconTextView) convertView.findViewById(R.id.rb_info);
    textView.setBackgroundResource(R.drawable.bg_chat_comment_info);
    textView.setText(message.getContent());
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        PostActivity.start(view.getContext(), message.getPostId());
      }
    });

    return convertView;
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    PostItemViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_post, parent, false);
      holder = new PostItemViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (PostItemViewHolder) convertView.getTag();
    }

    final Message message = getItem(position);

    if (TextUtils.isEmpty(mConversation.getBgUrl())) {
      holder.mAivBg.setUrl(null);
      holder.mFlPost.setBackgroundColor(ColorUtil.strToColor(mConversation.getBgColor()));
    } else {
      holder.mAivBg.setUrl(mConversation.getBgUrl());
      holder.mFlPost.setBackgroundColor(Color.TRANSPARENT);
    }
    holder.mTvContent.setText(mConversation.getPostContent());
    holder.mFlPost.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PostActivity.start(v.getContext(), message.getPostId());
      }
    });

    return convertView;
  }

  private View getTimestampView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_timestamp, parent, false);
    }

    Message message = getItem(position);
    ((RoundedButton) convertView.findViewById(R.id.rb_info)).setText(ChatUtils.timestamp(message.getTimestamp()));

    return convertView;
  }

  private void addTimestampMessages() {

    Message pre = null;

    List<Message> toBeAdded = new ArrayList<Message>();

    for (Message message : mMessages) {
      if (pre != null && (pre.getType() == MessageConst.TYPE_TXT || pre.getType() == MessageConst.TYPE_IMAGE) &&
          (message.getType() == MessageConst.TYPE_TXT || message.getType() == MessageConst.TYPE_IMAGE)) {
        if (message.getTimestamp() - pre.getTimestamp() > 300000) { // 5 minutes
          Message m = ChatUtils.infoMsg(message.getChatId(), TimeUtil.getElapsed(message.getTimestamp()));
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

    @InjectView(R.id.fpv_portrait)
    FontPortraitView mFpvPortrait;

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

    @InjectView(R.id.fpv_portrait)
    FontPortraitView mFpvPortrait;

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

  class PostItemViewHolder {

    @InjectView(R.id.fl_post)
    FrameLayout mFlPost;

    @InjectView(R.id.tv_content)
    TextView mTvContent;

    @InjectView(R.id.aiv_post_bg)
    AsyncImageView mAivBg;

    PostItemViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
