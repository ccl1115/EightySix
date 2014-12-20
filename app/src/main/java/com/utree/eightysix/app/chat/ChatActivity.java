package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.*;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.publish.EmojiFragment;
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
import com.utree.eightysix.widget.TopBar;

import java.io.File;
import java.util.List;

/**
 * @author simon
 */
@Layout(R.layout.activity_chat)
public class ChatActivity extends BaseActivity implements
    EmojiconsFragment.OnEmojiconBackspaceClickedListener,
    EmojiconGridFragment.OnEmojiconClickedListener {

  private static String sCurrentChatId;

  @InjectView(R.id.fl_send)
  public FrameLayout mFlSend;

  @InjectView(R.id.fl_emotion)
  public FrameLayout mFlEmotion;

  @InjectView(R.id.rl_actions)
  public RelativeLayout mRlActions;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshView;

  @InjectView(R.id.et_post_content)
  public EmojiconEditText mEtPostContent;

  @InjectView(R.id.iv_post)
  public ImageView mIvPost;

  @InjectView(R.id.alv_chats)
  public AdvancedListView mAlvChats;

  @InjectView(R.id.iv_camera)
  public ImageView mIvCamera;

  @InjectView(R.id.iv_emotion)
  public ImageView mIvEmotion;

  private ChatAdapter mChatAdapter;

  private String mChatId;
  private Post mPost;
  private Comment mComment;
  private Conversation mConversation;

  private CameraUtil mCameraUtil;
  private boolean mIsOpened;

  static void start(Context context, String chatId, Post post, Comment comment) {
    context.startActivity(getIntent(context, chatId, post, comment));
  }

  static Intent getIntent(Context context, String chatId, Post post, Comment comment) {

    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra("chatId", chatId);
    intent.putExtra("post", post);
    intent.putExtra("comment", comment);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return intent;
  }

  public static String getCurrentChatId() {
    return sCurrentChatId;
  }

  public static void setCurrentChatId(String currentChatId) {
    ChatActivity.sCurrentChatId = currentChatId;
  }

  @OnClick(R.id.iv_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getSender()
        .txt(mChatId, mPost.id, mComment == null ? null : mComment.id, mEtPostContent.getText().toString());
    mEtPostContent.setText("");
  }

  @OnClick(R.id.iv_camera)
  public void onIvCameraClicked() {
    hideSoftKeyboard(mEtPostContent);
    mIvEmotion.setSelected(false);
    mIvCamera.setSelected(true);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mRlActions.setVisibility(View.VISIBLE);
        mFlEmotion.setVisibility(View.GONE);
      }
    }, 200);
  }

  @OnClick(R.id.iv_emotion)
  public void onIvEmationClicked() {
    hideSoftKeyboard(mEtPostContent);
    mIvCamera.setSelected(false);
    mIvEmotion.setSelected(true);


    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mFlEmotion.setVisibility(View.VISIBLE);
        mRlActions.setVisibility(View.GONE);
      }
    }, 200);
  }

  @OnClick(R.id.iv_open_camera)
  public void onIvOpenCameraClicked() {
    mCameraUtil.startCamera();
  }

  @OnClick(R.id.iv_album)
  public void onIvAlbumClicked() {
    mCameraUtil.startAlbum();
  }

  @OnTextChanged(R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence cs) {
    mIvPost.setEnabled(cs.length() > 0);
  }

  @OnItemLongClick(R.id.alv_chats)
  public boolean onAlvChatsItemLongClicked(int position) {
    Message m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getStatus() == MessageConst.STATUS_FAILED) {
        showToast(R.string.resending);
        ChatAccount.inst().getSender().send(m);
      }
    }
    return true;
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
        mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
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

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        ChatAccount.inst().getSender()
            .photo(mChatId, mPost.id, mComment == null ? null : mComment.id, new File(path));
      }
    });

    mIvCamera.setVisibility(View.VISIBLE);
    mIvEmotion.setVisibility(View.VISIBLE);
    mIvPost.setEnabled(false);

    //region To detect soft keyboard visibility change
    // works after ICM
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      final View activityRootView = findViewById(android.R.id.content);
      activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
          if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.

            if (!mIsOpened) {
              mFlEmotion.setVisibility(View.GONE);
              mRlActions.setVisibility(View.GONE);
              mIvEmotion.setSelected(false);
              mIvCamera.setSelected(false);
            }
            mIsOpened = true;
          } else if (mIsOpened) {
            mIsOpened = false;
          }
        }
      });
    }
    //endregion

    mChatAdapter = new ChatAdapter();

    mPost = getIntent().getParcelableExtra("post");
    mComment = getIntent().getParcelableExtra("comment");
    mChatId = getIntent().getStringExtra("chatId");

    mConversation = DaoUtils.getConversationDao()
        .queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(mChatId))
        .unique();

    setTopTitle(mPost.viewType == 3 ? "认识的人" : "陌生人");
    setTopSubTitle("来自" + mPost.shortName);

    if (mChatId == null) {
      finish();
    }


    mAlvChats.setAdapter(mChatAdapter);

    mChatAdapter.add(ChatUtils.MessageUtil.getConversation(mChatId, 0));
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
          List<Message> conversation = ChatUtils.MessageUtil.getConversation(mChatId, page);
          if (conversation.size() == 0) {
            has = false;
            Message message = ChatUtils.infoMsg(mChatId, getString(R.string.no_more_history));
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
                U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
              } else {
                ((ImageActionButton) getTopBar().getActionView(0))
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_selected));
              }
            }
          }, Response.class, mChatId, mPost.id, mComment == null ? null : mComment.id);
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
                U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
              } else {
                ((ImageActionButton) getTopBar().getActionView(0))
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_outline));
              }
            }
          }, Response.class, mChatId, mPost.id, mComment == null ? null : mComment.id);

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


    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fl_emotion, EmojiFragment.newInstance())
        .commitAllowingStateLoss();
  }

  @Override
  protected void onResume() {
    super.onResume();
    sCurrentChatId = mChatId;

    (new AsyncTask<Void, Integer, Void>() {

      private Long mUnreadConversationCount;
      private Conversation mConversation;

      @Override
      protected Void doInBackground(Void... voids) {
        mConversation = ChatUtils.MessageUtil.setRead(mChatId);
        publishProgress(1);

        mUnreadConversationCount = ChatUtils.ConversationUtil.getUnreadConversationCount();
        publishProgress(2);
        return null;
      }

      @Override
      protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
          case 1:
            U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
            break;
          case 2:
            U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
            break;
        }
      }
    }).execute();
  }

  @Override
  protected void onPause() {
    super.onPause();
    sCurrentChatId = null;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    U.getChatBus().unregister(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBackPressed() {
    if (mFlEmotion.getVisibility() == View.VISIBLE) {
      mFlEmotion.setVisibility(View.GONE);
      mIvEmotion.setSelected(false);
      mIvCamera.setSelected(false);
    } else {
      super.onBackPressed();
    }
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
    new AlertDialog.Builder(this).setTitle(getString(R.string.confirm_report_chat))
        .setMessage(getString(R.string.report_chat_info))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialogInterface, int i) {
            U.request("chat_report", new OnResponse2<Response>() {

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  showToast(getString(R.string.report_success));
                  ChatUtils.ConversationUtil.deleteConversation(mChatId);
                  finish();
                }
                dialogInterface.dismiss();
              }

              @Override
              public void onResponseError(Throwable e) {
                dialogInterface.dismiss();
              }
            }, Response.class, mChatId);
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
    new AlertDialog.Builder(this).setTitle(getString(R.string.confirm_delete_chat))
        .setMessage(getString(R.string.delete_chat_info))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            ChatUtils.ConversationUtil.deleteConversation(mChatId);
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
    Message message = ChatUtils.MessageUtil.addPostSummaryInfo(mChatId, System.currentTimeMillis(), mPost);
    mChatAdapter.add(message);
  }

  private void addCommentSummaryInfo() {
    Message message = ChatUtils.MessageUtil.addCommentSummaryInfo(mChatId,
        System.currentTimeMillis(), mComment);
    mChatAdapter.add(message);
  }

  @Override
  public void onEmojiconBackspaceClicked(View view) {

  }

  @Override
  public void onEmojiconClicked(Emojicon emojicon) {
    String text = mEtPostContent.getText().toString();
    String before = text.substring(0, mEtPostContent.getSelectionStart());
    String after = text.substring(mEtPostContent.getSelectionEnd());

    mEtPostContent.setText(before + emojicon.getEmoji() + after);
    mEtPostContent.setSelection(before.length() + emojicon.getEmoji().length());
  }
}