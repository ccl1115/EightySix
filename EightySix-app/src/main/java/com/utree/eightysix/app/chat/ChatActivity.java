package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.easemob.EMCallBack;
import com.easemob.chat.*;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.chat.event.ChatStatusEvent;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;

/**
* @author simon
*/
@Layout(R.layout.activity_chat)
public class ChatActivity extends BaseActivity {

  @InjectView(R.id.fl_send)
  public FrameLayout mFlSend;

  @InjectView(R.id.et_post_content)
  public EditText mEtPostContent;

  @InjectView(R.id.rb_post)
  public RoundedButton mRbPost;

  @InjectView(R.id.alv_chats)
  public AdvancedListView mAlvChats;

  private ChatAdapter mChatAdapter;

  private String mUsername;

  public static void start(Context context, String username) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra("username", username);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick(R.id.rb_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getSender().txt(mUsername, mEtPostContent.getText().toString());
    mEtPostContent.setText("");
  }

  @OnTextChanged(R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence cs) {
    mRbPost.setEnabled(cs.length() > 0);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mChatAdapter = new ChatAdapter();

    mUsername = getIntent().getStringExtra("username");

    if (mUsername == null) {
      finish();
    }

    EMConversation conversation = EMChatManager.getInstance().getConversation(mUsername);


    mChatAdapter.add(conversation.getAllMessages());
    mAlvChats.setAdapter(mChatAdapter);
    mAlvChats.setSelection(Integer.MAX_VALUE);

    U.getChatBus().register(this);
  }

  @Subscribe
  public void onChatStatusEvent(ChatStatusEvent event) {
    switch (event.getStatus()) {
      case ChatStatusEvent.EVENT_RECEIVE_MSG: {
        EMMessage obj = (EMMessage) event.getObj();
        if (obj.getFrom().equals(mUsername)) {
          mChatAdapter.add(obj);
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
        }
        break;
      }
      case ChatStatusEvent.EVENT_SENT_MSG_SUCCESS:
      case ChatStatusEvent.EVENT_SENT_MSG_ERROR: {
        mChatAdapter.notifyDataSetChanged();
        mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
        break;
      }
      case ChatStatusEvent.EVENT_SENDING_MSG: {
        EMMessage obj = (EMMessage) event.getObj();
        if (obj.getTo().equals(mUsername)) {
          mChatAdapter.add(obj);
        }
        break;
      }
    }
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    U.getChatBus().unregister(this);
  }
}