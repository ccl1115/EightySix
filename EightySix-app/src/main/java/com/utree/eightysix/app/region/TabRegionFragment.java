package com.utree.eightysix.app.region;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.widget.ITopBar2;
import com.utree.eightysix.widget.TitleTab;

/**
 * @author simon
 */
public class TabRegionFragment extends BaseFragment {

  @InjectView (R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView (R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.ll_filter)
  public LinearLayout mLlFilter;

  @OnClick(R.id.v_mask)
  public void onVMaskClicked() {
    mLlFilter.setVisibility(View.GONE);
  }

  @InjectView(R.id.tv_gender_all)
  public TextView mTvAll;

  @InjectView(R.id.tv_gender_male)
  public TextView mTvMale;

  @InjectView(R.id.tv_gender_female)
  public TextView mTvFemale;

  @InjectView(R.id.tv_region_0)
  public TextView mTvRegion0;

  @InjectView(R.id.tv_region_1)
  public TextView mTvRegion1;

  @InjectView(R.id.tv_region_2)
  public TextView mTvRegion2;

  @InjectView(R.id.tv_region_3)
  public TextView mTvRegion3;

  private FeedRegionFragment mFeedFragment;
  private HotFeedRegionFragment mHotFeedFragment;

  public TabRegionFragment() {
    mFeedFragment = new FeedRegionFragment();
    mHotFeedFragment = new HotFeedRegionFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_tab, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mVpTab.setOffscreenPageLimit(2);

    mVpTab.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mFeedFragment;
          case 1:
            return mHotFeedFragment;
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
            return "最新";
          case 1:
            return "热门";
        }
        return "";
      }
    });

    mTtTab.setViewPager(mVpTab);

    mTtTab.setOnPageChangedListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        U.getAnalyser().trackEvent(getActivity(), "topic_detail_tab", position);

        switch (position) {
          case 0:
            if (mTtTab.hasBudget(position)) {
              mFeedFragment.setActive(false);
            }
            mFeedFragment.setActive(true);
            break;
          case 1:
            if (mTtTab.hasBudget(position)) {
              mHotFeedFragment.setActive(false);
            }
            mHotFeedFragment.setActive(true);
            break;
        }

        U.getAnalyser().trackEvent(U.getContext(), "feed_tab_switch", String.valueOf(position));
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    mVpTab.setCurrentItem(getArguments().getInt("tabIndex"));

    getBaseActivity().getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (getArguments().getInt("tabIndex") == 0) {
          mFeedFragment.setActive(true);
        }
      }
    }, 500);

  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    onHiddenChanged(false);
  }

  @Subscribe
  public void onNewAllPostCountEvent(NewAllPostCountEvent event) {
    if (event.getCircleId() == mFeedFragment.getFeedAdapter().getFeeds().circle.id) {
      mTtTab.setTabBudget(0, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    } else {
      mTtTab.setTabBudget(0, "", true);
    }
  }

  @Subscribe
  public void onNewHotPostCountEvent(NewHotPostCountEvent event) {
    if (event.getCircleId() == mFeedFragment.getFeedAdapter().getFeeds().circle.id) {
      mTtTab.setTabBudget(1, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    } else {
      mTtTab.setTabBudget(1, "", true);
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (mFeedFragment.isAdded()) {
      mFeedFragment.onHiddenChanged(hidden);
    }

    if (!hidden) {
      getTopBar().setLeftText("筛选");
      getTopBar().setCallback(new ITopBar2.Callback() {
        @Override
        public void onLeftClicked(View v) {
          mLlFilter.setVisibility(mLlFilter.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onRightClicked(View v) {

        }
      });
    }
  }

  public boolean canPublish() {
    return mFeedFragment != null && mFeedFragment.canPublish();
  }

  public void setRegionType(int regionType) {
    clearActive();

    mFeedFragment.setRegionType(regionType);
    mHotFeedFragment.setRegionType(regionType);

    if (mVpTab == null) return;

    mVpTab.setCurrentItem(0);

    switch (mVpTab.getCurrentItem()) {
      case 0:
        mFeedFragment.setActive(true);
        break;
      case 1:
        mHotFeedFragment.setActive(true);
        break;
    }
  }

  public int getRegionType() {
    return mFeedFragment.getRegionType();
  }

  public void setTabIndex(int index) {
    if (mVpTab == null) return;

    mVpTab.setCurrentItem(index);
  }

  public boolean onBackPressed() {
    return false;
  }

  @Subscribe
  public void onPostPublishedEvent(PostPublishedEvent event) {
    mVpTab.setCurrentItem(0);
  }

  private void clearActive() {
    if (mFeedFragment != null) mFeedFragment.setActive(false);
    if (mHotFeedFragment != null) mHotFeedFragment.setActive(false);
  }
}
