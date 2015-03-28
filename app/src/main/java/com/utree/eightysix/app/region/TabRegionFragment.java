package com.utree.eightysix.app.region;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

  @InjectView(R.id.tv_current)
  public TextView mTvCurrent;

  @InjectView(R.id.ll_distance_selector)
  public LinearLayout mLlDistanceSelector;

  @InjectView(R.id.ll_add_follow)
  public LinearLayout mLlAddFollow;

  @InjectView(R.id.sb_distance)
  public SeekBar mSbDistance;

  @InjectView(R.id.tv_distance)
  public TextView mTvDistance;

  private FeedRegionFragment mFeedFragment;
  private HotFeedRegionFragment mHotFeedFragment;
  private FriendsFeedRegionFragment mFriendsFeedFragment;

  private ThemedDialog mNoPermDialog;

  private List<View> mFollowCircleViews = new ArrayList<View>();

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

  @OnClick(R.id.tv_add_follow)
  public void onTvAddFollow() {
    BaseCirclesActivity.startMyCircles(getActivity());
  }

  @OnClick(R.id.ll_distance_selector)
  public void onLlDistanceSelector() {
    mLlDistanceSelector.setVisibility(View.GONE);
  }

  @OnClick(R.id.rb_select)
  public void onRbSelect() {
    mLlDistanceSelector.setVisibility(View.GONE);
    mFeedFragment.mDistance = mSbDistance.getProgress();
    mHotFeedFragment.mDistance = mSbDistance.getProgress();
    mFriendsFeedFragment.mDistance = mSbDistance.getProgress();
    float value = mSbDistance.getProgress() / 1000f + 1;
    if (value == 10) {
      setRegionType(3);
    } else {
      setRegionType(4);
    }
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

    mSbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float value = progress / 1000f + 1;
        if (((int) value) == 10) {
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

    requestFollowCircles();
  }

  private void setTopBarTitle() {
    if (mFeedFragment.getRegionType() == 4 || mFeedFragment.getRegionType() == 3) {
      getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.tb_distance));
    } else {
      getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.tb_drawer));
    }
    getTopBar().getAbLeft().setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (getTopBar().getTitleBarSelectedIndex() == 0) {
              mLlFollowCircles.setVisibility(
                  mLlFollowCircles.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
        mLlFollowCircles.setVisibility(View.GONE);
        mLlDistanceSelector.setVisibility(View.GONE);
        if (position == 0) {
          setRegionType(0);
        } else if (position == 1) {
          clearFollowCircleViews();
          setRegionType(4);
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
    setTopBarTitle();
    if (!hidden) {
      mFeedFragment.onHiddenChanged(false);
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
      mSbDistance.setProgress(9000);
    } else if (event.getRegion() == 4) {
      mSbDistance.setProgress(event.getDistance());
    }
  }

  @Subscribe
  public void onCircleResponseEvent(CircleResponseEvent event) {
  }

  @Subscribe
  public void onCurrentCircleResponseEvent(CurrentCircleResponseEvent event)  {
    if (event.getCircle() != null) {
      mLlSetCurrent.setVisibility(View.GONE);
      mLlCurrent.setVisibility(View.VISIBLE);
      mTvCurrent.setText(event.getCircle().shortName);
      mTvCurrent.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!v.isSelected()) {
            clearFollowCircleViews();
            v.setSelected(true);
            setRegionType(0);
            mLlFollowCircles.setVisibility(View.GONE);
            getTopBar().setTitleTabSelected(0);
            getTopBar().setTitleTabText(0, "在职");
            getTopBar().setSubTitle("");
          }
        }
      });
    }
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

    mFeedFragment.requestFeeds(circleId);
    mHotFeedFragment.requestFeeds(circleId);
    mFriendsFeedFragment.requestFeeds(circleId);

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

  private void requestFollowCircles() {
    U.request("follow_circle_list", new OnResponse2<FollowCircleListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FollowCircleListResponse response) {

        if (response.object.size() > 0) {
          mLlAddFollow.setVisibility(View.GONE);
        }

        if (response.object.size() / 2 == 1) {
          response.object.add(null);
        }
        for (int i = 0, size = response.object.size(); i < size; i += 2) {
          buildFollowCircleRow(new FollowCircle[]{
              response.object.get(i),
              response.object.get(i + 1)
          });
        }
      }
    }, FollowCircleListResponse.class);
  }

  private void buildFollowCircleRow(final FollowCircle[] circles) {
    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
    linearLayout.setBackgroundColor(Color.WHITE);

    int p = U.dp2px(8);
    if (circles[0] != null) {
      TextView textView = new TextView(getActivity());
      textView.setText(circles[0].factoryName);
      textView.setTag(circles[0]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
      params.setMargins(p, p, p, p);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));

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
            mLlFollowCircles.setVisibility(View.GONE);
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    }
    if (circles[1] != null) {
      TextView textView = new TextView(getActivity());
      textView.setText(circles[1].factoryName);
      textView.setTag(circles[1]);
      textView.setGravity(Gravity.CENTER);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
      params.setMargins(p, p, p, p);
      textView.setLayoutParams(params);

      textView.setBackgroundResource(R.drawable.border_outline_secondary_dark_color_btn);
      textView.setTextColor(getResources().getColorStateList(R.color.border_outline_secondary_dark_color_btn_text));

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
            mLlFollowCircles.setVisibility(View.GONE);
          }
        }
      });

      mFollowCircleViews.add(textView);
      linearLayout.addView(textView);
    } else {
      View view = new View(getActivity());
      view.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1));
      linearLayout.addView(view);
    }

    mLlFollowCircles.addView(linearLayout);
  }

  private void clearFollowCircleViews() {
    mTvCurrent.setSelected(false);
    for(View view : mFollowCircleViews) {
      view.setSelected(false);
    }
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
