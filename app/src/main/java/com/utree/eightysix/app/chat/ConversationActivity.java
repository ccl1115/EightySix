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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

import java.util.List;

/**
 */
@Layout(R.layout.activity_conversation)
@TopTitle(R.string.chat_anonymous)
public class ConversationActivity extends BaseActivity {

  private final static int PAGE_SIZE = 20;

  private static boolean sInConversation;

  @InjectView(R.id.alv_conversation)
  public AdvancedListView mAlvConversation;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private ConversationAdapter mConversationAdapter;

  private int mPage = 1;

  public static void start(Context context) {

    context.startActivity(getIntent(context));
  }

  public static Intent getIntent(Context context) {

    Intent intent = new Intent(context, ConversationActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return intent;
  }

  public static boolean isInConversation() {
    return sInConversation;
  }

  public static void setInConversation(boolean inConversation) {
    ConversationActivity.sInConversation = inConversation;
  }

  @OnItemClick(R.id.alv_conversation)
  public void onAlvConversationItemClicked(int position) {
    Conversation conversation = mConversationAdapter.getItem(position);
    Post post = new Post();
    post.id = conversation.getPostId();
    post.shortName = conversation.getPostSource();
    post.content = conversation.getPostContent();
    if (conversation.getCommentId() != null) {
      Comment comment = new Comment();
      comment.id = conversation.getCommentId();
      comment.content = conversation.getCommentContent();
      ChatActivity.start(this, conversation.getChatId());
    } else {
      ChatActivity.start(this, conversation.getChatId());
    }
  }

  @OnItemLongClick(R.id.alv_conversation)
  public boolean onAlvConversationItemLongClicked(int position) {
    Conversation conversation = mConversationAdapter.getItem(position);
    showMoreDialog(conversation);
    return true;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    List<Conversation> conversations = ConversationUtil.getConversations(0, PAGE_SIZE);
    mConversationAdapter = new ConversationAdapter(conversations);
    mAlvConversation.setAdapter(mConversationAdapter);

    mRstvEmpty.setDrawable(R.drawable.scene_1);
    mRstvEmpty.setText("你还没有聊天");
    mRstvEmpty.setSubText("快去帖子和评论中发起匿名聊天");

    mAlvConversation.setEmptyView(mRstvEmpty);

    mAlvConversation.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(ConversationActivity.this).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPage < ConversationUtil.getPage(PAGE_SIZE);
      }

      @Override
      public boolean onLoadMoreStart() {
        getHandler().postDelayed(new Runnable() {
          @Override
          public void run() {
            List<Conversation> conversations = ConversationUtil.getConversations(mPage, PAGE_SIZE);
            mConversationAdapter.add(conversations);
            mAlvConversation.stopLoadMore();
            mPage += 1;
          }
        }, 500);
        return true;
      }
    });

    U.getChatBus().register(mConversationAdapter);
  }

  @Override
  protected void onResume() {
    super.onResume();

    ChatUtils.NotifyUtil.clear();
  }

  @Override
  public boolean showActionOverflow() {
    return true;
  }

  @Override
  public void onActionOverflowClicked() {
    showActionDialog();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    U.getChatBus().unregister(mConversationAdapter);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void showMoreDialog(final Conversation conversation) {
    new AlertDialog.Builder(this).setTitle(getString(R.string.chat_actions))
        .setItems(
            new String[]{getString(R.string.report), getString(R.string.delete),
                (conversation.getBanned() != null && conversation.getBanned()) ? getString(R.string.shielded) : getString(R.string.shield)},
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                  case 0: {
                    showReportConfirmDialog(conversation);
                    break;
                  }
                  case 1: {
                    showDeleteConfirmDialog(conversation);
                    break;
                  }
                  case 2: {
                    if (conversation.getBanned() == null || !conversation.getBanned()) {
                      showShieldConfirmDialog(conversation);
                    }
                  }
                }
              }
            }).show();
  }

  private void showReportConfirmDialog(final Conversation conversation) {
    new AlertDialog.Builder(this).setTitle(R.string.confirm_report_chat)
        .setMessage(R.string.report_chat_info)
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialogInterface, int i) {
            U.request("chat_report", new OnResponse2<Response>() {

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  showToast(R.string.report_success);
                  ConversationUtil.deleteConversation(conversation);
                  mConversationAdapter.remove(conversation);
                }
                dialogInterface.dismiss();
              }

              @Override
              public void onResponseError(Throwable e) {
                dialogInterface.dismiss();
              }
            }, Response.class, conversation.getChatId());
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
  }

  private void showDeleteConfirmDialog(final Conversation conversation) {
    new AlertDialog.Builder(this).setTitle(R.string.confirm_delete_chat)
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {

            if (conversation.getFavorite() != null && conversation.getFavorite()) {
              U.request("chat_fav_del", new OnResponse2<Response>() {
                @Override
                public void onResponseError(Throwable e) {

                }

                @Override
                public void onResponse(Response response) {

                }
              }, Response.class, conversation.getChatId(), conversation.getPostId(), conversation.getCommentId());
            }
            ConversationUtil.deleteConversation(conversation);
            mConversationAdapter.remove(conversation);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
  }

  private void showShieldConfirmDialog(final Conversation conversation) {
    new AlertDialog.Builder(this).setTitle(getString(R.string.confirm_shield_chat))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            U.request("chat_shield", new OnResponse2<Response>() {
              @Override
              public void onResponseError(Throwable e) {
              }

              @Override
              public void onResponse(Response response) {
                showToast(getString(R.string.shield_succeed));
                conversation.setBanned(true);
                DaoUtils.getConversationDao().update(conversation);
                U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
              }
            }, Response.class, conversation.getChatId());

            dialog.dismiss();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .show();
  }

  private void showActionDialog() {
    new AlertDialog.Builder(this).setItems(
        new String[]{getString(R.string.clear_unread), getString(R.string.chat_delete_all)},
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
              case 0:
                showClearUnreadConfirmDialog();
                break;
              case 1:
                showDeleteAllConfirmDialog();
                break;
            }
          }
        }
    ).show();
  }

  private void showClearUnreadConfirmDialog() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.mark_all_message_read))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            MessageUtil.setAllRead();
          }
        })
        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {

          }
        });
  }


  private void showDeleteAllConfirmDialog() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.delete_all_unfav_conversation))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            ConversationUtil.deleteAllConversation();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {

          }
        }).show();
  }

}
