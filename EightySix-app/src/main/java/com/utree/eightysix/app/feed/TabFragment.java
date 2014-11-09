package com.utree.eightysix.app.feed;

import android.app.Activity;
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
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.app.tag.MoreTagFragment;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.widget.TitleTab;

/**
 * @author simon
 */
public class TabFragment extends BaseFragment {

  static final int MODE_MORE = 1;
  static final int MODE_HAS_FRIENDS = 2;

  @InjectView (R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView (R.id.tt_tab)
  public TitleTab mTtTab;

  private FeedFragment mFeedFragment;
  private HotFeedFragment mHotFeedFragment;
  private FriendsFeedFragment mFriendsFeedFragment;
  private MoreTagFragment mMoreTagFragment;
  private BaseFragment mThirdFragment;
  private Circle mCircle;
  private int mMode;

  public TabFragment() {
    mFeedFragment = new FeedFragment();
    mHotFeedFragment = new HotFeedFragment();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    mMode = getArguments().getInt("mode");
    if (mMode == MODE_HAS_FRIENDS) {
      mThirdFragment = new FriendsFeedFragment();
    } else {
      mThirdFragment = new MoreTagFragment();
    }
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
          case 2:
            return mThirdFragment;
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
            return "热门";
          case 2:
            if (mMode == MODE_HAS_FRIENDS) {
              return "与我相关";
            } else if (mMode == MODE_MORE) {
              return "更多";
            }
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
          case 2:
            if (mTtTab.hasBudget(position)) {
              mThirdFragment.setActive(false);
            }
            mThirdFragment.setActive(true);
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
    mHotFeedFragment.setCircle(circle);
    if (mThirdFragment instanceof FriendsFeedFragment) {
      ((FriendsFeedFragment) mThirdFragment).setCircle(circle);
    }

    if (mVpTab == null) return;

    mVpTab.setCurrentItem(0);

    switch (mVpTab.getCurrentItem()) {
      case 0:
        mFeedFragment.setActive(true);
        break;
      case 1:
        mHotFeedFragment.setActive(true);
        break;
      case 2:
        mThirdFragment.setActive(true);
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
    mHotFeedFragment.setCircle(circleId);
    if (mThirdFragment instanceof FriendsFeedFragment) {
      ((FriendsFeedFragment) mThirdFragment).setCircle(circleId);
    }

    if (mVpTab == null) return;

    mVpTab.setCurrentItem(0);

    switch (mVpTab.getCurrentItem()) {
      case 0:
        mFeedFragment.setActive(true);
        break;
      case 1:
        mHotFeedFragment.setActive(true);
        break;
      case 2:
        mThirdFragment.setActive(true);
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
    if (event.getCircleId() == mCircle.id) {
      mTtTab.setTabBudget(0, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    }
  }

  @Subscribe
  public void onNewHotPostCountEvent(NewHotPostCountEvent event) {
    if (event.getCircleId() == mCircle.id) {
      mTtTab.setTabBudget(1, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    }
  }

  @Subscribe
  public void onNewFriendsPostCountEvent(NewFriendsPostCountEvent event) {
    if (event.getCircleId() == mCircle.id) {
      mTtTab.setTabBudget(2, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    }
  }

  @Subscribe
  public void onPostPublishedEvent(PostPublishedEvent event) {
    mVpTab.setCurrentItem(0);
  }

  private void clearActive() {
    if (mFeedFragment != null) mFeedFragment.setActive(false);
    if (mHotFeedFragment != null) mHotFeedFragment.setActive(false);
    if (mThirdFragment != null) mThirdFragment.setActive(false);
  }
}
