package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.widget.TitleTab;

/**
 * @author simon
 */
class TabFragment extends BaseFragment {

  @InjectView (R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView (R.id.tt_tab)
  public TitleTab mTtTab;
  private FeedFragment mFeedFragment;
  private HotFragment mHotFragment;
  private FriendsFragment mFriendsFragment;
  private Circle mCircle;

  public TabFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_tab, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mFeedFragment = new FeedFragment();
    mHotFragment = new HotFragment();
    mFriendsFragment = new FriendsFragment();

    mVpTab.setOffscreenPageLimit(2);

    mVpTab.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return mFeedFragment;
          case 1:
            return mHotFragment;
          case 2:
            return mFriendsFragment;
        }
        return null;

      }

      @Override
      public int getCount() {
        return 3;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        switch (position) {
          case 0:
            return "全部";
          case 1:
            return "热贴";
          case 2:
            return "@";
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
        switch (position) {
          case 0:
            mFeedFragment.setActive(true);
            break;
          case 1:
            mHotFragment.setActive(true);
            break;
          case 2:
            mFriendsFragment.setActive(true);
            break;
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    getBaseActivity().getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mFeedFragment.setActive(true);
      }
    }, 500);
  }

  public boolean canPublish() {
    return mFeedFragment != null && mFeedFragment.canPublish();
  }

  public int getCircleId() {
    if (mCircle != null) {
      return mCircle.id;
    }
    if (mFeedFragment != null) {
      return mFeedFragment.getCircleId();
    }
    return 0;
  }

  public void setCircle(Circle circle) {
    clearActive();

    mCircle = circle;

    mFeedFragment.setCircle(circle);
    mHotFragment.setCircle(circle);
    mFriendsFragment.setCircle(circle);

    switch (mVpTab.getCurrentItem()) {
      case 0:
        mFeedFragment.setActive(true);
        break;
      case 1:
        mHotFragment.setActive(true);
        break;
      case 2:
        mFriendsFragment.setActive(true);
        break;
    }
  }

  public Circle getCircle() {
    if (mCircle != null) {
      return mCircle;
    }
    if (mFeedFragment != null) {
      return mFeedFragment.getCircle();
    }
    return null;
  }

  public void setCircle(int circleId) {
    clearActive();

    if (mCircle != null) {
      mCircle.id = circleId;
    } else {
      mCircle = new Circle();
      mCircle.id = circleId;
    }

    mFeedFragment.setCircle(circleId);
    mHotFragment.setCircle(circleId);
    mFriendsFragment.setCircle(circleId);

    switch (mVpTab.getCurrentItem()) {
      case 0:
        mFeedFragment.setActive(true);
        break;
      case 1:
        mHotFragment.setActive(true);
        break;
      case 2:
        mFriendsFragment.setActive(true);
        break;
    }
  }

  public boolean onBackPressed() {
    return false;
  }

  @Subscribe
  public void onCircleEvent(Circle circle) {
    if (circle != null) mCircle = circle;
  }

  @Subscribe
  public void onNewAllPostCountEvent(NewAllPostCountEvent event) {
    mTtTab.setTabBudget(0, String.valueOf(event.getCount()), event.getCount() == 0);
  }

  @Subscribe
  public void onNewHotPostCountEvent(NewHotPostCountEvent event) {
    mTtTab.setTabBudget(1, String.valueOf(event.getCount()), event.getCount() == 0);
  }

  @Subscribe
  public void onNewFriendsPostCountEvent(NewFriendsPostCountEvent event) {
    mTtTab.setTabBudget(2, String.valueOf(event.getCount()), event.getCount() == 0);
  }

  private void clearActive() {
    mFeedFragment.setActive(false);
    mHotFragment.setActive(false);
    mFriendsFragment.setActive(false);
  }
}
