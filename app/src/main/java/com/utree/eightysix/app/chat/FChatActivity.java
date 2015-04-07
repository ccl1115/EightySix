/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

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
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.app.publish.EmojiFragment;
import com.utree.eightysix.app.publish.EmojiViewPager;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.dao.FriendConversationDao;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.ThemedDialog;

import java.io.File;
import java.util.List;

/**
 */
@Layout(R.layout.activity_fchat)
public class FChatActivity extends BaseActivity implements
  EmojiconsFragment.OnEmojiconBackspaceClickedListener,
  EmojiconGridFragment.OnEmojiconClickedListener {

  public static void start(Context context, String chatId) {
    Intent intent = new Intent(context, FChatActivity.class);
    intent.putExtra("chatId", chatId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  private static String sCurrentFChatId;

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

  private FChatAdapter mChatAdapter;

  private String mChatId;

  private FriendConversation mConversation;

  private CameraUtil mCameraUtil;

  private boolean mIsOpened;

  private Instrumentation mInstrumentation = new Instrumentation();

  @OnClick(R.id.iv_post)
  public void onRbPostClicked() {
    ChatAccount.inst().getFriendSender().txt(mChatId, mEtPostContent.getText().toString());
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
    FriendMessage m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getStatus() == MessageConst.STATUS_FAILED) {
        resendConfirm(m);
      }
    }
  }

  @OnItemLongClick(R.id.alv_chats)
  public boolean onAlvChatsItemLongClicked(int position) {
    FriendMessage m = mChatAdapter.getItem(position);
    if (m != null) {
      if (m.getType() == MessageConst.TYPE_TXT) {
        showMessageDialog(m);
      }
    }
    return true;
  }

  @Subscribe
  public void onFriendChatEvent(FriendChatEvent event) {
    if (event.getObj() instanceof FriendMessage) {
      if (!mChatId.equals(((FriendMessage) event.getObj()).getChatId())) {
        return;
      }
      switch (event.getStatus()) {
        case FriendChatEvent.EVENT_RECEIVE_MSG: {
          mChatAdapter.add((FriendMessage) event.getObj());
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          break;
        }
        case FriendChatEvent.EVENT_UPDATE_MSG: {
          mChatAdapter.notifyDataSetChanged();
          break;
        }
        case FriendChatEvent.EVENT_SENT_MSG_SUCCESS: {
          FriendConversation conversation = FConversationUtil.setLastMessage((FriendMessage) event.getObj());
          U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE, conversation));
          mChatAdapter.notifyDataSetChanged();
          break;
        }
        case FriendChatEvent.EVENT_SENT_MSG_ERROR: {
          mChatAdapter.notifyDataSetChanged();
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          break;
        }
        case FriendChatEvent.EVENT_SENDING_MSG: {
          mChatAdapter.add((FriendMessage) event.getObj());
          mAlvChats.smoothScrollToPosition(Integer.MAX_VALUE);
          break;
        }
        case FriendChatEvent.EVENT_MSG_REMOVE: {
          FriendMessage message = (FriendMessage) event.getObj();
          mChatAdapter.remove(message);
          if (message.getType() == MessageConst.TYPE_TXT) {
            mEtPostContent.setText(message.getContent());
            mEtPostContent.setSelection(message.getContent().length());
          }
          DaoUtils.getFriendMessageDao().delete(message);
        }
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_action_profile));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle args = new Bundle();
        args.putInt("viewId", mConversation.getViewId());
        args.putBoolean("isVisitor", true);
        args.putString("userName", mConversation.getTargetName());
        FragmentHolder.start(FChatActivity.this, ProfileFragment.class, args);
      }
    });

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
          ChatAccount.inst().getFriendSender().photo(mChatId, event.getFile());
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

    mConversation = DaoUtils.getFriendConversationDao()
        .queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(mChatId))
        .unique();

    setTopTitle(mConversation.getTargetName());

    if (mChatId == null) {
      finish();
    }

    mChatAdapter = new FChatAdapter(mConversation);

    mAlvChats.setAdapter(mChatAdapter);

    mChatAdapter.add(FMessageUtil.getMessages(mChatId, 0));
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
          List<FriendMessage> conversation = FMessageUtil.getMessages(mChatId, page);
          if (conversation.size() == 0) {
            has = false;
            FriendMessage message = ChatUtils.infoFriendMsg(mChatId, getString(R.string.no_more_history));
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

    addBannedInfoMsg();

    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fl_emotion, EmojiFragment.newInstance())
        .commitAllowingStateLoss();

    mFlEmotion.setFragmentManager(getSupportFragmentManager());

  }

  @Override
  protected void onResume() {
    super.onResume();

    sCurrentFChatId = mChatId;

    (new AsyncTask<Void, Integer, Void>() {

      private Long mUnreadConversationCount;
      private FriendConversation mConversation;

      @Override
      protected Void doInBackground(Void... voids) {
        mConversation = FMessageUtil.setRead(mChatId);
        publishProgress(1);

        mUnreadConversationCount = ConversationUtil.getUnreadConversationCount();
        publishProgress(2);
        return null;
      }

      @Override
      protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
          case 1:
            U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE, mConversation));
            break;
          case 2:
            U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
            break;
        }
      }
    }).execute();
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

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
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

  private void showMessageDialog(final FriendMessage m) {
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

  private void resendConfirm(final FriendMessage message) {
    final ThemedDialog dialog = new ThemedDialog(this);
    dialog.setTitle("是否重新发送此消息？");
    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (message.getStatus() == MessageConst.STATUS_FAILED) {
          showToast(R.string.resending);
          ChatAccount.inst().getFriendSender().send(message);
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
      mChatAdapter.add(ChatUtils.infoFriendMsg(mChatId, "你已将对方拉黑，不会再收到对方发来的消息"));
    }
  }
}