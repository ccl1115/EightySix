package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.ConversationDao;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.ImageActionButton;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TopBar;

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

  @InjectView(R.id.iv_camera)
  public ImageView mIvCamera;

  @InjectView(R.id.iv_emotion)
  public ImageView mIvEmotion;

  private ChatAdapter mChatAdapter;

  private Post mPost;
  private Comment mComment;
  private Conversation mConversation;

  public static void start(Context context, Post post, Comment comment) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("comment", comment);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick(R.id.rb_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getSender().txt(mPost.chatId, mPost.id, mComment.id, mEtPostContent.getText().toString());
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
  public void onChatEvent(ChatEvent event) {
    switch (event.getStatus()) {
      case ChatEvent.EVENT_RECEIVE_MSG: {
        mChatAdapter.add((Message) event.getObj());
        mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
        break;
      }
      case ChatEvent.EVENT_SENT_MSG_SUCCESS:
      case ChatEvent.EVENT_SENT_MSG_ERROR: {
        mChatAdapter.notifyDataSetChanged();
        mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
        break;
      }
      case ChatEvent.EVENT_SENDING_MSG: {
        mChatAdapter.add((Message) event.getObj());
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
  public boolean showActionOverflow() {
    return true;
  }

  @Override
  public void onActionOverflowClicked() {
    showMoreDialog();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mIvCamera.setVisibility(View.VISIBLE);
    mIvEmotion.setVisibility(View.VISIBLE);

    mChatAdapter = new ChatAdapter();

    mPost = getIntent().getParcelableExtra("post");
    mComment = getIntent().getParcelableExtra("comment");

    mConversation = DaoUtils.getConversationDao()
        .queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(mPost.chatId))
        .unique();

    setTopTitle(mPost.viewType == 3 ? "认识的人" : "陌生人");
    setTopSubTitle("来自" + mPost.shortName);

    if (mPost.chatId == null) {
      finish();
    }

    mChatAdapter.add(ChatUtils.MessageUtil.getConversation(mPost.chatId, 0));

    mAlvChats.setAdapter(mChatAdapter);
    mAlvChats.setSelection(Integer.MAX_VALUE);

    U.getChatBus().register(this);

    mRefreshView.setColorSchemeResources(R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed);

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

    if (mComment == null) {
      addPostSummaryInfo();
    } else {
      addCommentSummaryInfo();
    }

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return null;
      }

      @Override
      public Drawable getIcon(int position) {
        return mConversation.getFavorite() ?
            getResources().getDrawable(R.drawable.ic_favorite_selected) :
            getResources().getDrawable(R.drawable.ic_favorite_outline);
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        ((ImageActionButton) getTopBar().getActionView(0))
            .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline));
        if (mConversation.getFavorite()) {
          U.request("chat_fav_del", new OnResponse2<Response>() {
            @Override
            public void onResponseError(Throwable e) {
              ((ImageActionButton) getTopBar().getActionView(0))
                  .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_selected));
            }

            @Override
            public void onResponse(Response response) {
              if (RESTRequester.responseOk(response)) {
                mConversation.setFavorite(false);
                DaoUtils.getConversationDao().update(mConversation);
              } else {
                ((ImageActionButton) getTopBar().getActionView(0))
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_selected));
              }
            }
          }, Response.class, mPost.chatId);
        } else {
          ((ImageActionButton) getTopBar().getActionView(0))
              .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_selected));
          U.request("chat_fav_add", new OnResponse2<Response>() {
            @Override
            public void onResponseError(Throwable e) {
              ((ImageActionButton) getTopBar().getActionView(0))
                  .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline));
            }

            @Override
            public void onResponse(Response response) {
              if (RESTRequester.responseOk(response)) {
                mConversation.setFavorite(true);
                DaoUtils.getConversationDao().update(mConversation);
              } else {
                ((ImageActionButton) getTopBar().getActionView(0))
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline));
              }
            }
          }, Response.class, mPost.chatId);

        }
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return null;
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    U.getChatBus().unregister(this);
  }

  private void showMoreDialog() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.chat_actions))
        .setItems(new String[]{getString(R.string.report), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
              case 0: {
                showReportConfirmDialog();
                break;
              }
              case 1: {
                showDeleteConfirmDialog();
                break;
              }
            }
          }
        }).show();
  }

  private void showReportConfirmDialog() {
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
                  ChatUtils.ConversationUtil.deleteConversation(mPost.chatId);
                  finish();
                }
                dialogInterface.dismiss();
              }

              @Override
              public void onResponseError(Throwable e) {
                dialogInterface.dismiss();
              }
            }, Response.class, mPost.chatId);
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
  }

  private void showDeleteConfirmDialog() {
    new AlertDialog.Builder(this).setTitle("确认删除该对话？")
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            ChatUtils.ConversationUtil.deleteConversation(mPost.chatId);
            finish();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
  }

  private void addPostSummaryInfo() {
    if (!ChatUtils.MessageUtil.hasPostSummaryMessage(mPost.chatId)) {
      Message message =
          ChatUtils.infoMsg(mPost.chatId,
              "主题：" + (mPost.content.length() > 80 ? mPost.content.substring(0, 76) + "..." : mPost.content));

      message.setType(MessageConst.TYPE_POST);

      DaoUtils.getMessageDao().insertOrReplace(message);
      mChatAdapter.add(message);
    }
  }

  private void addCommentSummaryInfo() {
    if (!ChatUtils.MessageUtil.hasCommentSummrayMessage(mPost.chatId, mComment.id)) {
      Message message =
          ChatUtils.infoMsg(mComment.chatId,
              "评论：" + (mComment.content.length() > 80 ? mComment.content.substring(0, 76) + "..." : mComment.content));

      message.setType(MessageConst.TYPE_COMMENT);

      DaoUtils.getMessageDao().insertOrReplace(message);
      mChatAdapter.add(message);
    }
  }
}