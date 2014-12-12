/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
@Layout(R.layout.activity_conversation)
@TopTitle(R.string.chat_anonymous)
public class ConversationActivity extends BaseActivity {

  @InjectView(R.id.alv_conversation)
  public AdvancedListView mAlvConversation;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

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
    post.shortName = conversation.getChatSource();
    post.content = conversation.getPostContent();
    if (conversation.getCommentId() != null) {
      Comment comment = new Comment();
      comment.id = conversation.getCommentId();
      comment.content = conversation.getCommentContent();
      ChatActivity.start(this, post, comment);
    } else {
      ChatActivity.start(this, post, null);
    }
  }

  @OnItemLongClick(R.id.alv_conversation)
  public boolean onAlvConversationItemLongClicked(int position) {
    Conversation conversation = mConversationAdapter.getItem(position);
    showMoreDialog(conversation.getChatId());
    return true;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mConversationAdapter = new ConversationAdapter();
    mAlvConversation.setAdapter(mConversationAdapter);

    mRstvEmpty.setDrawable(R.drawable.scene_1);
    mRstvEmpty.setText("你还没有聊天");
    mRstvEmpty.setSubText("快去帖子和评论中发起匿名聊天");

    mAlvConversation.setEmptyView(mRstvEmpty);

  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void showMoreDialog(final String chatId) {
    new AlertDialog.Builder(this).setTitle(getString(R.string.chat_actions))
        .setItems(new String[]{getString(R.string.report), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
              case 0: {
                showReportConfirmDialog(chatId);
                break;
              }
              case 1: {
                showDeleteConfirmDialog(chatId);
                break;
              }
            }
          }
        }).show();
  }

  private void showReportConfirmDialog(final String chatId) {
    new AlertDialog.Builder(this).setTitle("确认举报该对话？")
        .setMessage("举报的同时，该对话会被系统删除")
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialogInterface, int i) {
            U.request("chat_report", new OnResponse2<Response>() {

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  showToast("举报成功");
                  ChatUtils.ConversationUtil.deleteConversation(chatId);
                  mConversationAdapter.removeByChatId(chatId);
                }
                dialogInterface.dismiss();
              }

              @Override
              public void onResponseError(Throwable e) {
                dialogInterface.dismiss();
              }
            }, Response.class, chatId);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
  }

  private void showDeleteConfirmDialog(final String chatId) {
    new AlertDialog.Builder(this).setTitle("确认删除该对话？")
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            ChatUtils.ConversationUtil.deleteConversation(chatId);
            mConversationAdapter.removeByChatId(chatId);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
  }
}