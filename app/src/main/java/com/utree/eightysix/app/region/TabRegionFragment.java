package com.utree.eightysix.app.region;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.account.event.CurrentCircleNameUpdatedEvent;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.circle.event.CircleFollowsChangedEvent;
import com.utree.eightysix.app.feed.SelectAreaFragment;
import com.utree.eightysix.app.feed.event.InviteClickedEvent;
import com.utree.eightysix.app.feed.event.UnlockClickedEvent;
import com.utree.eightysix.app.feed.event.UploadClickedEvent;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.app.region.event.CircleResponseEvent;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.data.FollowCircle;
import com.utree.eightysix.event.CurrentCircleResponseEvent;
import com.utree.eightysix.response.FollowCircleListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.TitleTab;
import com.utree.eightysix.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class TabRegionFragment extends BaseFragment implements AbsRegionFragment.OnScrollListener {

  @InjectView(R.id.vp_tab)
  public ViewPager mVpTab;

  @InjectView(R.id.tt_tab)
  public TitleTab mTtTab;

  @InjectView(R.id.fl_follow_circles)
  public FrameLayout mFlFollowCircles;

  @InjectView(R.id.ll_follow_circles)
  public LinearLayout mLlFollowCircles;

  @InjectView(R.id.ll_set_current)
  public LinearLayout mLlSetCurrent;

  @InjectView(R.id.ll_current)
  public LinearLayout mLlCurrent;

  @InjectView(R.id.tv_current)
  public TextView mTvCurrent;

  @InjectView(R.id.fl_distance_selector)
  public FrameLayout mLlDistanceSelector;

  @InjectView(R.id.ll_add_follow)
  public LinearLayout mLlAddFollow;

  @InjectView(R.id.sb_distance)
  public SeekBar mSbDistance;

  @InjectView(R.id.tv_distance)
  public TextView mTvDistance;

  @InjectView(R.id.tv_area_name)
  public TextView mTvAreaName;

  @InjectView(R.id.rb_region)
  public RadioButton mRbRegion;

  @InjectView(R.id.rb_area)
  public RadioButton mRbArea;

  @InjectView(R.id.iv_select_area)
  public ImageView mIvSelectArea;

  private FeedRegionFragment mFeedFragment;
  private HotFeedRegionFragment mHotFeedFragment;
  private FriendsFeedRegionFragment mFriendsFeedFragment;

  private SelectAreaFragment mSelectAreaFragment;

  private ThemedDialog mNoPermDialog;
  private ThemedDialog mUnlockDialog;
  private ThemedDialog mInviteDialog;

  private List<View> mFollowCircleViews = new ArrayList<View>();

  private ObjectAnimator mHideTtTabAnimator;
  private ObjectAnimator mShowTtTabAnimator;

  private boolean mTtTabHidden;

  private int mLastCircleId = -1;

  public TabRegionFragment() {
    mFeedFragment = new FeedRegionFragment();
    mHotFeedFragment = new HotFeedRegionFragment();
    mFriendsFeedFragment = new FriendsFeedRegionFragment();

    mFeedFragment.setOnScrollListener(this);
    mHotFeedFragment.setOnScrollListener(this);
    mFriendsFeedFragment.setOnScrollListener(this);
  }

  @OnClick(R.id.ib_send)
  public void onIbSendClicked() {
    if (!canPublish()) {
      showNoPermDialog();
    } else {
      PublishActivity.start(getActivity(), mFeedFragment.getCircle().id, null);
    }
  }

  @OnClick(R.id.fl_follow_circles)
  public void onFlFollowCirclesClicked() {
    mFlFollowCircles.setVisibility(View.GONE);
  }

  @OnClick(R.id.tv_set_current)
  public void onTvSetCurrent() {
    BaseCirclesActivity.startSelect(getActivity(), true);
  }

  @OnClick(R.id.tv_add_follow)
  public void onTvAddFollow() {
    BaseCirclesActivity.startMyCircles(getActivity());
  }

  @OnClick(R.id.fl_distance_selector)
  public void onLlDistanceSelector() {
    mLlDistanceSelector.setVisibility(View.GONE);
  }

  @OnClick(R.id.rb_select)
  public void onRbSelect() {
    mLlDistanceSelector.setVisibility(View.GONE);
    if (mRbRegion.isChecked()) {
      int progress = mSbDistance.getProgress();
      if (progress > 9000 && progress < 9200) {
        mFeedFragment.mDistance = 10000;
        mHotFeedFragment.mDistance = 10000;
        mFriendsFeedFragment.mDistance = 10000;
      } else {
        mFeedFragment.mDistance = progress + 1000;
        mHotFeedFragment.mDistance = progress + 1000;
        mFriendsFeedFragment.mDistance = progress + 1000;
      }

      if (progress > 9200) {
        setRegionType(3);
      } else {
        setRegionType(4);
      }
    } else if (mRbArea.isChecked()) {
      setRegionType(5);
    }
  }

  @OnClick(R.id.iv_select_area)
  public void onIvSelectArea() {
    if (mSelectAreaFragment == null) {
      mSelectAreaFragment = new SelectAreaFragment();
      mSelectAreaFragment.setCallback(new SelectAreaFragment.Callback() {
        @Override
        public void onAreaSelected(int areaType, int areaId, String areaName) {
          mLlDistanceSelector.setVisibility(View.GONE);
          mTvAreaName.setText(areaName);
          mFeedFragment.mAreaId = areaId;
          mFeedFragment.mAreaType = areaType;
          mHotFeedFragment.mAreaId = areaId;
          mHotFeedFragment.mAreaType = areaType;
          mFriendsFeedFragment.mAreaId = areaId;
          mFriendsFeedFragment.mAreaType = areaType;
          setRegionType(5);
        }
      });
      getFragmentManager().beginTransaction()
          .add(R.id.content, mSelectAreaFragment)
          .commit();
    } else if (mSelectAreaFragment.isDetached()) {
      getFragmentManager().beginTransaction()
          .attach(mSelectAreaFragment)
          .commit();
    }
  }

  public final void hideTtTab() {
    if (mTtTabHidden) return;
    if (mHideTtTabAnimator.isRunning()) {
      return;
    }
    if (mShowTtTabAnimator.isRunning()) {
      mShowTtTabAnimator.cancel();
    }
    mHideTtTabAnimator.start();
  }

  public final void showTtTab() {
    if (!mTtTabHidden) return;
    if (mShowTtTabAnimator.isRunning()) {
      return;
    }
    if (mHideTtTabAnimator.isRunning()) {
      mHideTtTabAnimator.cancel();
    }
    mShowTtTabAnimator.start();
  }

  private void initAnimator() {
    mHideTtTabAnimator = ObjectAnimator.ofFloat(mTtTab, "translationY", 0,
        -getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height));
    mHideTtTabAnimator.setDuration(150);
    mHideTtTabAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mTtTabHidden = true;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    mShowTtTabAnimator = ObjectAnimator.ofFloat(mTtTab, "translationY",
        -getResources().getDimensionPixelSize(R.dimen.activity_top_bar_height), 0f);
    mShowTtTabAnimator.setDuration(150);
    mShowTtTabAnimator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mTtTabHidden = false;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
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

    initAnimator();

    mSbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float value = progress / 1000f + 1;
        if (value > 10f && value <= 10.2f) {
          mTvDistance.setText("10km");
        } else if (value > 10.2f) {
          mTvDistance.setText("同城");
        } else {
          mTvDistance.setText(String.format("%.2fkm", value));
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

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

    mTtTab.setOnTabItemClicked(new TitleTab.OnTabItemClickedListener() {
      @Override
      public void onTabItemClicked(View view, int position) {
        if (mVpTab.getCurrentItem() == position) {
          switch (position) {
            case 0:
              mFeedFragment.mLvFeed.setSelection(0);
              break;
            case 1:
              mHotFeedFragment.mLvFeed.setSelection(0);
              break;
            case 2:
              mFriendsFeedFragment.mLvFeed.setSelection(0);
              break;
          }
        }
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

    getBaseActivity().setTopTitle("");
    getBaseActivity().setTopSubTitle("");

    setTopBarTitle();

    requestFollowCircles();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    clearActive();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      setTopBarTitle();
    }
    mFeedFragment.onHiddenChanged(hidden);
    mFriendsFeedFragment.onHiddenChanged(hidden);
    mHotFeedFragment.onHiddenChanged(hidden);
  }

  @Subscribe
  public void onUploadClicked(UploadClickedEvent event) {
    if (getBaseActivity() != null) {
      ContactsSyncService.start(getBaseActivity(), true);
      getBaseActivity().showProgressBar(true);
    }
  }

  @Subscribe
  public void onUnlockClicked(UnlockClickedEvent event) {
    if (getBaseActivity() != null) {
      showUnlockDialog();
      ContactsSyncService.start(getBaseActivity(), true);
    }
  }

  @Subscribe
  public void onInviteClicked(InviteClickedEvent event) {
    if (getBaseActivity() != null) {
      showInviteDialog();
    }
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
    if (event.getRegion() == 3) {
      mRbRegion.setChecked(true);
      mSbDistance.setProgress(10000);
    } else if (event.getRegion() == 4) {
      mRbRegion.setChecked(true);
      mSbDistance.setProgress(event.getDistance() - 1000);
    } else if (event.getRegion() == 5) {
      mRbArea.setChecked(true);
    }
    mTvAreaName.setText(event.getCityName());
  }

  @Subscribe
  public void onCircleResponseEvent(CircleResponseEvent event) {
    clearSelectedCircle();
    if (event.getCircle() != null) {
      for (View view : mFollowCircleViews) {
        if (view.getTag() != null) {
          if (((FollowCircle) view.getTag()).factoryId == event.getCircle().id) {
            view.setSelected(true);
            break;
          }
        }
      }
    }
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(CurrentCircleResponseEvent event) {
    if (event.getCircle() != null) {
      clearSelectedCircle();
      mLlSetCurrent.setVisibility(View.GONE);
      mLlCurrent.setVisibility(View.VISIBLE);
      mTvCurrent.setText(event.getCircle().shortName);
      mTvCurrent.setSelected(true);
      mTvCurrent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            setRegionType(0);
            mFlFollowCircles.setVisibility(View.GONE);
            getTopBar().setTitleTabSelected(0);
            getTopBar().setTitleTabText(0, "在职");
            getTopBar().setSubTitle("");
          }
        }
      });
    }
  }

  @Subscribe
  public void onCurrentCircleNameUpdatedEvent(CurrentCircleNameUpdatedEvent event) {
    setRegionType(0);
  }

  public boolean canPublish() {
    return mFeedFragment != null && mFeedFragment.canPublish();
  }

  public void setRegionType(int regionType) {
    clearActive();

    mFeedFragment.requestRegion(regionType);
    mHotFeedFragment.requestRegion(regionType);
    mFriendsFeedFragment.requestRegion(regionType);

    if (regionType > 0 && mTtTab != null) {
      mTtTab.setTabBudget(0, "", true);
      mTtTab.setTabBudget(1, "", true);
      mTtTab.setTabBudget(2, "", true);
    } else {
      mLastCircleId = -1;
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

  public void setCircleId(int circleId) {
    clearActive();

    mLastCircleId = circleId;

    mFeedFragment.requestFeeds(circleId);
    mHotFeedFragment.requestFeeds(circleId);
    mFriendsFeedFragment.requestFeeds(circleId);

    mTtTab.setTabBudget(0, "", true);
    mTtTab.setTabBudget(1, "", true);
    mTtTab.setTabBudget(2, "", true);

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

  @Subscribe
  public void onCircleFollowsChangedEvent(CircleFollowsChangedEvent event) {
    requestFollowCircles();
  }

  private void clearSelectedCircle() {
    mTvCurrent.setSelected(false);

    for (View v : mFollowCircleViews) {
      v.setSelected(false);
    }
  }

  private void clearActive() {
    if (mFeedFragment != null) mFeedFragment.setActive(false);
    if (mHotFeedFragment != null) mHotFeedFragment.setActive(false);
    if (mFriendsFeedFragment != null) mFriendsFeedFragment.setActive(false);
  }

  private void showUnlockDialog() {
    if (mUnlockDialog == null) {
      mUnlockDialog = new ThemedDialog(getBaseActivity());
      View view = LayoutInflater.from(getBaseActivity()).inflate(R.layout.dialog_unlock, null);
      TextView tipView = (TextView) view.findViewById(R.id.tv_unlock_tip);
      String tip = getString(R.string.unlock_tip, U.getSyncClient().getSync().unlockFriends, U.getSyncClient().getSync().unlockFriends);
      int index = tip.indexOf("解锁条件");
      ForegroundColorSpan span = new ForegroundColorSpan(
          getResources().getColor(R.color.apptheme_primary_light_color));
      SpannableString spannableString = new SpannableString(tip);
      spannableString.setSpan(span, index, index + 4, 0);
      tipView.setText(spannableString);
      mUnlockDialog.setContent(view);
      mUnlockDialog.setPositive(R.string.invite_people, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mUnlockDialog.dismiss();
          showInviteDialog();
        }
      });
      mUnlockDialog.setTitle("帖子为什么会隐藏");
    }

    if (!mUnlockDialog.isShowing()) {
      mUnlockDialog.show();
    }
  }

  private void showInviteDialog() {
    if (mInviteDialog == null) {
      mInviteDialog = U.getShareManager().shareAppDialog(getBaseActivity(), mFeedFragment.getCircle());
    }
    if (!mInviteDialog.isShowing()) {
      mInviteDialog.show();
    }
  }

  private void setTopBarTitle() {
    if (mFeedFragment.getRegionType() == 4 || mFeedFragment.getRegionType() == 3 || mFeedFragment.getRegionType() == 5) {
      getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.tb_distance));
    } else {
      getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.tb_drawer));
    }
    getTopBar().getAbLeft().setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (getTopBar().getTitleBarSelectedIndex() == 0) {
              mFlFollowCircles.setVisibility(
                  mFlFollowCircles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            } else if (getTopBar().getTitleBarSelectedIndex() == 1) {
              mLlDistanceSelector.setVisibility(
                  mLlDistanceSelector.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
          }
        });

    getTopBar().getAbRight().hide();


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
      public void onSelected(View view, int position) {
        mFlFollowCircles.setVisibility(View.GONE);
        mLlDistanceSelector.setVisibility(View.GONE);
        if (position == 0) {
          if (mLastCircleId != -1) {
            setCircleId(mLastCircleId);
          } else {
            setRegionType(0);
          }
          getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.tb_drawer));
          getTopBar().getAbRight().hide();
        } else if (position == 1) {
          clearFollowCircleViews();
          setRegionType(4);
          getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.tb_distance));
          getTopBar().getAbRight().hide();
        }
      }

      @Override
      public int getCount() {
        return 2;
      }
    });
  }


  private void requestFollowCircles() {
    U.request("follow_circle_list", new OnResponse2<FollowCircleListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FollowCircleListResponse response) {

        if (response.object.size() > 0) {
          mLlAddFollow.setVisibility(View.GONE);
        } else {
          mLlAddFollow.setVisibility(View.VISIBLE);
        }

        if (response.object.size() % 3 == 1) {
          response.object.add(null);
          response.object.add(null);
        } else if (response.object.size() % 3 == 2) {
          response.object.add(null);
        }

        mLlFollowCircles.removeAllViews();

        for (int i = 0, size = response.object.size(); i < size; i += 3) {
          buildFollowCircleRow(new FollowCircle[]{
              response.object.get(i),
              response.object.get(i + 1),
              response.object.get(i + 2)
          });
        }
      }
    }, FollowCircleListResponse.class);
  }

  private void buildFollowCircleRow(final FollowCircle[] circles) {

    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

    int m = U.dp2px(8);
    if (circles[0] != null) {
      TextView textView = new TextView(getActivity());
      textView.setSingleLine();
      textView.setLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setText(circles[0].factoryName);
      textView.setTag(circles[0]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, U.dp2px(30), 1);
      params.setMargins(0, m, m, 0);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));
      textView.setTag(circles[0]);

      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            setCircleId(circles[0].factoryId);
            getTopBar().setTitleTabText(0, "关注");
            getTopBar().setTitleTabSelected(0);
            getTopBar().setSubTitle("");
            mFlFollowCircles.setVisibility(View.GONE);
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    }
    if (circles[1] != null) {
      TextView textView = new TextView(getActivity());
      textView.setSingleLine();
      textView.setLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setText(circles[1].factoryName);
      textView.setTag(circles[1]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, U.dp2px(30), 1);
      params.setMargins(m, m, m, 0);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));
      textView.setTag(circles[1]);

      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            setCircleId(circles[1].factoryId);
            getTopBar().setTitleTabText(0, "关注");
            getTopBar().setTitleTabSelected(0);
            getTopBar().setSubTitle("");
            mFlFollowCircles.setVisibility(View.GONE);
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    } else {
      View view = new View(getActivity());
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 1, 1);
      params.setMargins(m, m, m, 0);
      view.setLayoutParams(params);
      linearLayout.addView(view);
    }

    if (circles[2] != null) {
      TextView textView = new TextView(getActivity());
      textView.setSingleLine();
      textView.setLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      textView.setText(circles[2].factoryName);
      textView.setTag(circles[2]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, U.dp2px(30), 1);
      params.setMargins(m, m, 0, 0);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));
      textView.setTag(circles[2]);

      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            setCircleId(circles[2].factoryId);
            getTopBar().setTitleTabText(0, "关注");
            getTopBar().setTitleTabSelected(0);
            getTopBar().setSubTitle("");
            mFlFollowCircles.setVisibility(View.GONE);
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    } else {
      View view = new View(getActivity());
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 1, 1);
      params.setMargins(m, m, 0, 0);
      view.setLayoutParams(params);
      linearLayout.addView(view);
    }

    mLlFollowCircles.addView(linearLayout);
  }

  private void clearFollowCircleViews() {
    mTvCurrent.setSelected(false);
    for (View view : mFollowCircleViews) {
      view.setSelected(false);
    }
  }

  @Override
  public void onShowTopBar() {
    showTtTab();
  }

  @Override
  public void onHideTopBar() {
    hideTtTab();
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
