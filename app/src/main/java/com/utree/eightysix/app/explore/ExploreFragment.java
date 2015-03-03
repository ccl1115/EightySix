/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.explore;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.hometown.HometownTabFragment;
import com.utree.eightysix.app.web.BaseWebActivity;

/**
 */
public class ExploreFragment extends BaseFragment {


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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_explore, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    getBaseActivity().setTopTitle("发现");
    getBaseActivity().setTopSubTitle("");
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    getBaseActivity().setTopTitle("发现");
    getBaseActivity().setTopSubTitle("");
  }
}