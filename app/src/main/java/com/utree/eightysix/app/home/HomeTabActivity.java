/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.chat.ConversationUtil;
import com.utree.eightysix.app.chat.FConversationUtil;
import com.utree.eightysix.app.chat.FMessageUtil;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.app.explore.ExploreFragment;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.msg.MsgCenterFragment;
import com.utree.eightysix.app.nearby.NearbyFragment;
import com.utree.eightysix.app.region.TabRegionFragment;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.event.MyPostCommentCountEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.CounterView;
import com.utree.eightysix.widget.ListPopupWindowCompat;
import com.utree.eightysix.widget.ThemedDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */

@Layout(R.layout.activity_home_tab)
public class HomeTabActivity extends BaseActivity {

  public static boolean sIsRunning = false;

  private static final String FIRST_RUN_KEY = "feed";

  @InjectView(R.id.fl_feed)
  public FrameLayout mFlFeed;

  @InjectView(R.id.fl_explore)
  public FrameLayout mFlExplore;

  @InjectView(R.id.fl_message)
  public FrameLayout mFlMessage;

  @InjectView (R.id.fl_nearby)
  public FrameLayout mFlNearBy;

  @InjectView(R.id.rb_msg_count)
  public CounterView mRbMsgCount;

  public TabRegionFragment mTabRegionFragment;
  public MsgCenterFragment mMsgCenterFragment;
  public ExploreFragment mExploreFragment;
  public NearbyFragment mNearbyFragment;

  public Fragment mCurrentFragment;

  private boolean mShouldExit;

  private int mNewCommentCount;
  private int mMyPostCommentCount;
  private int mUnreadConversationCount;
  private int mUnreadFConversationCount;
  private int mRequestCount;
  private int mAssistMessageUnreadCount;
  private ListPopupWindowCompat mListPopupWindow;

  private QRCodeScanFragment mQRCodeScanFragment;

  public static void start(Context context) {
    Intent i = new Intent(context, HomeTabActivity.class);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }

  public static void start(Context context, int regionType) {
    context.startActivity(getIntent(context, regionType));
  }

  public static Intent getIntent(Context context, int regionType) {
    Intent i = new Intent(context, HomeTabActivity.class);
    i.putExtra("regionType", regionType);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    return i;
  }


  @OnClick ({R.id.fl_feed, R.id.fl_nearby, R.id.fl_explore, R.id.fl_message})
  public void onTabItemClicked(View v) {
    clearSelected();
    v.setSelected(true);
    final int id = v.getId();

    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    if (mCurrentFragment != null) {
      t.hide(mCurrentFragment);
    }
    String index = "";
    switch (id) {
      case R.id.fl_feed:
        if (mTabRegionFragment == null) {
          mTabRegionFragment = new TabRegionFragment();
          Bundle args = new Bundle();
          args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
          args.putInt("regionType", getIntent().getIntExtra("regionType", -1));
          mTabRegionFragment.setArguments(args);
          t.add(R.id.fl_content, mTabRegionFragment).commitAllowingStateLoss();
        } else if (mTabRegionFragment.isHidden()) {
          t.show(mTabRegionFragment).commitAllowingStateLoss();
          mTabRegionFragment.onHiddenChanged(false);
        }
        mCurrentFragment = mTabRegionFragment;
        index = "home";
        break;
      case R.id.fl_nearby:
        if (mNearbyFragment == null) {
          mNearbyFragment = new NearbyFragment();
          t.add(R.id.fl_content, mNearbyFragment).commitAllowingStateLoss();
        } else if (mNearbyFragment.isHidden()) {
          t.show(mNearbyFragment).commitAllowingStateLoss();
          mNearbyFragment.onHiddenChanged(false);
        }
        mCurrentFragment = mExploreFragment;
        index = "explore";
        break;
      case R.id.fl_explore:
        if (mExploreFragment == null) {
          mExploreFragment = new ExploreFragment();
          t.add(R.id.fl_content, mExploreFragment).commitAllowingStateLoss();
        } else if (mExploreFragment.isHidden()) {
          t.show(mExploreFragment).commitAllowingStateLoss();
          mExploreFragment.onHiddenChanged(false);
        }
        mCurrentFragment = mExploreFragment;
        index = "explore";
        break;
      case R.id.fl_message:
        if (mMsgCenterFragment == null) {
          mMsgCenterFragment = new MsgCenterFragment();
          t.add(R.id.fl_content, mMsgCenterFragment).commitAllowingStateLoss();

        } else if (mMsgCenterFragment.isHidden()) {
          t.show(mMsgCenterFragment).commitAllowingStateLoss();
          mMsgCenterFragment.onHiddenChanged(false);
        }
        mCurrentFragment = mMsgCenterFragment;
        index = "msg";
        break;
    }
    U.getAnalyser().trackEvent(this, "home_tab", index);
  }

  @Override
  public void onBackPressed() {
    if (mShouldExit) {
      super.onBackPressed();
    } else {
      mShouldExit = true;
      showToast(getString(R.string.press_again_to_exit));
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mShouldExit = false;
        }
      }, 1000);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setFillContent(true);

    U.getChatBus().register(this);

    ContactsSyncService.start(this, false);

    setActionLeftDrawable(null);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        try {
          onTabItemClicked(mFlFeed);
        } catch (IllegalStateException ignored) {

        }
      }
    }, 1000);

    sIsRunning = true;

    onNewIntent(getIntent());

    mUnreadConversationCount = (int) ConversationUtil.getUnreadConversationCount();
    mUnreadFConversationCount = (int) FConversationUtil.getUnreadConversationCount();
    mAssistMessageUnreadCount = (int) FMessageUtil.getAssistUnreadCount();
    mNewCommentCount = Account.inst().getNewCommentCount();
    mMyPostCommentCount = Account.inst().getMyPostCommentCount();
    mRequestCount = Account.inst().getFriendRequestCount();

    mRbMsgCount.setCount(mUnreadConversationCount + mUnreadFConversationCount + mAssistMessageUnreadCount +
        mNewCommentCount + mMyPostCommentCount + mRequestCount);


    getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_add));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          if (mListPopupWindow == null) {
            mListPopupWindow = new ListPopupWindowCompat(v.getContext());
            String[] items = {
                "添加朋友",
                "扫一扫",
                "邀请朋友，赚蓝星",
                "分享蓝莓"
            };
            int[] drawables = {
                R.drawable.popup_add_friend,
                R.drawable.popup_scan,
                R.drawable.popup_blue_star,
                R.drawable.popup_share
            };

            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(items.length);

            for (int i = 0; i < items.length; i++) {
              String str = items[i];
              int drawable = drawables[i];
              Map<String, Object> item = new HashMap<String, Object>();
              item.put("text", str);
              item.put("image", drawable);
              data.add(item);
            }

            mListPopupWindow.setAdapter(new SimpleAdapter(v.getContext(),
                data,
                R.layout.item_explore_popup,
                new String[]{"text", "image"},
                new int[]{R.id.tv, R.id.iv}));
            mListPopupWindow.setWidth(U.dp2px(190));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              mListPopupWindow.setDropDownGravity(Gravity.END);
            }
            mListPopupWindow.setAnchorView(getTopBar());
            mListPopupWindow.setModal(true);

            mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                  case 0:
                    startActivity(new Intent(HomeTabActivity.this, AddFriendActivity.class));
                    break;
                  case 1:
                    if (mQRCodeScanFragment == null) {
                      mQRCodeScanFragment = new QRCodeScanFragment();
                      getSupportFragmentManager().beginTransaction()
                          .add(android.R.id.content, mQRCodeScanFragment)
                          .commit();
                    } else if (mQRCodeScanFragment.isDetached()) {
                      getSupportFragmentManager().beginTransaction()
                          .attach(mQRCodeScanFragment)
                          .commit();
                    }
                    break;
                  case 2:
                    showProgressBar(true);

                    U.request("get_invite_code", new OnResponse2<AddFriendActivity.GetInviteCodeResponse>() {
                      @Override
                      public void onResponseError(Throwable e) {
                        hideProgressBar();
                      }

                      @Override
                      public void onResponse(AddFriendActivity.GetInviteCodeResponse response) {
                        hideProgressBar();
                        final ThemedDialog dialog = new ThemedDialog(HomeTabActivity.this);
                        dialog.setTitle("你的专属邀请码");

                        TextView textView = new TextView(HomeTabActivity.this);
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(response.object.msg).append("\n\n").append("你的专属邀请码是：\n");
                        SpannableString color = new SpannableString(response.object.inviteCode);
                        color.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.apptheme_primary_light_color)), 0, color.length(), 0);
                        builder.append(color);
                        builder.append("\n\n").append("你已经邀请了").append(String.valueOf(response.object.newCount)).append("个人\n");

                        textView.setText(builder);
                        textView.setGravity(Gravity.CENTER);
                        textView.setEms(12);
                        textView.setPadding(U.dp2px(16), U.dp2px(8), U.dp2px(16), U.dp2px(8));
                        textView.setTextSize(16);
                        dialog.setContent(textView);
                        dialog.setPositive("知道啦", new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                            dialog.dismiss();
                          }
                        });

                        dialog.show();
                      }
                    }, AddFriendActivity.GetInviteCodeResponse.class, null, null);
                    break;
                  case 3:
                    U.getShareManager().shareAppDialog(HomeTabActivity.this, Account.inst().getCurrentCircle()).show();
                    break;
                }

                mListPopupWindow.dismiss();
              }
            });
          }
          mListPopupWindow.show();
        } else {
          startActivity(new Intent(HomeTabActivity.this, AddFriendActivity.class));
        }

      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    startService(new Intent(this, FetchNotificationService.class));
  }

  @Override
  protected void onStop() {
    super.onStop();

    stopService(new Intent(this, FetchNotificationService.class));
  }

  @Override
  protected void onDestroy() {
    sIsRunning = false;
    Env.setFirstRun(FIRST_RUN_KEY, false);
    U.getChatBus().unregister(this);

    super.onDestroy();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    int regionType = intent.getIntExtra("regionType", -1);

    if (mTabRegionFragment != null) {
      if (regionType != -1 && regionType != mTabRegionFragment.getRegionType()) {
        mTabRegionFragment.setRegionType(regionType);
      }
    }
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  protected void onTopBarShown() {
    mTabRegionFragment.showTtTab();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onChatEvent(ChatEvent event) {
    if (event.getStatus() == ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT) {
      mUnreadConversationCount = ((Long) event.getObj()).intValue();
      U.getBus().post(new MsgCountEvent(MsgCountEvent.TYPE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
    }
    updateMsgCount();
  }

  @Subscribe
  public void onFriendChatEvent(FriendChatEvent event) {
    if (event.getStatus() == FriendChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT) {
      mUnreadFConversationCount = ((Long) event.getObj()).intValue();
      U.getBus().post(new MsgCountEvent(MsgCountEvent.TYPE_UNREAD_FCONVERSATION_COUNT, mUnreadFConversationCount));
    } else if (event.getStatus() == FriendChatEvent.EVENT_NEW_ASSISTANT_MESSAGE) {
      mAssistMessageUnreadCount = ((Long) event.getObj()).intValue();
      U.getBus().post(new MsgCountEvent(MsgCountEvent.TYPE_ASSIST_MESSAGE_COUNT, mAssistMessageUnreadCount));
    }
    updateMsgCount();
  }

  @Subscribe
  public void onNewCommentCountEvent(NewCommentCountEvent event) {
    mNewCommentCount = event.getCount();
    U.getBus().post(new MsgCountEvent(MsgCountEvent.TYPE_NEW_COMMENT_COUNT, mNewCommentCount + mMyPostCommentCount));
    updateMsgCount();
  }

  @Subscribe
  public void onMyPostCommentCountEvent(MyPostCommentCountEvent event) {
    mMyPostCommentCount = event.getCount();
    U.getBus().post(new MsgCountEvent(MsgCountEvent.TYPE_NEW_COMMENT_COUNT, mNewCommentCount + mMyPostCommentCount));
    updateMsgCount();
  }

  @Subscribe
  public void onMsgCountEvent(MsgCountEvent event) {
    if (event.getType() == MsgCountEvent.TYPE_NEW_FRIEND_REQUEST) {
      mRequestCount = event.getCount();
      updateMsgCount();
    }
  }

  private void updateMsgCount() {
    mRbMsgCount.setCount(mNewCommentCount + mMyPostCommentCount + mUnreadFConversationCount
        + mUnreadConversationCount + mAssistMessageUnreadCount + mRequestCount);
  }

  private void clearSelected() {
    mFlExplore.setSelected(false);
    mFlFeed.setSelected(false);
    mFlMessage.setSelected(false);
    mFlNearBy.setSelected(false);
  }

  public static class MsgCountEvent {

    public static final int TYPE_UNREAD_CONVERSATION_COUNT = 0x1;
    public static final int TYPE_UNREAD_FCONVERSATION_COUNT = 0x2;
    public static final int TYPE_ASSIST_MESSAGE_COUNT = 0x3;
    public static final int TYPE_NEW_COMMENT_COUNT = 0x4;
    public static final int TYPE_NEW_PRAISE = 0x5;
    public static final int TYPE_NEW_FRIEND_REQUEST = 0x6;

    private int count;
    private int type;

    public MsgCountEvent(int type, int count) {
      this.count = count;
      this.type = type;
    }

    public int getCount() {
      return count;
    }

    public int getType() {
      return type;
    }
  }
}