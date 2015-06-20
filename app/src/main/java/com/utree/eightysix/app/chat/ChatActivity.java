package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import butterknife.*;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.publish.EmojiFragment;
import com.utree.eightysix.app.publish.EmojiViewPager;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.ConversationDao;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageDao;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.ThemedDialog;

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
  public EmojiViewPager mFlEmotion;

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

  @InjectView(R.id.ll_notice)
  public LinearLayout mLlNotice;

  @InjectView(R.id.tv_text)
  public TextView mTvText;

  private ChatAdapter mChatAdapter;

  private String mChatId;

  private Conversation mConversation;

  private CameraUtil mCameraUtil;

  private boolean mIsOpened;

  private Instrumentation mInstrumentation = new Instrumentation();

  static void start(Context context, String chatId) {
    context.startActivity(getIntent(context, chatId));
  }

  static Intent getIntent(Context context, String chatId) {

    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra("chatId", chatId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return intent;
  }

  public static String getCurrentChatId() {
    return sCurrentChatId;
  }

  @OnClick(R.id.iv_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getSender()
        .txt(mChatId, mConversation.getPostId(), mConversation.getCommentId(), mEtPostContent.getText().toString());
    mEtPostContent.setText("");
  }

  @OnClick(R.id.iv_camera)
  public void onIvCameraClicked() {
    if (mRlActions.getVisibility() == View.VISIBLE) {
      mRlActions.setVisibility(View.GONE);
      mIvCamera.setSelected(false);
    } else {
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
  }

  @OnClick(R.id.iv_emotion)
  public void onIvEmotionClicked() {
    if (mFlEmotion.getVisibility() == View.VISIBLE) {
      mFlEmotion.setVisibility(View.GONE);
      mIvEmotion.setSelected(false);
    } else {
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

  @OnItemClick(R.id.alv_chats)
  public void onAlvChatsItemClicked(int position) {
    Message m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getStatus() == MessageConst.STATUS_FAILED) {
        resendConfirm(m);
      }
    }
  }

  @OnItemLongClick(R.id.alv_chats)
  public boolean onAlvChatsItemLongClicked(int position) {
    Message m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getType() == MessageConst.TYPE_TXT) {
        showMessageDialog(m);
      }
    }
    return true;
  }

  @Subscribe
  public void onChatEvent(ChatEvent event) {
    if (event.getObj() instanceof Message) {
      if (!mChatId.equals(((Message) event.getObj()).getChatId())) {
        return;
      }
      switch (event.getStatus()) {
        case ChatEvent.EVENT_RECEIVE_MSG: {
          mChatAdapter.add((Message) event.getObj());
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          mLlNotice.setVisibility(View.GONE);
          break;
        }
        case ChatEvent.EVENT_WARNING_MSG_RECEIVE: {
          mTvText.setText(((Message) event.getObj()).getContent());
          mLlNotice.setVisibility(View.VISIBLE);
          break;
        }
        case ChatEvent.EVENT_UPDATE_MSG: {
          mChatAdapter.notifyDataSetChanged();
          break;
        }
        case ChatEvent.EVENT_SENT_MSG_SUCCESS: {
          Conversation conversation = ConversationUtil.setLastMessage((Message) event.getObj());
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE, conversation));
          mChatAdapter.notifyDataSetChanged();
          break;
        }
        case ChatEvent.EVENT_SENT_MSG_ERROR: {
          mChatAdapter.notifyDataSetChanged();
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          break;
        }
        case ChatEvent.EVENT_SENDING_MSG: {
          mChatAdapter.add((Message) event.getObj());
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          mLlNotice.setVisibility(View.GONE);
          break;
        }
        case ChatEvent.EVENT_MSG_REMOVE: {
          Message message = (Message) event.getObj();
          mChatAdapter.remove(message);
          if (message.getType() == MessageConst.TYPE_TXT) {
            mEtPostContent.setText(message.getContent());
            mEtPostContent.setSelection(message.getContent().length());
          }
          DaoUtils.getMessageDao().delete(message);
        }
      }
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    hideSoftKeyboard(mEtPostContent);
    finish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_action_overflow));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showMoreDialog();
      }
    });

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      private File mFile;
      private String mLastHash;

      @Override
      public void onImageReturn(String path) {
        M.getRegisterHelper().register(this);
        mFile = new File(path);
        ImageUtils.compress(mFile, 600, 600, 50);
      }

      @Subscribe
      public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
        if (event.getHash().equals(mLastHash) && event.getWidth() == U.dp2px(48) && event.getHeight() == U.dp2px(48)) {
          ChatAccount.inst().getSender().photo(mChatId, mConversation.getPostId(), mConversation.getCommentId(), event.getFile());
          M.getRegisterHelper().unregister(this);
        }
      }

      @Subscribe
      public void onCompressEvent(ImageUtils.CompressEvent event) {
        if (mFile == event.getFile()) {
          mLastHash = IOUtils.fileHash(event.getFile());
          ImageUtils.asyncLoadThumbnail(event.getFile(), mLastHash);
        }
      }
    });

    mCameraUtil.setFixedRatioWhenCrop(false);

    mIvCamera.setVisibility(View.VISIBLE);
    mIvEmotion.setVisibility(View.VISIBLE);
    mIvPost.setEnabled(false);
    mEtPostContent.setHint("");

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
            mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          } else if (mIsOpened) {
            mIsOpened = false;
          }
        }
      });
    }
    //endregion


    mChatId = getIntent().getStringExtra("chatId");

    mConversation = DaoUtils.getConversationDao()
        .queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(mChatId))
        .unique();

    setTopTitle("悄悄话");

    if (mChatId == null) {
      finish();
    }

    mChatAdapter = new ChatAdapter(mConversation);

    mAlvChats.setAdapter(mChatAdapter);

    mChatAdapter.add(MessageUtil.getMessages(mChatId, 0));
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
          List<Message> conversation = MessageUtil.getMessages(mChatId, page);
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
      public void onDrag(int value) {

      }

      @Override
      public void onCancel() {

      }
    });

    if (mConversation.getCommentId() == null || "0".equals(mConversation.getCommentId())) {
      addPostSummaryInfo();
    } else {
      addCommentSummaryInfo();
    }

    addBannedInfoMsg();

    addWarningMsg();

    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fl_emotion, EmojiFragment.newInstance())
        .commitAllowingStateLoss();

    mFlEmotion.setFragmentManager(getSupportFragmentManager());
  }

  public void requestFavAdd() {
    U.request("chat_fav_add", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          mConversation.setFavorite(true);
          DaoUtils.getConversationDao().update(mConversation);
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE, mConversation));
          showToast("收藏成功");
        }
      }
    }, Response.class, mChatId, mConversation.getPostId(), mConversation.getCommentId());
  }

  private void requestFavDel() {
    if (mConversation.getFavorite() != null && mConversation.getFavorite()) {
      U.request("chat_fav_del", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {
        }

        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            mConversation.setFavorite(false);
            DaoUtils.getConversationDao().update(mConversation);
            U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
          }
        }
      }, Response.class, mChatId, mConversation.getPostId(), mConversation.getCommentId());
    }
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
        mConversation = MessageUtil.setRead(mChatId);
        publishProgress(1);

        mUnreadConversationCount = ConversationUtil.getUnreadConversationCount();
        publishProgress(2);
        return null;
      }

      @Override
      protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
          case 1:
            U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE, mConversation));
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
    } else if (mRlActions.getVisibility() == View.VISIBLE) {
      mRlActions.setVisibility(View.GONE);
      mIvEmotion.setSelected(false);
      mIvCamera.setSelected(false);
    } else {
      super.onBackPressed();
    }
  }

  private void showMoreDialog() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.chat_actions))
        .setItems(
            new String[]{
                (mConversation.getFavorite() != null && mConversation.getFavorite()) ?
                    getString(R.string.unfavorite) : getString(R.string.favorite),
                getString(R.string.report),
                getString(R.string.delete),
                (mConversation.getBanned() != null && mConversation.getBanned()) ?
                    getString(R.string.shielded) : getString(R.string.shield)},
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                  case 0: {
                    if (mConversation.getFavorite() != null && mConversation.getFavorite()) {
                      requestFavDel();
                    } else {
                      requestFavAdd();
                    }
                    break;
                  }
                  case 1: {
                    showReportConfirmDialog();
                    break;
                  }
                  case 2: {
                    showDeleteConfirmDialog();
                    break;
                  }
                  case 3: {
                    if (mConversation.getBanned() == null || !mConversation.getBanned()) {
                      showShieldConfirmDialog();
                    }
                    break;
                  }
                }
              }
            }).show();
  }

  private void showReportConfirmDialog() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.confirm_report_chat))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialogInterface, int i) {
            U.request("chat_report", new OnResponse2<Response>() {

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  showToast(getString(R.string.report_success));
                }
              }

              @Override
              public void onResponseError(Throwable e) {
                dialogInterface.dismiss();
              }
            }, Response.class, mChatId);

            dialogInterface.dismiss();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        })
        .show();
  }

  private void showDeleteConfirmDialog() {
    new AlertDialog.Builder(this).setTitle(getString(R.string.confirm_delete_chat))
        .setMessage(getString(R.string.delete_chat_info))
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            requestFavDel();
            ConversationUtil.deleteConversation(mChatId);
            U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_REMOVE, mChatId));
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

  private void showShieldConfirmDialog() {
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
                mConversation.setBanned(true);
                DaoUtils.getConversationDao().update(mConversation);
                U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
                addBannedInfoMsg();
              }
            }, Response.class, mChatId);

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

  private void showMessageDialog(final Message m) {
    new AlertDialog.Builder(this).setTitle("操作")
        .setItems(new String[]{getString(R.string.copy_to_clipboard)}, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                  ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                  clipboardManager.setPrimaryClip(
                      new ClipData("chat_text", new String[]{"text/plain"}, new ClipData.Item(m.getContent())));
                  showToast(R.string.clipboard_copied);
                }
                break;
            }
          }
        })
        .show();
  }

  private void addPostSummaryInfo() {
    Message message = MessageUtil.addPostSummaryInfo(mChatId,
        System.currentTimeMillis(),
        mConversation.getPostId(),
        mConversation.getPostContent());
    mChatAdapter.add(message);
  }

  private void addCommentSummaryInfo() {
    Message message = MessageUtil.addCommentSummaryInfo(mChatId,
        System.currentTimeMillis(),
        mConversation.getPostId(),
        mConversation.getPostContent(),
        mConversation.getCommentId(),
        mConversation.getCommentContent());
    mChatAdapter.add(message);
  }

  @Override
  public void onEmojiconBackspaceClicked(View view) {

  }

  @Override
  public void onEmojiconClicked(Emojicon emojicon) {
    if ("\u274c".equals(emojicon.getEmoji())) {
      (new Thread() {
        @Override
        public void run() {
          mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
        }
      }).start();
    } else {
      String text = mEtPostContent.getText().toString();
      String before = text.substring(0, mEtPostContent.getSelectionStart());
      String after = text.substring(mEtPostContent.getSelectionEnd());

      mEtPostContent.setText(before + emojicon.getEmoji() + after);
      mEtPostContent.setSelection(before.length() + emojicon.getEmoji().length());
    }
  }


  private void resendConfirm(final Message message) {
    final ThemedDialog dialog = new ThemedDialog(this);
    dialog.setTitle("是否重新发送此消息？");
    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (message.getStatus() == MessageConst.STATUS_FAILED) {
          showToast(R.string.resending);
          ChatAccount.inst().getSender().send(message);
        }
        dialog.dismiss();
      }
    });
    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  private void addBannedInfoMsg() {
    if (mConversation.getBanned() != null && mConversation.getBanned()) {
      mChatAdapter.add(ChatUtils.infoMsg(mChatId, "你已将对方拉黑，不会再收到对方发来的消息"));
    }
  }

  private void addWarningMsg() {
    long count = DaoUtils.getMessageDao().queryBuilder()
        .where(MessageDao.Properties.ChatId.eq(mChatId),
            MessageDao.Properties.Type.eq(MessageConst.TYPE_TXT),
            MessageDao.Properties.Direction.eq(MessageConst.DIRECTION_SEND))
        .count();

    if (count == 0) {
      Message unique = DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.eq(mChatId),
              MessageDao.Properties.Type.eq(MessageConst.TYPE_WARNING))
          .limit(1)
          .unique();
      if (unique != null) {
        mTvText.setText(unique.getContent());
        mLlNotice.setVisibility(View.VISIBLE);
      }
    }
  }
}
