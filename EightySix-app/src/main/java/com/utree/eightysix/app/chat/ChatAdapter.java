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
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.utree.eightysix.R;
import com.utree.eightysix.widget.RoundedButton;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author simon
 */
public class ChatAdapter extends BaseAdapter {


  private static final int TYPE_COUNT = 4;
  private static final int TYPE_INVALID = 0;
  private static final int TYPE_TEXT_FROM = 1;
  private static final int TYPE_TEXT_TO = 2;
  private static final int TYPE_TIMESTAMP = 3;

  private List<EMMessage> mEMMessages;
  private Comparator<EMMessage> mEMMessageComparator;

  {
    mEMMessageComparator = new Comparator<EMMessage>() {
      @Override
      public int compare(EMMessage lhs, EMMessage rhs) {
        if (lhs.getMsgTime() > rhs.getMsgTime()) {
          return 1;
        } else if (lhs.getMsgTime() < rhs.getMsgTime()) {
          return -1;
        } else {
          return 0;
        }
      }
    };
  }

  public ChatAdapter() {
    mEMMessages = new ArrayList<EMMessage>();
  }

  public void add(EMMessage message) {
    if (contains(message)) {
      return;
    }
    trimCmdMessages();
    mEMMessages.add(message);
    Collections.sort(mEMMessages, mEMMessageComparator);
    addTimestampMessages();
    notifyDataSetChanged();
  }

  public void add(List<EMMessage> messages) {
    trimCmdMessages();
    mEMMessages.addAll(messages);
    Collections.sort(mEMMessages, mEMMessageComparator);
    addTimestampMessages();
    notifyDataSetChanged();
  }

  private boolean contains(EMMessage message) {
    for (EMMessage emMessage : mEMMessages) {
      if (emMessage.getMsgId().equals(message.getMsgId())) {
        return true;
      }
    }
    return false;
  }

  private void addTimestampMessages() {
    List<EMMessage> messages = new ArrayList<EMMessage>();
    EMMessage pre = null;
    for (EMMessage emMessage : mEMMessages) {
      if (pre == null) {
        messages.add(buildTimestampMessage(emMessage.getMsgTime()));
      } else {
        if (pre.getType() != EMMessage.Type.CMD
            || !((CmdMessageBody) pre.getBody()).action.equals("timestamp")) {
          if (emMessage.getMsgTime() - pre.getMsgTime() > 300000) {
            messages.add(buildTimestampMessage(emMessage.getMsgTime()));
          }
        }
      }

      messages.add(emMessage);
      pre = emMessage;
    }
    mEMMessages.clear();
    mEMMessages = messages;
  }

  private void trimCmdMessages() {
    for (Iterator<EMMessage> iterator = mEMMessages.iterator(); iterator.hasNext(); ) {
      EMMessage emMessage = iterator.next();
      if (emMessage.getType() == EMMessage.Type.CMD) {
        iterator.remove();
      }
    }
  }

  private EMMessage buildTimestampMessage(long time) {
    EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.CMD);
    CmdMessageBody body = new CmdMessageBody("timestamp", new String[]{timestamp(time)});
    message.setMsgId(String.valueOf(time));
    message.addBody(body);
    return message;
  }

  private String timestamp(long time) {
    return SimpleDateFormat
        .getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE)
        .format(new Date(time));
  }

  public String getFirstMsgId() {
    for (EMMessage emMessage : mEMMessages) {
      if (emMessage.getType() != EMMessage.Type.CMD) {
        return emMessage.getMsgId();
      }
    }
    return null;
  }

  @Override
  public int getCount() {
    return mEMMessages.size();
  }

  @Override
  public EMMessage getItem(int position) {
    return mEMMessages.get(position);
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
      case TYPE_TIMESTAMP:
        return getTimestampView(position, convertView, parent);
    }
    return null;
  }

  @Override
  public int getItemViewType(int position) {
    EMMessage m = getItem(position);
    switch (m.direct) {
      case RECEIVE: {
        if (m.getType() == EMMessage.Type.TXT) {
          return TYPE_TEXT_FROM;
        } else if (m.getType() == EMMessage.Type.CMD) {
          if (((CmdMessageBody) m.getBody()).action.equals("timestamp")) {
            return TYPE_TIMESTAMP;
          }
        }
        break;
      }
      case SEND: {
        if (m.getType() == EMMessage.Type.TXT) {
          return TYPE_TEXT_TO;
        }
        break;
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

    EMMessage message = getItem(position);

    String text = ((TextMessageBody) message.getBody()).getMessage();

    holder.mTvText.setText(text);
    holder.mPbLoading.setVisibility(message.status == EMMessage.Status.INPROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.status == EMMessage.Status.FAIL ? View.VISIBLE : View.GONE);

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

    EMMessage message = getItem(position);

    String text = ((TextMessageBody) message.getBody()).getMessage();

    holder.mTvText.setText(text);
    holder.mPbLoading.setVisibility(message.status == EMMessage.Status.INPROGRESS ? View.VISIBLE : View.GONE);
    holder.mIvError.setVisibility(message.status == EMMessage.Status.FAIL ? View.VISIBLE : View.GONE);

    return convertView;
  }

  private View getTimestampView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_time, parent, false);
    }

    ((RoundedButton) convertView.findViewById(R.id.rb_time)).setText(((CmdMessageBody) getItem(position).getBody()).params[0]);

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
