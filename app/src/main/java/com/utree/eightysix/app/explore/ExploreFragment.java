/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.explore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.account.AddFriendActivity;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.dp.DailyPicksActivity;
import com.utree.eightysix.app.feed.FeedsSearchActivity;
import com.utree.eightysix.app.hometown.HometownTabFragment;
import com.utree.eightysix.app.topic.TopicActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.response.TopicListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.IndicatorView;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.TagView;

import java.util.List;

/**
 */
public class ExploreFragment extends BaseFragment {


  @InjectView(R.id.fl_daily_picks)
  public FrameLayout mFlDailyPicksHead;

  @InjectView(R.id.fl_topic)
  public FrameLayout mFlTopicsHead;

  @InjectView(R.id.in_topics)
  public IndicatorView mInTopics;

  @InjectView(R.id.vp_topics)
  public ViewPager mVpTopics;

  @InjectView(R.id.ll_tags)
  public LinearLayout mLlTags;

  @OnClick(R.id.tv_circles)
  public void onTvCirclesClicked() {
    BaseCirclesActivity.startMyCircles(getActivity());
  }

  @OnClick(R.id.tv_hometown)
  public void onTvHometownClicked() {
    FragmentHolder.start(getActivity(), HometownTabFragment.class);
  }

  @OnClick(R.id.tv_snapshot)
  public void onTvSnapshotClicked() {
    BaseCirclesActivity.startSnapshot(getActivity());
  }

  @OnClick(R.id.tv_blue_star)
  public void onTvBlueStarClicked() {
    BaseWebActivity.start(getBaseActivity(), "蓝星商城",
        String.format("%s/activity/blueStar.do?userid=%s&token=%s",
            U.getConfig("api.host"),
            Account.inst().getUserId(),
            Account.inst().getToken()));
  }

  @OnClick(R.id.tv_search)
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
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    updateTopBar();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      updateTopBar();
    }
  }

  private void updateTopBar() {
    getBaseActivity().setTopTitle("发现");
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_add));
    getBaseActivity().getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
        getActivity().startActivity(intent);
      }
    });
    getBaseActivity().getTopBar().getAbLeft().hide();
    getBaseActivity().showTopBar(true);
  }


  private void requestTags() {
    U.request("daily_picks_tags", new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        setTagsEmpty();
      }

      @Override
      public void onResponse(TagsResponse response) {
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
    }, TagsResponse.class);
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

  private void buildNewestTopics(final List<Topic> topics) {
    mVpTopics.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return topics.size();
      }

      @Override
      public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.page_newest_topic, container, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setData(topics.get(position));
        container.addView(view);
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
    });

    mInTopics.setCount(3);

    mVpTopics.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInTopics.setPosition(position + positionOffset);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_title)
    public TextView mTvTitle;

    @InjectView(R.id.tv_text)
    public TextView mTvText;

    @InjectView(R.id.tv_tag_1)
    public TagView mTvTag1;

    @InjectView(R.id.tv_tag_2)
    public TagView mTvTag2;

    @InjectView(R.id.tv_count)
    public TextView mTvCount;

    @InjectView(R.id.aiv_bg)
    public AsyncImageViewWithRoundCorner mAivBg;

    @InjectView(R.id.rb_bg)
    public RoundedButton mRbBg;

    private Topic mTopic;

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
      view.setOnClickListener(new View.OnClickListener() {
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

      if (TextUtils.isEmpty(topic.bgUrl)) {
        mAivBg.setUrl(null);
        mRbBg.setBackgroundColor(ColorUtil.strToColor(topic.bgColor));
      } else {
        mAivBg.setUrl(topic.bgUrl);
        mRbBg.setBackgroundColor(0);
      }

      List<Tag> tags = topic.tags;
      for (int i = 0; i < tags.size(); i++) {
        Tag tag = tags.get(i);
        switch (i) {
          case 0:
            mTvTag1.setText("#" + tag.content);
            break;
          case 1:
            mTvTag2.setText("#" + tag.content);
            break;
        }
      }
    }
  }
}