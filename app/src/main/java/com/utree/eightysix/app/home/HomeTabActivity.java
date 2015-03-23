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
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.explore.ExploreFragment;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.msg.MsgCenterFragment;
import com.utree.eightysix.app.region.TabRegionFragment;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.RoundedButton;

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
  public RoundedButton mRbMsgCount;

  public TabRegionFragment mTabRegionFragment;
  public MsgCenterFragment mMsgCenterFragment;
  public ExploreFragment mExploreFragment;
  public ProfileFragment mProfileFragment;

  public Fragment mCurrentFragment;

  private boolean mShouldExit;

  public static void start(Context context) {
    Intent i = new Intent(context, HomeTabActivity.class);

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
    super.onDestroy();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onMsgCountEvent(MsgCountEvent event) {
    if (event.getCount() == 0) {
      mRbMsgCount.setVisibility(View.INVISIBLE);
    } else {
      mRbMsgCount.setVisibility(View.VISIBLE);
    }

    mRbMsgCount.setText(String.valueOf(event.getCount()));
  }

  private void clearSelected() {
    mFlExplore.setSelected(false);
    mFlFeed.setSelected(false);
    mFlMessage.setSelected(false);
    mFlMore.setSelected(false);
  }

  public static class MsgCountEvent {
    private int count;

    public MsgCountEvent(int count) {
      this.count = count;
    }

    public int getCount() {
      return count;
    }
  }
}