/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.event.ProfileFilledEvent;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.RoundedButton;

import java.io.File;

/**
 */
@Layout(R.layout.activity_profile_fill)
@TopTitle(R.string.profile_fill)
public class ProfileFillActivity extends BaseActivity {

  public static void start(Context context, boolean fromRegister) {
    Intent intent = new Intent(context, ProfileFillActivity.class);
    intent.putExtra("fromRegister", fromRegister);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.rg_gender)
  public RadioGroup mRgGender;

  @InjectView(R.id.et_nickname)
  public EditText mEtNickname;

  @InjectView(R.id.aiv_portrait)
  public AsyncImageView mAivPortrait;

  @InjectView(R.id.progress_bar)
  public ProgressBar mProgressBar;

  @InjectView(R.id.rb_submit)
  public RoundedButton mRbSubmit;

  private CameraUtil mCameraUtil;

  private boolean mFromRegister;

  private String mPortraitUrl;

  @OnClick(R.id.fl_upload_portrait)
  public void onFlUploadClicked() {
    mCameraUtil.showCameraDialog();
  }

  @OnTextChanged(R.id.et_nickname)
  public void onEtNicknameTextChanged(CharSequence cs) {
    if (cs.length() == 0) {
      mRbSubmit.setEnabled(false);
    } else {
      mRbSubmit.setEnabled(true);
    }
  }

  @OnClick(R.id.rb_submit)
  public void onRbSubmit() {
    mRbSubmit.setEnabled(false);
    showProgressBar(true);
    Utils.updateProfile(mPortraitUrl,
        mEtNickname.getText().toString(),
        mRgGender.getCheckedRadioButtonId() == R.id.rb_male ? "男" : "女",
        null,
        null,
        null,
        null,
        null,
        new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {
            mRbSubmit.setEnabled(true);
            hideProgressBar();
          }

          @Override
          public void onResponse(Response response) {
            mRbSubmit.setEnabled(true);
            hideProgressBar();
            if (RESTRequester.responseOk(response)) {
              if (mFromRegister) {
                BaseCirclesActivity.startSelect(ProfileFillActivity.this, false);
              } else {
                U.getBus().post(new ProfileFilledEvent());
                finish();
              }
            }
          }
        });
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mFromRegister = getIntent().getBooleanExtra("fromRegister", false);

    if (mFromRegister) {
      getTopBar().getAbRight().setText(getString(R.string.skip));
      getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          BaseCirclesActivity.startSelect(ProfileFillActivity.this, false);
          finish();
        }
      });
    } else {
      getTopBar().getAbRight().hide();
    }

    getTopBar().getAbLeft().hide();

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        mAivPortrait.setUrl(path);
        ImageUtils.asyncUpload(new File(path));
        mProgressBar.setVisibility(View.VISIBLE);
      }
    });
  }

  @Subscribe
  public void onImageUploadEvent(ImageUtils.ImageUploadedEvent event) {
    mProgressBar.setVisibility(View.INVISIBLE);
    mPortraitUrl = event.getUrl();
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onBackPressed() {
    if (!mFromRegister) {
      super.onBackPressed();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
  }
}