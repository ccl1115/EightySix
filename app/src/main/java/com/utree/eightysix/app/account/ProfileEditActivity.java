/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.event.*;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.hometown.SetHometownFragment;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.Calendar;

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

  private Calendar mCalendar;

  private String mSignature;

  @OnClick(R.id.ll_portrait)
  public void onLlPortraitClicked() {
    startActivity(new Intent(this, AvatarsActivity.class));
  }

  @OnClick(R.id.aiv_portrait)
  public void onAivPortraitClicked() {
    AvatarViewerActivity.start(this, 0, -1);
  }

  @OnClick(R.id.ll_name)
  public void onLlNameClicked() {
    NameEditActivity.start(this, mTvName.getText().toString());
  }

  @OnClick(R.id.ll_gender)
  public void onLlGenderClicked() {
    final ThemedDialog dialog = new ThemedDialog(this);

    dialog.setTitle("修改性别");

    View view = LayoutInflater.from(this).inflate(R.layout.widget_select_gender, null);

    RadioButton male = (RadioButton) view.findViewById(R.id.rb_male);
    RadioButton female = (RadioButton) view.findViewById(R.id.rb_female);
    if ("男".equals(mTvGender.getText())) {
      male.setChecked(true);
    } else if ("女".equals(mTvGender.getText())) {
      female.setChecked(true);
    }
    female.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();

        Utils.updateProfile(null, null, "女", null, null, null, null, null,
            new OnResponse2<Response>() {
              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  mTvGender.setText("女");
                  U.getBus().post(new GenderUpdatedEvent("女"));
                }
              }

              @Override
              public void onResponseError(Throwable e) {

              }
            });

      }
    });
    male.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();

        Utils.updateProfile(null, null, "男", null, null, null, null, null,
            new OnResponse2<Response>() {
              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  mTvGender.setText("男");
                  U.getBus().post(new GenderUpdatedEvent("男"));
                }
              }

              @Override
              public void onResponseError(Throwable e) {

              }
            });
      }
    });

    dialog.setContent(view);

    dialog.show();
  }

  @OnClick(R.id.ll_birthday)
  public void onLlBirthdayClicked() {
    BirthdayEditActivity.start(this, mCalendar);
  }

  @OnClick(R.id.ll_current)
  public void onLlCurrentClicked() {
    BaseCirclesActivity.startSelect(this, true);
  }

  @OnClick(R.id.ll_hometown)
  public void onLlHometownClicked() {
    SetHometownFragment fragment = new SetHometownFragment();
    getSupportFragmentManager().beginTransaction()
        .add(R.id.content, fragment)
        .commit();
  }

  @OnClick(R.id.ll_signature)
  public void onLlSignatureClicked() {
    SignatureEditActivity.start(this, mSignature);
  }

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
        mCalendar = Calendar.getInstance();
        if (response.object.birthday == -1) {
          mTvBirthday.setText("未设置");
        } else {
          mCalendar.setTimeInMillis(response.object.birthday);
          mTvBirthday.setText(String.format("%d岁 %s %s",
              Utils.computeAge(Calendar.getInstance(), mCalendar),
              TimeUtil.getDate(mCalendar),
              response.object.constellation));

        }
        mTvCurrent.setText(response.object.workinFactoryName);
        mTvHometown.setText(response.object.hometown);
        mSignature = response.object.signature;
        if (TextUtils.isEmpty(mSignature)) {
          mTvSignature.setText("没有设置个性签名");
        } else {
          mTvSignature.setText(mSignature);
        }
      }
    }, ProfileResponse.class, (Integer) null);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onNameUpdatedEvent(NameUpdatedEvent event) {
    mTvName.setText(event.getName());
  }

  @Subscribe
  public void onBirdayUpdatedEvent(BirthdayUpdatedEvent event) {
    Calendar calendar = event.getCalendar();
    mTvBirthday.setText(String.format("%d岁 %s %s",
        Utils.computeAge(Calendar.getInstance(), calendar),
        TimeUtil.getDate(calendar),
        event.getConstellation()));
  }

  @Subscribe
  public void onCurrentCircleNameUpdatedEvent(CurrentCircleNameUpdatedEvent event) {
    mTvCurrent.setText(event.getName());
  }

  @Subscribe
  public void onHometownUpdatedEvent(HometownUpdatedEvent event) {
    mTvHometown.setText(event.getName());
  }

  @Subscribe
  public void onSignatureUpdateEvent(SignatureUpdatedEvent event) {
    mTvSignature.setText(event.getText());
  }
}
