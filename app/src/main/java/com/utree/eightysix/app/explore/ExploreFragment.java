/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.explore;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.app.account.AddFriendActivity.GetInviteCodeResponse;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.dp.DailyPicksActivity;
import com.utree.eightysix.app.feed.FeedsSearchActivity;
import com.utree.eightysix.app.hometown.HometownTabFragment;
import com.utree.eightysix.app.ladder.LadderActivity;
import com.utree.eightysix.app.topic.TopicActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.response.TopicListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.IndicatorView;
import com.utree.eightysix.widget.ListPopupWindowCompat;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TagView;
import com.utree.eightysix.widget.ThemedDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ExploreFragment extends BaseFragment {

  private static final long REFRESH_INTERNAL = 60000 * 30;

  @InjectView (R.id.fl_daily_picks)
  public FrameLayout mFlDailyPicksHead;

  @InjectView (R.id.fl_topic)
  public FrameLayout mFlTopicsHead;

  @InjectView (R.id.in_topics)
  public IndicatorView mInTopics;

  @InjectView (R.id.vp_topics)
  public ViewPager mVpTopics;

  @InjectView (R.id.ll_tags)
  public LinearLayout mLlTags;

  @InjectView (R.id.ll_ladder)
  public LinearLayout mLlLadder;

  @InjectView (R.id.tv_ladder_new)
  public TextView mTvLadderNew;

  private Handler mHandler = new Handler();

  private long mLastRefreshTimestamp;

  private ListPopupWindowCompat mListPopupWindow;

  private QRCodeScanFragment mQRCodeScanFragment;

  private Runnable mCarousel = new Runnable() {

    @Override
    public void run() {
      if (mVpTopics == null || mVpTopics.getAdapter() == null) return;
      int currentItem = mVpTopics.getCurrentItem();
      if (currentItem == mVpTopics.getAdapter().getCount() - 1) {
        mVpTopics.setCurrentItem(0);
      } else {
        mVpTopics.setCurrentItem(currentItem + 1);
      }

      mHandler.postDelayed(mCarousel, 5000);
    }
  };

  @OnClick ({R.id.ll_tags, R.id.fl_daily_picks})
  public void onLlTagsClicked(View v) {
    DailyPicksActivity.start(v.getContext(), 0);
  }

  @OnClick (R.id.tv_circles)
  public void onTvCirclesClicked() {
    BaseCirclesActivity.startMyCircles(getActivity());
  }

  @OnClick (R.id.tv_hometown)
  public void onTvHometownClicked() {
    FragmentHolder.start(getActivity(), R.style.AppTheme_Light, HometownTabFragment.class, null);
  }

  @OnClick (R.id.tv_ladder)
  public void onTvLadderClicked() {
    mTvLadderNew.setVisibility(View.GONE);
    startActivity(new Intent(getActivity(), LadderActivity.class));
  }

  @OnClick (R.id.tv_snapshot)
  public void onTvSnapshotClicked() {
    BaseCirclesActivity.startSnapshot(getActivity());
  }

  @OnClick (R.id.tv_blue_star)
  public void onTvBlueStarClicked() {
    BaseWebActivity.start(getBaseActivity(), "蓝星商城",
        String.format("%s/activity/blueStar.do?userid=%s&token=%s",
            U.getConfig("api.host"),
            Account.inst().getUserId(),
            Account.inst().getToken()));
  }

  @OnClick (R.id.tv_search)
  public void onTvSearchClicked() {
    startActivity(new Intent(getActivity(), FeedsSearchActivity.class));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_explore, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    if (Env.firstRun("ladder_new")) {
      mTvLadderNew.setVisibility(View.VISIBLE);
    }

    TextView picksHead = (TextView) mFlDailyPicksHead.findViewById(R.id.tv_head);
    picksHead.setText("每日精选");
    picksHead.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_head_daily_picks), null, null, null);
    picksHead.setCompoundDrawablePadding(U.dp2px(8));

    TextView topicsHead = (TextView) mFlTopicsHead.findViewById(R.id.tv_head);
    topicsHead.setText("最新话题");
    topicsHead.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_head_newest_topic), null, null, null);
    topicsHead.setCompoundDrawablePadding(U.dp2px(8));

    TextView viewById = ((TextView) mFlTopicsHead.findViewById(R.id.tv_right));
    viewById.setVisibility(View.VISIBLE);
    viewById.setText("更多");
    viewById.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        TopicListActivity.start(v.getContext());
      }
    });

    requestTags();
    requestNewestTopics();
    mLastRefreshTimestamp = System.currentTimeMillis();

    Sync sync = U.getSyncClient().getSync();
    if (sync != null) {
      String userRankSwitch = sync.userRankSwitch;
      if ("on".equals(userRankSwitch)) {
        mLlLadder.setVisibility(View.VISIBLE);
      } else if ("off".equals(userRankSwitch)) {
        mLlLadder.setVisibility(View.GONE);
      }
    }

    updateTopBar();
    startCarousel();
  }

  @Subscribe
  public void onSyncEvent(Sync sync) {
    String userRankSwitch = sync.userRankSwitch;
    if ("on".equals(userRankSwitch)) {
      mLlLadder.setVisibility(View.VISIBLE);
    } else if ("off".equals(userRankSwitch)) {
      mLlLadder.setVisibility(View.GONE);
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      if (System.currentTimeMillis() - mLastRefreshTimestamp > REFRESH_INTERNAL) {
        requestTags();
        requestNewestTopics();
      }

      updateTopBar();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    if (!isHidden()) {
      if (System.currentTimeMillis() - mLastRefreshTimestamp > REFRESH_INTERNAL) {
        requestTags();
        requestNewestTopics();
      }
    }
  }

  private void updateTopBar() {
    getBaseActivity().setTopTitle("发现");
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().getTopBar().getAbLeft().hide();
    getBaseActivity().showTopBar(true);
    getBaseActivity().hideRefreshIndicator();
  }


  private void requestTags() {
    U.request("daily_picks_tags", new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        setTagsEmpty();
      }

      @Override
      public void onResponse(TagsResponse response) {
        mLlTags.removeAllViews();
        if (RESTRequester.responseOk(response)) {
          int size = response.object.tags.size();
          if (size == 0) {
            setTagsEmpty();
          } else {
            final int padding = U.dp2px(2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < size; i++) {
              RoundedButton roundedButton = new RoundedButton(getActivity());
              if (i != 0) {
                roundedButton.setBackgroundColor(0xfffd9e16);
              }
              roundedButton.setText(response.object.tags.get(i).content);
              roundedButton.setTextSize(14);
              params.rightMargin = U.dp2px(13);
              roundedButton.setLayoutParams(params);
              roundedButton.setPadding(padding << 2, padding, padding << 2, padding);
              final int finalI = i;
              roundedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  DailyPicksActivity.start(getActivity(), finalI);
                }
              });

              mLlTags.addView(roundedButton);
            }
          }

        } else {
          setTagsEmpty();
        }
      }
    }, TagsResponse.class, 1);
  }

  private void requestNewestTopics() {
    U.request("newest_topics", new OnResponse2<TopicListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(TopicListResponse response) {
        if (RESTRequester.responseOk(response)) {
          buildNewestTopics(response.object.newTopic.postTopics.lists);
        }
      }
    }, TopicListResponse.class, 1);
  }

  private void setTagsEmpty() {
    TextView textView = new TextView(getActivity());
    textView.setText("没有精选");
    textView.setTextColor(getResources().getColor(R.color.apptheme_primary_grey_color_200));
    mLlTags.addView(textView);
  }

  private void stopCarousel() {
    mHandler.removeCallbacks(mCarousel);
  }

  private void startCarousel() {
    mHandler.postDelayed(mCarousel, 5000);
  }

  private void buildNewestTopics(final List<Topic> topics) {
    PagerAdapter adapter = new PagerAdapter() {
      @Override
      public int getCount() {
        return topics.size();
      }

      @Override
      public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
            .inflate(R.layout.page_newest_topic, container, false);
        container.addView(view);
        ViewHolder holder = new ViewHolder(view);
        holder.setData(topics.get(position));
        return view;
      }

      @Override
      public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
      }

      @Override
      public boolean isViewFromObject(View view, Object object) {
        return object.equals(view);
      }
    };
    mVpTopics.setAdapter(adapter);

    mInTopics.setCount(adapter.getCount());

    mVpTopics.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInTopics.setPosition(position + positionOffset);
      }

      @Override
      public void onPageSelected(int position) {
        stopCarousel();
        startCarousel();
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  public static class ViewHolder {

    @InjectView (R.id.tv_title)
    public TextView mTvTitle;

    @InjectView (R.id.tv_text)
    public TextView mTvText;

    @InjectView (R.id.tv_tag_1)
    public TagView mTvTag1;

    @InjectView (R.id.tv_tag_2)
    public TagView mTvTag2;

    @InjectView (R.id.tv_count)
    public TextView mTvCount;

    @InjectView (R.id.aiv_bg)
    public AsyncImageViewWithRoundCorner mAivBg;

    @InjectView (R.id.rb_bg)
    public RoundedButton mRbBg;

    @InjectView (R.id.rb_mask)
    public RoundedButton mRbMask;

    private Topic mTopic;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
      mRbBg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          TopicActivity.start(v.getContext(), mTopic);
        }
      });
    }

    public void setData(Topic topic) {
      mTopic = topic;
      mTvTitle.setText(topic.title);
      mTvText.setText(topic.content);
      mTvCount.setText(String.valueOf(topic.postCount) + "条帖子");

      if (TextUtils.isEmpty(topic.title) && TextUtils.isEmpty(topic.content)) {
        mRbMask.setVisibility(View.INVISIBLE);
      } else {
        mRbMask.setVisibility(View.VISIBLE);
      }

      if (TextUtils.isEmpty(topic.bgUrl)) {
        mAivBg.setUrl(null);
        mRbBg.setBackgroundColor(ColorUtil.strToColor(topic.bgColor));
      } else {
        mAivBg.setUrl(topic.bgUrl);
        mRbBg.setBackgroundColor(0);
      }

      List<Tag> tags = topic.tags;
      for (int i = 0; i < tags.size(); i++) {
        final Tag tag = tags.get(i);
        switch (i) {
          case 0:
            mTvTag1.setText("#" + tag.content);
            mTvTag1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                FeedsSearchActivity.start(v.getContext(), tag.content);
              }
            });
            break;
          case 1:
            mTvTag2.setText("#" + tag.content);
            mTvTag2.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                FeedsSearchActivity.start(v.getContext(), tag.content);
              }
            });
            break;
        }
      }
    }
  }
}