/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.widget.TitleTab;

/**
 */
@Layout(R.layout.activity_my_posts)
@TopTitle(R.string.my_posts)
public class MyPostsActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  private MyAnonymousPostsFragment mMyAnonymousPostsFragment = MyAnonymousPostsFragment.getInstance();
  private MyRealNamePostsFragment mMyRealNamePostsFragment = MyRealNamePostsFragment.getInstance();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mMyAnonymousPostsFragment;
          case 1:
            return mMyRealNamePostsFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 2;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        switch (position) {
          case 0:
            return "匿名贴";
          case 1:
            return "非匿名贴";
        }
        return null;
      }
    });

    mTtTab.setViewPager(mVpTab);

    mTtTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        switch (position) {
          case 0:
            mMyAnonymousPostsFragment.setActive(true);
          case 1:
            mMyRealNamePostsFragment.setActive(true);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mMyAnonymousPostsFragment.setActive(true);
      }
    }, 500);
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
}