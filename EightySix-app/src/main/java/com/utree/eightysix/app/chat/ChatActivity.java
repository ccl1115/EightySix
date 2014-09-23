package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import butterknife.InjectView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.chat.event.ChatStatusEvent;
import com.utree.eightysix.widget.AdvancedListView;

/**
 * @author simon
 */
@Layout(R.layout.activity_chat)
public class ChatActivity extends BaseActivity {

  @InjectView(R.id.fl_send)
  public FrameLayout mFlSend;

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
  }

  @Subscribe
  public void onChatStatusEvent(ChatStatusEvent event) {

  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }
}