package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
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
import com.utree.eightysix.*;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.chat.content.ImageContent;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.publish.EmojiFragment;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.ConversationDao;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.ImageActionButton;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TopBar;
import java.io.File;
import java.util.List;

/**
 * @author simon
 */
@Layout (R.layout.activity_chat)
public class ChatActivity extends BaseActivity implements
    EmojiconsFragment.OnEmojiconBackspaceClickedListener,
    EmojiconGridFragment.OnEmojiconClickedListener {

  private static String sCurrentChatId;

  @InjectView (R.id.fl_send)
  public FrameLayout mFlSend;

  @InjectView (R.id.fl_emotion)
  public FrameLayout mFlEmotion;

  @InjectView (R.id.rl_actions)
  public RelativeLayout mRlActions;

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRefreshView;

  @InjectView (R.id.et_post_content)
  public EmojiconEditText mEtPostContent;

  @InjectView (R.id.iv_post)
  public ImageView mIvPost;

  @InjectView (R.id.alv_chats)
  public AdvancedListView mAlvChats;

  @InjectView (R.id.iv_camera)
  public ImageView mIvCamera;

  @InjectView (R.id.iv_emotion)
  public ImageView mIvEmotion;

  private ChatAdapter mChatAdapter;

  private String mChatId;

  private Conversation mConversation;

  private CameraUtil mCameraUtil;

  private boolean mIsOpened;

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

  public static void setCurrentChatId(String currentChatId) {
    ChatActivity.sCurrentChatId = currentChatId;
  }

  @OnClick (R.id.iv_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getSender()
        .txt(mChatId, mConversation.getPostId(), mConversation.getCommentId(), mEtPostContent.getText().toString());
    mEtPostContent.setText("");
  }

  @OnClick (R.id.iv_camera)
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

  @OnClick (R.id.iv_emotion)
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

  @OnClick (R.id.iv_open_camera)
  public void onIvOpenCameraClicked() {
    mCameraUtil.startCamera();
  }

  @OnClick (R.id.iv_album)
  public void onIvAlbumClicked() {
    mCameraUtil.startAlbum();
  }

  @OnTextChanged (R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence cs) {
    mIvPost.setEnabled(cs.length() > 0);
  }

  @OnItemClick (R.id.alv_chats)
  public void onAlvChatsItemClicked(int position) {
    Message m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getStatus() == MessageConst.STATUS_FAILED) {
        resendConfirm(m);
      } else if (m.getType() == MessageConst.TYPE_IMAGE) {
        ImageContent content = U.getGson().fromJson(m.getContent(), ImageContent.class);
        ImageViewerActivity.start(this, content.local, content.remote, content.secret);
      }
    }
  }

  @OnItemLongClick(R.id.alv_chats)
  public boolean onAlvChatsItemLongClicked(int position) {
    Message m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getType() == MessageConst.TYPE_TXT) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
          ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
          clipboardManager.setPrimaryClip(
              new ClipData("chat_text", new String[] { "text/plain" }, new ClipData.Item(m.getContent())));
          showToast(R.string.clipboard_copied);
          return true;
        }
      }
    }
    return false;
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
          break;
        }
        case ChatEvent.EVENT_UPDATE_MSG: {
          mChatAdapter.notifyDataSetChanged();
          break;
        }
        case ChatEvent.EVENT_SENT_MSG_SUCCESS: {
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
      private File mFile;
      private String mLastHash;

      @Override
      public void onImageReturn(String path) {
        M.getRegisterHelper().register(this);
        mFile = new File(path);
        ImageUtils.compress(mFile, 600, 600);
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

    setTopTitle(mConversation.getRelation());
    setTopSubTitle("来自" + mConversation.getPostSource());

    if (mChatId == null) {
      finish();
    }

    mChatAdapter = new ChatAdapter(mConversation.getMyPortrait(),
        ColorUtil.strToColor(mConversation.getMyPortraitColor()),
        mConversation.getPortrait(),
        ColorUtil.strToColor(mConversation.getPortraitColor()));

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

    if (mConversation.getCommentId() == null) {
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
          }, Response.class, mChatId, mConversation.getPostId(), mConversation.getCommentId());
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
          }, Response.class, mChatId, mConversation.getPostId(), mConversation.getCommentId());

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

    addBannedInfoMsg();

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
        .setItems(new String[]{getString(R.string.report), getString(R.string.delete), getString(R.string.shield)}, new DialogInterface.OnClickListener() {
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
              case 2: {
                showShieldConfirmDialog();
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
            ChatUtils.ConversationUtil.deleteConversation(mChatId);
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

  private void addPostSummaryInfo() {
    Message message = ChatUtils.MessageUtil.addPostSummaryInfo(mChatId,
        System.currentTimeMillis(),
        mConversation.getPostId(),
        mConversation.getPostContent());
    mChatAdapter.add(message);
  }

  private void addCommentSummaryInfo() {
    Message message = ChatUtils.MessageUtil.addCommentSummaryInfo(mChatId,
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
      final int sel = mEtPostContent.getSelectionStart();
      if (sel > 0) {
        mEtPostContent.getText().delete(sel - 2, sel);
//        mEtPostContent.setText(mEtPostContent.getText().delete(sel - 1, 1));
//        mEtPostContent.setSelection(sel - 1);
      }
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
}
