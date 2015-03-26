/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.explore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class ExploreFragment extends BaseFragment {


  @InjectView(R.id.fl_daily_picks)
  public FrameLayout mFlDailyPicksHead;

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
        String.format("http://c.lanmeiquan.com/activity/blueStar.do?userid=%s&token=%s",
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

    ((TextView) mFlDailyPicksHead.findViewById(R.id.tv_head)).setText("每日精选");

    requestTags();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
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
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
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
    }
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
              if (i != 0)  {
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

  private void setTagsEmpty() {
    TextView textView = new TextView(getActivity());
    textView.setText("没有精选");
    textView.setTextColor(getResources().getColor(R.color.apptheme_primary_grey_color_200));
    mLlTags.addView(textView);
  }
}