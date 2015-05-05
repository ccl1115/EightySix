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
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.HolderFragment;
import com.utree.eightysix.app.feed.event.StartPublishActivityEvent;
import com.utree.eightysix.app.hometown.event.HometownNotSetEvent;
import com.utree.eightysix.widget.TitleTab;
import com.utree.eightysix.widget.TopBar;

/**
 */
public class HometownTabFragment extends HolderFragment {

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.vp_hometown)
  public ViewPager mVpHometown;

  private HotHometownFeedsFragment mHotHometownFeedsFragment;

  private NewHometownFeedsFragment mNewHometownFeedsFragment;

  private SetHometownFragment mSetHometownFragment;

  private HometownInfoFragment mHometownInfoFragment;

  public HometownTabFragment() {
    mNewHometownFeedsFragment = new NewHometownFeedsFragment();
    mHotHometownFeedsFragment = new HotHometownFeedsFragment();
  }

  @OnClick(R.id.ib_send)
  public void onIbSendClicked() {
    U.getBus().post(new StartPublishActivityEvent());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_hometown, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    getBaseActivity().setFillContent(true);

    getBaseActivity().setTopTitle(getString(R.string.hometown_feed));
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().getTopBar().setTitleClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);

    getBaseActivity().getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getBaseActivity().getTopBar().getAbLeft().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getBaseActivity().finish();
      }
    });

    getBaseActivity().getTopBar().getAbRight().setText("设置");
    getBaseActivity().getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showSetHometownFragment();
      }
    });

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

  private void showSetHometownFragment() {
    if (mSetHometownFragment == null) {
      mSetHometownFragment = new SetHometownFragment();
      Bundle bundle = new Bundle();
      bundle.putString("title", "设置家乡");
      mSetHometownFragment.setArguments(bundle);
      mSetHometownFragment.setCallback(new SetHometownFragment.Callback() {
        @Override
        public void onHometownSet(int hometownId) {
          mHotHometownFeedsFragment.setHometown(0, -1);
          mNewHometownFeedsFragment.setHometown(0, -1);
          refresh();
        }
      });
      getFragmentManager().beginTransaction()
          .add(R.id.content, mSetHometownFragment)
          .commit();
    } else if (mSetHometownFragment.isDetached()) {
      getFragmentManager().beginTransaction()
          .attach(mSetHometownFragment)
          .commit();
    }
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

  public void refresh() {
    clearActive();

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

  @Subscribe
  public void onHometownNotSetEvent(HometownNotSetEvent event) {
    showSetHometownFragment();
  }

  @Override
  public void onTitleClicked() {
    if (mHometownInfoFragment == null) {
      mHometownInfoFragment = new HometownInfoFragment();
      mHometownInfoFragment.setCallback(new HometownInfoFragment.Callback() {
        @Override
        public void onHometownClicked(int hometownId, int hometownType, String hometownName) {
          setHometown(hometownId, hometownType, hometownName);
        }
      });
      getFragmentManager().beginTransaction()
          .add(R.id.content, mHometownInfoFragment)
          .commit();
    } else if (mHometownInfoFragment.isDetached()) {
      getFragmentManager().beginTransaction()
          .attach(mHometownInfoFragment)
          .commit();
    }
  }

  @Override
  protected void onActionLeftClicked() {
    getActivity().finish();
  }

  @Override
  protected void onActionOverflowClicked() {

  }
}
