/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
@Layout(R.layout.activity_conversation)
@TopTitle(R.string.chat_anonymous)
public class ConversationActivity extends BaseActivity {

  @InjectView(R.id.alv_conversation)
  public AdvancedListView mAlvConversation;

  @InjectView(R.id.content)
  public SwipeRefreshLayout mSwipeRefreshLayout;
  private ConversationAdapter mConversationAdapter;

  public static void start(Context context) {
    Intent intent = new Intent(context, ConversationActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnItemClick(R.id.alv_conversation)
  public void onAlvConversationItemClicked(int position) {
    Conversation conversation = mConversationAdapter.getItem(position);
    Post post = new Post();
    post.chatId = conversation.getChatId();
    post.id = conversation.getPostId();
    ChatActivity.start(this, post, conversation.getCommentId());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConversationAdapter = new ConversationAdapter();
    mAlvConversation.setAdapter(mConversationAdapter);

    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {

      }

      @Override
      public void onDrag() {

      }

      @Override
      public void onCancel() {

      }
    });
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}