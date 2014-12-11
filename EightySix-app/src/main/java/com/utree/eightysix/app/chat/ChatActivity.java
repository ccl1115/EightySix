package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 * @author simon
 */
@Layout(R.layout.activity_chat)
public class ChatActivity extends BaseActivity {

  @InjectView(R.id.fl_send)
  public FrameLayout mFlSend;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshView;

  @InjectView(R.id.et_post_content)
  public EditText mEtPostContent;

  @InjectView(R.id.rb_post)
  public RoundedButton mRbPost;

  @InjectView(R.id.alv_chats)
  public AdvancedListView mAlvChats;

  private ChatAdapter mChatAdapter;

  private Post mPost;
  private String mCommentId;

  public static void start(Context context, Post post, String commentId) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("commentId", commentId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick(R.id.rb_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getSender().txt(mPost.chatId, mPost.id, mCommentId, mEtPostContent.getText().toString());
    mEtPostContent.setText("");
  }

  @OnTextChanged(R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence cs) {
    mRbPost.setEnabled(cs.length() > 0);
  }

  @OnItemClick(R.id.alv_chats)
  public void onAlvChatsItemClicked(int position) {
    Message m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getStatus() == MessageConst.STATUS_FAILED) {
        showToast(R.string.resending);
        ChatAccount.inst().getSender().send(m);
      }
    }
  }

  @Subscribe
  public void onChatStatusEvent(ChatEvent event) {
    switch (event.getStatus()) {
      case ChatEvent.EVENT_RECEIVE_MSG: {
        Message obj = (Message) event.getObj();
        if (obj.getChatId().equals(mPost.chatId)) {
          mChatAdapter.add(obj);
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
        }
        break;
      }
      case ChatEvent.EVENT_SENT_MSG_SUCCESS:
      case ChatEvent.EVENT_SENT_MSG_ERROR: {
        mChatAdapter.notifyDataSetChanged();
        mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
        break;
      }
      case ChatEvent.EVENT_SENDING_MSG: {
        Message obj = (Message) event.getObj();
        if (obj.getChatId().equals(mPost.chatId)) {
          mChatAdapter.add(obj);
        }
        break;
      }
      case ChatEvent.EVENT_MSG_REMOVE: {
        mChatAdapter.remove((Message) event.getObj());
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mChatAdapter = new ChatAdapter();


    mPost = getIntent().getParcelableExtra("post");
    mCommentId = getIntent().getStringExtra("commentId");

    if (mPost.chatId == null) {
      finish();
    }

    mChatAdapter.add(ChatUtils.MessageUtil.getConversation(mPost.chatId, 0));

    mAlvChats.setAdapter(mChatAdapter);
    mAlvChats.setSelection(Integer.MAX_VALUE);

    U.getChatBus().register(this);

    mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      private int page = 0;
      private boolean has = true;
      @Override
      public void onRefresh() {
        if (has) {
          page++;
          List<Message> conversation = ChatUtils.MessageUtil.getConversation(mPost.chatId, page);
          if (conversation.size() == 0) {
            has = false;
            Message message = ChatUtils.infoMsg(mPost.chatId, "没有更多的消息了");
            message.setTimestamp(mChatAdapter.getItem(0).getTimestamp() - 1);
            mChatAdapter.add(message);
          } else {
            has = true;
            mChatAdapter.add(conversation);
          }
        }
        mRefreshView.setRefreshing(false);
      }

      @Override
      public void onDrag() {

      }

      @Override
      public void onCancel() {

      }
    });

    mRefreshView.setColorSchemeResources(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    U.getChatBus().unregister(this);
  }

}