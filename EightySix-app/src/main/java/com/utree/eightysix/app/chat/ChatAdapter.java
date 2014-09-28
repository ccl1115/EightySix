package com.utree.eightysix.app.chat;

/**
 * @author simon
 */
//public class ChatAdapter extends BaseAdapter {
//
//
//  private static final int TYPE_COUNT = 3;
//  private static final int TYPE_INVALID = 0;
//  private static final int TYPE_TEXT_FROM = 1;
//  private static final int TYPE_TEXT_TO = 2;
//
//  private List<EMMessage> mEMMessages;
//
//  public ChatAdapter() {
//    mEMMessages = new ArrayList<EMMessage>();
//  }
//
//  public void add(EMMessage message) {
//    for (int i = 0; i < mEMMessages.size(); i++) {
//      EMMessage m = mEMMessages.get(i);
//      if (message.getMsgTime() > m.getMsgTime()) {
//        mEMMessages.add(i, message);
//        notifyDataSetChanged();
//        break;
//      }
//    }
//  }
//
//  public void add(List<EMMessage> messages) {
//    for (EMMessage m : messages) {
//      add(m);
//    }
//  }
//
//  @Override
//  public int getCount() {
//    return mEMMessages.size();
//  }
//
//  @Override
//  public EMMessage getItem(int position) {
//    return mEMMessages.get(position);
//  }
//
//  @Override
//  public long getItemId(int position) {
//    return position;
//  }
//
//  @Override
//  public int getViewTypeCount() {
//    return TYPE_COUNT;
//  }
//
//  @Override
//  public int getItemViewType(int position) {
//    EMMessage m = getItem(position);
//    switch (m.direct) {
//      case RECEIVE: {
//        if (m.getType() == EMMessage.Type.TXT) {
//          return TYPE_TEXT_FROM;
//        }
//        break;
//      }
//      case SEND: {
//        if (m.getType() == EMMessage.Type.TXT) {
//          return TYPE_TEXT_TO;
//        }
//        break;
//      }
//    }
//    return TYPE_INVALID;
//  }
//
//  @Override
//  public View getView(int position, View convertView, ViewGroup parent) {
//    switch (getItemViewType(position)) {
//      case TYPE_TEXT_FROM:
//        return getTextFromView(position, convertView, parent);
//      case TYPE_TEXT_TO:
//        return getTextToView(position, convertView, parent);
//    }
//    return null;
//  }
//
//  private View getTextFromView(int position, View convertView, ViewGroup parent) {
//    if (convertView == null) {
//      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_from, parent, false);
//    }
//
//    EMMessage message = getItem(position);
//
//    String text = ((TextMessageBody) message.getBody()).getMessage();
//
//    ((TextView) convertView.findViewById(R.id.tv_text)).setText(text);
//
//    return convertView;
//  }
//
//  private View getTextToView(int position, View convertView, ViewGroup parent) {
//    if (convertView == null) {
//      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_to, parent, false);
//    }
//
//    EMMessage message = getItem(position);
//
//    String text = ((TextMessageBody) message.getBody()).getMessage();
//
//    ((TextView) convertView.findViewById(R.id.tv_text)).setText(text);
//
//    return convertView;
//  }
//}
