/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.hometown;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.widget.TitleTab;
import com.utree.eightysix.widget.TopBar;

/**
 */
public class HometownTabFragment extends BaseFragment {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_hometown)
  public ViewPager mVpHometown;

  private HotHometownFeedsFragment mHotHometownFeedsFragment;

  private NewHometownFeedsFragment mNewHometownFeedsFragment;

  public HometownTabFragment() {
    mNewHometownFeedsFragment = new NewHometownFeedsFragment();
    mHotHometownFeedsFragment = new HotHometownFeedsFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_hometown, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    getBaseActivity().setTopTitle(getString(R.string.hometown_feed));
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().getTopBar().setTitleClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);

    mVpHometown.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

      @Override
      public Fragment getItem(int position) {
        if (position == 0) {
          return mNewHometownFeedsFragment;
        } else if (position == 1) {
          return mHotHometownFeedsFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 2;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        if (position == 0) {
          return "最新";
        } else if (position == 1) {
          return "热门";
        }
        return null;
      }

    });

    mTtTab.setViewPager(mVpHometown);
    mTtTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        if (position == 0) {
          mNewHometownFeedsFragment.setActive(true);
        } else if (position == 1) {
          mHotHometownFeedsFragment.setActive(true);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getBaseActivity().getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mNewHometownFeedsFragment.setActive(true);
      }
    }, 500);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    clearActive();
  }

  public void setHometown(int hometownId, int hometownType, String hometownName) {
    clearActive();

    mNewHometownFeedsFragment.setHometown(hometownId, hometownType);
    mHotHometownFeedsFragment.setHometown(hometownId, hometownType);

    if (getBaseActivity() != null) {
      getBaseActivity().setTopTitle(hometownName);
      getBaseActivity().setTopSubTitle("");
    }

    if (mVpHometown == null) return;

    mVpHometown.setCurrentItem(0);

    switch (mVpHometown.getCurrentItem()) {
      case 0:
        mNewHometownFeedsFragment.setActive(true);
        break;
      case 1:
        mHotHometownFeedsFragment.setActive(true);
        break;
    }
  }

  private void clearActive() {
    mNewHometownFeedsFragment.setActive(false);
    mHotHometownFeedsFragment.setActive(false);

  }
}
