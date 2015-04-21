/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.response.UserSetupResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

/**
 */
public class MsgSettingsFragment extends BaseFragment {

  @InjectView(R.id.ll_silent_mode)
  public LinearLayout mLlSilentMode;

  @InjectView(R.id.ll_praise_remind)
  public LinearLayout mLlPraiseRemind;

  @InjectView(R.id.cb_silent_mode)
  public CheckBox mCbSilentMode;

  @InjectView(R.id.cb_praise_remind)
  public CheckBox mCbPraiseRemind;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_msg_settings, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    getTopBar().setTitle("消息设置");

    mCbSilentMode.setChecked(Account.inst().getSilentMode());

    mCbSilentMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Account.inst().setSilentMode(isChecked);
      }
    });

    U.request("user_get_config", new OnResponse2<UserSetupResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(UserSetupResponse response) {
        if (RESTRequester.responseOk(response)) {
          mCbPraiseRemind.setChecked(response.object.praiseNotRemind == 0);

          mCbPraiseRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
              U.request("user_config", new OnResponse2<Response>() {
                @Override
                public void onResponseError(Throwable e) {
                  mCbPraiseRemind.setChecked(!isChecked);
                }

                @Override
                public void onResponse(Response response) {
                  if (!RESTRequester.responseOk(response)) {
                    mCbPraiseRemind.setChecked(!isChecked);
                  }
                }
              }, Response.class, isChecked ? 0 : 1);
            }
          });
        }
      }
    }, UserSetupResponse.class);
  }
}