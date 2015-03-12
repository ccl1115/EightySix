/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.event.*;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.hometown.SetHometownFragment;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.ThemedDialog;

import java.io.File;
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

  private CameraUtil mCameraUtil;
  private Calendar mCalendar;

  @OnClick(R.id.ll_portrait)
  public void onLlPortraitClicked() {
    mCameraUtil.showCameraDialog();
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
    dialog.setContent(view);

    ((RadioGroup) view).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        String gender = null;
        if (checkedId == R.id.rb_male) {
          gender = "男";
        } else if (checkedId == R.id.rb_female) {
          gender = "女";
        }

        dialog.dismiss();

        final String finalGender = gender;
        Utils.updateProfile(null, null, gender, null, null, null, null, null,
            new OnResponse2<Response>() {
              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  mTvGender.setText(finalGender);
                  U.getBus().post(new GenderUpdatedEvent(finalGender));
                }
              }

              @Override
              public void onResponseError(Throwable e) {

              }
            });
      }
    });

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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        ImageUtils.asyncUpload(new File(path));
      }
    });

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
              Utils.Constellation.get(mCalendar)));

        }
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
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
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
  public void onImageUploadEvent(final ImageUtils.ImageUploadedEvent event) {
    mAivPortrait.setUrl(event.getUrl());
    Utils.updateProfile(event.getUrl(), null, null, null, null, null, null, null,
        new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              U.getBus().post(new PortraitUpdatedEvent(event.getUrl()));
            }
          }
        });
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
        Utils.Constellation.get(calendar)));
  }

  @Subscribe
  public void onCurrentCircleNameUpdatedEvent(CurrentCircleNameUpdatedEvent event) {
    mTvCurrent.setText(event.getName());
  }

  public void onHometownUpdatedEvent(HometownUpdatedEvent event) {
    mTvHometown.setText(event.getName());
  }
}