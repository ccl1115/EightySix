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
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.widget.TitleTab;

/**
 * @author simon
 */
class TabFragment extends BaseFragment {

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  private FeedFragment mFeedFragment;
  private HotFragment mHotFragment;
  private FriendsFragment mFriendsFragment;

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

      @Override
      public int getCount() {
        return 3;
      }
    });

    mTtTab.setViewPager(mVpTab);
  }

  public boolean canPublish() {
    return mFeedFragment != null && mFeedFragment.canPublish();
  }

  public int getCircleId() {
    if (mFeedFragment != null) {
      return mFeedFragment.getCircleId();
    }
    return 0;
  }

  public void setCircle(Circle circle, boolean b) {
    if (mFeedFragment != null) {
      mFeedFragment.setCircle(circle, b);
    }
  }

  public Circle getCircle() {
    if (mFeedFragment != null) {
      return mFeedFragment.getCircle();
    }
    return null;
  }

  public boolean onBackPressed() {
    return mFeedFragment != null && mFeedFragment.onBackPressed();
  }

  public void setCircle(int circleId, boolean skipCache) {
    if (mFeedFragment != null) {
      mFeedFragment.setCircle(circleId, skipCache);
    }
  }
}
