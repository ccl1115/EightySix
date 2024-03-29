/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.ladder;

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
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.TitleTab;

/**
 */
@Layout(R.layout.activity_ladder)
@TopTitle(R.string.ladder)
public class LadderActivity extends BaseActivity {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  public BaseLadderFragment mWeekRankLadderFragment = new WeekRankLadderFragment();

  public BaseLadderFragment mAllRankLadderFragment = new AllRankLadderFragment();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Env.setFirstRun("ladder_new", false);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mVpTab.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mWeekRankLadderFragment;
          case 1:
            return mAllRankLadderFragment;
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
            return "上升最快";
          case 1:
            return "经验最多";
        }
        return null;
      }
    });

    mTtTab.setViewPager(mVpTab);
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

  public static class WeekRankLadderFragment extends BaseLadderFragment {
    @Override
    protected String getApi() {
      return "ladder_week_rank";
    }
  }

  public static class AllRankLadderFragment extends BaseLadderFragment {
    @Override
    protected String getApi() {
      return "ladder_all_rank";
    }
  }
}