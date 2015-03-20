package com.utree.eightysix.app.region;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.app.region.event.CircleResponseEvent;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TitleTab;
import com.utree.eightysix.widget.TopBar;

/**
 * @author simon
 */
public class TabRegionFragment extends BaseFragment {

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.ll_follow_circles)
  public LinearLayout mLlFollowCircles;

  @InjectView(R.id.ll_set_current)
  public LinearLayout mLlSetCurrent;

  @InjectView(R.id.ll_current)
  public LinearLayout mLlCurrent;

  @InjectView(R.id.rb_current)
  public RoundedButton mRbCurrent;

  private FeedRegionFragment mFeedFragment;
  private HotFeedRegionFragment mHotFeedFragment;
  private FriendsFeedRegionFragment mFriendsFeedFragment;
  private ThemedDialog mNoPermDialog;

  private Circle mCurrentCircle;

  public TabRegionFragment() {
    mFeedFragment = new FeedRegionFragment();
    mHotFeedFragment = new HotFeedRegionFragment();
    mFriendsFeedFragment = new FriendsFeedRegionFragment();
  }

  @OnClick(R.id.ib_send)
  public void onIbSendClicked() {
    if (!canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(getActivity(), -1, null);
    }
  }

  @OnClick(R.id.ll_follow_circles)
  public void onLlFollowCirclesClicked() {
    mLlFollowCircles.setVisibility(View.GONE);
  }

  @OnClick(R.id.tv_set_current)
  public void onTvSetCurrent() {
    BaseCirclesActivity.startSelect(getActivity(), true);
  }

  private void showNoPermDialog() {
    if (mNoPermDialog == null) {
      mNoPermDialog = new ThemedDialog(getActivity());
      View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_publish_locked, null);
      NoPermViewHolder noPermViewHolder = new NoPermViewHolder(view);
      String tip = getString(R.string.no_perm_tip);
      int index = tip.indexOf("解锁条件");
      ForegroundColorSpan span = new ForegroundColorSpan(
          getResources().getColor(R.color.apptheme_primary_light_color));
      SpannableString spannableString = new SpannableString(tip);
      spannableString.setSpan(span, index, index + 4, 0);
      noPermViewHolder.mTvNoPermTip.setText(spannableString);
      mNoPermDialog.setContent(view);
      mNoPermDialog.setPositive(R.string.invite_people, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mNoPermDialog.dismiss();
          U.getShareManager().shareAppDialog(getBaseActivity(), mFeedFragment.getCircle());
        }
      });
      mNoPermDialog.setTitle(getString(R.string.no_perm_to_publish));
    }

    if (!mNoPermDialog.isShowing()) {
      mNoPermDialog.show();
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
            return mFriendsFeedFragment;
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
            return "与我相关";
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
          case 2:
            if (mTtTab.hasBudget(position)) {
              mFriendsFeedFragment.setActive(false);
            }
            mFriendsFeedFragment.setActive(true);
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
  public void onDestroyView() {
    super.onDestroyView();
    clearActive();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    getBaseActivity().setTopTitle("");
    getBaseActivity().setTopSubTitle("");

    setTopBarTitle();
  }

  private void setTopBarTitle() {
    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.ic_drawer));
    getTopBar().getAbLeft().setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mLlFollowCircles.setVisibility(
                mLlFollowCircles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
          }
        });

    getTopBar().getAbRight().setText(getString(R.string.snapshot));
    getTopBar().getAbRight().setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            SnapshotActivity.start(getActivity(), mFeedFragment.getCircle());
          }
        }
    );

    getTopBar().setTitleAdapter(new TopBar.TitleAdapter() {
      @Override
      public String getTitle(int position) {
        if (position == 0) {
          return "在职";
        } else if (position == 1) {
          return "附近";
        }
        return null;
      }

      @Override
      public void onClick(View view, int position) {
        if (view.isSelected()) {
          if (position == 0) {
            setRegionType(0);
          } else if (position == 1) {
            setRegionType(4);
          }
        }
      }

      @Override
      public int getCount() {
        return 2;
      }
    });
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      mFeedFragment.onHiddenChanged(false);
    }

    setTopBarTitle();
  }

  @Subscribe
  public void onNewAllPostCountEvent(NewAllPostCountEvent event) {
    FeedRegionAdapter feedAdapter = mFeedFragment.getFeedAdapter();
    if (feedAdapter != null && feedAdapter.getFeeds().circle != null
        && event.getCircleId() == feedAdapter.getFeeds().circle.id) {
      mTtTab.setTabBudget(0, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    } else {
      mTtTab.setTabBudget(0, "", true);
    }
  }

  @Subscribe
  public void onNewHotPostCountEvent(NewHotPostCountEvent event) {
    FeedRegionAdapter feedAdapter = mFeedFragment.getFeedAdapter();
    if (feedAdapter != null && feedAdapter.getFeeds().circle != null
        && event.getCircleId() == feedAdapter.getFeeds().circle.id) {
      mTtTab.setTabBudget(1, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    } else {
      mTtTab.setTabBudget(1, "", true);
    }
  }

  @Subscribe
  public void onNewFriendsPostCountEvent(NewFriendsPostCountEvent event) {
    FeedRegionAdapter feedAdapter = mFeedFragment.getFeedAdapter();
    if (feedAdapter != null && feedAdapter.getFeeds().circle != null
        && event.getCircleId() == feedAdapter.getFeeds().circle.id) {
      mTtTab.setTabBudget(2, String.valueOf(Math.min(99, event.getCount())), event.getCount() == 0);
    } else {
      mTtTab.setTabBudget(2, "", true);
    }
  }

  @Subscribe
  public void onRegionResponseEvent(RegionResponseEvent event) {
    if (event.getRegion() == 0) {
      getTopBar().setTitleTabSelected(0);
    } else  {
      getTopBar().setTitleTabSelected(1);
    }
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(CurrentCircleResponseEvent event)  {
    mCurrentCircle = event.getCircle();

    if (mCurrentCircle != null) {
      mLlSetCurrent.setVisibility(View.GONE);
      mLlCurrent.setVisibility(View.VISIBLE);
      mRbCurrent.setText(mCurrentCircle.shortName);
      mRbCurrent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          setRegionType(0);
        }
      });
    }
  }

  @Subscribe
  public void onCircleResponseEvent(CircleResponseEvent event) {
    getTopBar().setTitleTabText(0, "关注");
  }


  public boolean canPublish() {
    return mFeedFragment != null && mFeedFragment.canPublish();
  }

  public void setRegionType(int regionType) {
    clearActive();

    mFeedFragment.setRegionType(regionType);
    mHotFeedFragment.setRegionType(regionType);
    mFriendsFeedFragment.setRegionType(regionType);

    if (regionType > 0 && mTtTab != null) {
      mTtTab.setTabBudget(0, "", true);
      mTtTab.setTabBudget(1, "", true);
      mTtTab.setTabBudget(2, "", true);
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
        mFriendsFeedFragment.setActive(true);
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
    if (mFriendsFeedFragment != null) mFriendsFeedFragment.setActive(false);
  }

  @Keep
  class NoPermViewHolder {

    @InjectView(R.id.tv_no_perm_tip)
    TextView mTvNoPermTip;

    NoPermViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
