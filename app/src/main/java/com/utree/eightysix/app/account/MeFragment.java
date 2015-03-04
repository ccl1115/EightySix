/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;

/**
 */
public class MeFragment extends BaseFragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_me, container, false);
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