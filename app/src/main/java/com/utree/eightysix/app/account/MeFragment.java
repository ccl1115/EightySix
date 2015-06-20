/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.settings.MainSettingsActivity;

/**
 */
public class MeFragment extends BaseFragment {

  @OnClick(R.id.tv_settings)
  public void onTvSettingsClicked() {
    startActivity(new Intent(getActivity(), MainSettingsActivity.class));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_me, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    getBaseActivity().setTopTitle("我");
    getBaseActivity().setTopSubTitle("");
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      getBaseActivity().setTopTitle("我");
      getBaseActivity().setTopSubTitle("");
    }
  }
}