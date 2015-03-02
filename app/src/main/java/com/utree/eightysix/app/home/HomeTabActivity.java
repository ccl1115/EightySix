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
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.msg.MsgCenterFragment;
import com.utree.eightysix.app.region.TabRegionFragment;

/**
 */

@Layout(R.layout.activity_home_tab)
public class HomeTabActivity extends BaseActivity {

  @InjectView(R.id.fl_feed)
  public FrameLayout mFlFeed;

  @InjectView(R.id.fl_explore)
  public FrameLayout mFlExplore;

  @InjectView(R.id.fl_message)
  public FrameLayout mFlMessage;

  @InjectView(R.id.fl_more)
  public FrameLayout mFlMore;

  public TabRegionFragment mTabRegionFragment;
  public MsgCenterFragment mMsgCenterFragment;

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
    t.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
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
          t.add(R.id.fl_content, mTabRegionFragment).commit();
        } else if (mTabRegionFragment.isHidden()) {
          t.show(mTabRegionFragment).commit();
        }
        mCurrentFragment = mTabRegionFragment;
        break;
      case R.id.fl_explore:
        break;
      case R.id.fl_message:
        if (mMsgCenterFragment == null) {
          mMsgCenterFragment = new MsgCenterFragment();
          t.add(R.id.fl_content, mMsgCenterFragment).commit();

        } else if (mMsgCenterFragment.isHidden()) {
          t.show(mMsgCenterFragment).commit();
        }
        mCurrentFragment = mMsgCenterFragment;
        break;
      case R.id.fl_more:
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

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        onTabItemClicked(mFlFeed);
      }
    }, 1000);
  }

  @Override
  public void onActionLeftClicked() {

  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void clearSelected() {
    mFlExplore.setSelected(false);
    mFlFeed.setSelected(false);
    mFlMessage.setSelected(false);
    mFlMore.setSelected(false);
  }
}