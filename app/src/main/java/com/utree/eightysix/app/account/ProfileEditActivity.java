/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AsyncImageView;

/**
 */
@Layout(R.layout.activity_profile_edit)
@TopTitle(R.string.profile)
public class ProfileEditActivity extends BaseActivity {

  @InjectView(R.id.aiv_portrait)
  public AsyncImageView mAivPortrait;

  @InjectView(R.id.tv_name)
  public TextView mTvName;

  @InjectView(R.id.tv_gender)
  public TextView mTvGender;

  @InjectView(R.id.tv_birthday)
  public TextView mTvBirthday;

  @InjectView(R.id.tv_current)
  public TextView mTvCurrent;

  @InjectView(R.id.tv_hometown)
  public TextView mTvHometown;

  @InjectView(R.id.tv_signature)
  public TextView mTvSignature;



  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    U.request("profile", new OnResponse2<ProfileResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(ProfileResponse response) {
        mAivPortrait.setUrl(response.object.avatar);
        mTvName.setText(response.object.userName);
        mTvGender.setText(response.object.sex);
        mTvBirthday.setText(response.object.birthday);
        mTvCurrent.setText(response.object.workinFactoryName);
        mTvHometown.setText(response.object.hometown);
        if (TextUtils.isEmpty(response.object.signature)) {
          mTvSignature.setText("没有设置个性签名");
        } else {
          mTvSignature.setText(response.object.signature);
        }
      }
    }, ProfileResponse.class, (Integer) null);

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}