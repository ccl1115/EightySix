/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.chat.ConversationUtil;
import com.utree.eightysix.app.chat.FConversationUtil;
import com.utree.eightysix.app.chat.FMessageUtil;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.app.explore.ExploreFragment;
import com.utree.eightysix.app.msg.MsgCenterFragment;
import com.utree.eightysix.app.region.TabRegionFragment;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.CounterView;

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

  @InjectView(R.id.fl_more)
  public FrameLayout mFlMore;

  @InjectView(R.id.rb_msg_count)
  public CounterView mRbMsgCount;

  public TabRegionFragment mTabRegionFragment;
  public MsgCenterFragment mMsgCenterFragment;
  public ExploreFragment mExploreFragment;
  public ProfileFragment mProfileFragment;

  public Fragment mCurrentFragment;

  private boolean mShouldExit;

  private int mUnreadConversationCount;
  private int mUnreadFConversationCount;
  private int mRequestCount;
  private int mAssistMessageUnreadCount;

  public static void start(Context context) {
    Intent i = new Intent(context, HomeTabActivity.class);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }

  public static void start(Context context, int regionType) {
    Intent i = new Intent(context, HomeTabActivity.class);
    i.putExtra("regionType", regionType);

    if (!(context instanceof Activity)) {
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(i);
  }


  @OnClick({R.id.fl_feed, R.id.fl_explore, R.id.fl_message, R.id.fl_more})
  public void onTabItemClicked(View v) {
    clearSelected();
    v.setSelected(true);
    final int id = v.getId();

    FragmentTransaction t = getSupportFragmentManager().beginTransaction();
    if (mCurrentFragment != null) {
      t.hide(mCurrentFragment);
    }
    switch (id) {
      case R.id.fl_feed:
        if (mTabRegionFragment == null) {
          mTabRegionFragment = new TabRegionFragment();
          Bundle args = new Bundle();
          args.putInt("tabIndex", getIntent().getIntExtra("tabIndex", 0));
          mTabRegionFragment.setArguments(args);
          t.add(R.id.fl_content, mTabRegionFragment).commitAllowingStateLoss();
        } else if (mTabRegionFragment.isHidden()) {
          t.show(mTabRegionFragment).commitAllowingStateLoss();
          mTabRegionFragment.onHiddenChanged(false);
        }
        mCurrentFragment = mTabRegionFragment;
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
        break;
      case R.id.fl_more:
        if (mProfileFragment == null) {
          mProfileFragment = new ProfileFragment();
          t.add(R.id.fl_content, mProfileFragment).commitAllowingStateLoss();
        } else if (mProfileFragment.isHidden()) {
          t.show(mProfileFragment).commitAllowingStateLoss();
          mProfileFragment.onHiddenChanged(false);
        }
        mCurrentFragment = mProfileFragment;
        break;
    }
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
        onTabItemClicked(mFlFeed);
      }
    }, 1000);

    sIsRunning = true;

    onNewIntent(getIntent());

    mUnreadConversationCount = (int) ConversationUtil.getUnreadConversationCount();
    mUnreadFConversationCount = (int) FConversationUtil.getUnreadConversationCount();
    mAssistMessageUnreadCount = (int) FMessageUtil.getAssistUnreadCount();

    mRbMsgCount.setCount(mUnreadConversationCount + mUnreadFConversationCount + mAssistMessageUnreadCount);
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

    if (regionType != -1 && regionType != mTabRegionFragment.getRegionType()) {
      mTabRegionFragment.setRegionType(regionType);
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
  public void onMsgCountEvent(MsgCountEvent event) {
    mRbMsgCount.setCount(event.getCount());
  }

  @Subscribe
  public void onChatEvent(ChatEvent event) {
    if (event.getStatus() == ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT) {
      mUnreadConversationCount = ((Long) event.getObj()).intValue();
      mRbMsgCount.setCount(mUnreadConversationCount + mUnreadFConversationCount + mAssistMessageUnreadCount);
      U.getBus().post(new MsgCountEvent(MsgCountEvent.TYPE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
    }
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
    mRbMsgCount.setCount(mUnreadConversationCount + mUnreadFConversationCount + mAssistMessageUnreadCount);
  }

  private void clearSelected() {
    mFlExplore.setSelected(false);
    mFlFeed.setSelected(false);
    mFlMessage.setSelected(false);
    mFlMore.setSelected(false);
  }

  public static class MsgCountEvent {

    public static final int TYPE_UNREAD_CONVERSATION_COUNT = 0x1;
    public static final int TYPE_UNREAD_FCONVERSATION_COUNT = 0x2;
    public static final int TYPE_ASSIST_MESSAGE_COUNT = 0x3;

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