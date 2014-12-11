package com.utree.eightysix.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;
import com.utree.eightysix.widget.RoundedButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author simon
 */
public class ChatAdapter extends BaseAdapter {


  private static final int TYPE_COUNT = 5;
  private static final int TYPE_INVALID = 0;
  private static final int TYPE_TEXT_FROM = 1;
  private static final int TYPE_TEXT_TO = 2;
  private static final int TYPE_INFO = 3;
  private static final int TYPE_POST = 4;

  private List<Message> mMessages;
  private Comparator<Message> mMessageComparator;

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

  public void add(Message message) {
    if (!contains(message)) {
      mMessages.add(message);
    }
    Collections.sort(mMessages, mMessageComparator);
    notifyDataSetChanged();
  }

  public void add(List<Message> messages) {
    mMessages.addAll(messages);
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
      case TYPE_INFO:
      case TYPE_POST:
        return getInfoView(position, convertView, parent);
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    Message m = getItem(position);
    switch (m.getDirection()) {
      case MessageConst.DIRECTION_RECEIVE: {
        if (m.getType() == MessageConst.TYPE_TXT) {
          return TYPE_TEXT_FROM;
        }
        break;
      }
      case MessageConst.DIRECTION_SEND: {
        if (m.getType() == MessageConst.TYPE_TXT) {
          return TYPE_TEXT_TO;
        }
        break;
      }
    }

    switch (m.getType()) {
      case MessageConst.TYPE_INFO: {
        return TYPE_INFO;
      }
      case MessageConst.TYPE_POST: {
        return TYPE_POST;
      }
    }

    return TYPE_INVALID;
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_COUNT;
  }

  private View getTextFromView(int position, View convertView, ViewGroup parent) {
    ChatItemViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_from, parent, false);
      holder = new ChatItemViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ChatItemViewHolder) convertView.getTag();
    }

    Message message = getItem(position);

    holder.mTvText.setText(message.getContent());
    holder.mPbLoading.setVisibility(message.getStatus() == MessageConst.STATUS_IN_PROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.getStatus() == MessageConst.STATUS_FAILED ? View.VISIBLE : View.GONE);

    return convertView;
  }

  private View getTextToView(int position, View convertView, ViewGroup parent) {
    ChatItemViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_to, parent, false);
      holder = new ChatItemViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ChatItemViewHolder) convertView.getTag();
    }

    Message message = getItem(position);

    holder.mTvText.setText(message.getContent());
    holder.mPbLoading.setVisibility(message.getStatus() == MessageConst.STATUS_IN_PROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.getStatus() == MessageConst.STATUS_FAILED ? View.VISIBLE : View.GONE);

    return convertView;
  }

  private View getInfoView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info, parent, false);
    }

    Message message = getItem(position);
    ((RoundedButton) convertView.findViewById(R.id.rb_info)).setText(message.getContent());

    return convertView;
  }

  class ChatItemViewHolder {

    @InjectView(R.id.pb_loading)
    ProgressBar mPbLoading;

    @InjectView(R.id.tv_text)
    TextView mTvText;

    @InjectView(R.id.iv_error)
    ImageView mIvError;

    ChatItemViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
