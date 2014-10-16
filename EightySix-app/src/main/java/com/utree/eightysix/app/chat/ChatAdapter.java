package com.utree.eightysix.app.chat;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.utree.eightysix.R;

import java.util.*;

/**
 * @author simon
 */
public class ChatAdapter extends BaseAdapter {


  private static final int TYPE_COUNT = 3;
  private static final int TYPE_INVALID = 0;
  private static final int TYPE_TEXT_FROM = 1;
  private static final int TYPE_TEXT_TO = 2;

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
    mEMMessages.add(message);
    Collections.sort(mEMMessages, mEMMessageComparator);
    notifyDataSetChanged();
  }

  public void add(List<EMMessage> messages) {
    mEMMessages.addAll(messages);
    Collections.sort(mEMMessages, mEMMessageComparator);
    notifyDataSetChanged();
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
