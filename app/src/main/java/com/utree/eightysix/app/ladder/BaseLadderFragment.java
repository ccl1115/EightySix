/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.ladder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.response.BaseLadderResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
public abstract class BaseLadderFragment extends BaseFragment {

  @InjectView(R.id.alv_ladder)
  public AdvancedListView mAlvLadder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_base_ladder, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    U.request(getApi(), new OnResponse2<BaseLadderResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(BaseLadderResponse response) {
        if (RESTRequester.responseOk(response)) {
          mAlvLadder.setAdapter(new BaseLadderAdapter(response));
        }
      }
    }, BaseLadderResponse.class);
  }

  protected abstract String getApi();

}