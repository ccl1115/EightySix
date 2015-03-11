package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.account.event.BirthdayUpdatedEvent;
import com.utree.eightysix.app.account.event.GenderUpdatedEvent;
import com.utree.eightysix.app.account.event.NameUpdatedEvent;
import com.utree.eightysix.app.account.event.PortraitUpdatedEvent;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;

import java.io.File;

/**
 */
public class ProfileFragment extends BaseFragment {

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshLayout;

  @InjectView(R.id.tv_name)
  public TextView mTvName;

  @InjectView(R.id.tv_signature)
  public TextView mTvSignature;

  @InjectView(R.id.tv_gender)
  public TextView mTvGender;

  @InjectView(R.id.tv_birthday)
  public TextView mTvBirthday;

  @InjectView(R.id.tv_circle_name)
  public TextView mTvCircleName;

  @InjectView(R.id.tv_age)
  public TextView mTvAge;

  @InjectView(R.id.tv_constellation)
  public TextView mTvConstellation;

  @InjectView(R.id.tv_hometown)
  public TextView mTvHometown;

  @InjectView(R.id.aiv_portrait)
  public AsyncImageViewWithRoundCorner mAivPortrait;

  @InjectView(R.id.aiv_bg)
  public AsyncImageView mAivBg;

  private CameraUtil mCameraUtil;
  private boolean mIsVisitor;

  @OnClick(R.id.rb_change_bg)
  public void onRbChageBgClicked() {
    mCameraUtil.showCameraDialog();
  }

  @OnClick(R.id.tv_settings)
  public void onTvSettingsClicked() {
    startActivity(new Intent(getActivity(), MainSettingsActivity.class));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    if (getArguments() != null) {
      mIsVisitor = getArguments().getBoolean("isVisitor", false);
    }

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        ImageUtils.asyncUpload(new File(path));
      }
    });

    mRefreshLayout.setColorSchemeResources(R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed);

    mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        requestProfile(null);
      }

      @Override
      public void onDrag() {
        getBaseActivity().showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        getBaseActivity().hideRefreshIndicator();
      }
    });

    requestProfile(null);
  }

  public void requestProfile(Integer userId) {
    getBaseActivity().showRefreshIndicator(true);
    U.request("profile", new OnResponse2<ProfileResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideRefreshIndicator();
        mRefreshLayout.setRefreshing(false);
      }

      @Override
      public void onResponse(ProfileResponse response) {
        getBaseActivity().hideRefreshIndicator();
        mRefreshLayout.setRefreshing(false);
        if (RESTRequester.responseOk(response)) {
          mTvName.setText(response.object.userName);
          mTvAge.setText(String.valueOf(response.object.age));
          if (response.object.birthday == -1) {
            mTvBirthday.setVisibility(View.GONE);
          } else {
            mTvBirthday.setVisibility(View.VISIBLE);
            mTvBirthday.setText(TimeUtil.getDate(response.object.birthday));
          }
          mTvGender.setText(response.object.sex);
          mTvConstellation.setText(response.object.constellation);
          mAivBg.setUrl(response.object.background);
          mAivPortrait.setUrl(response.object.avatar);
          if (TextUtils.isEmpty(response.object.signature)) {
            mTvSignature.setText("还没有设置签名");
          } else {
            mTvSignature.setText(response.object.signature);
          }
          mTvCircleName.setText(response.object.workinFactoryName);
          mTvHometown.setText(response.object.hometown);
        }
      }
    }, ProfileResponse.class, userId);

  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    updateTopTitle();
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      updateTopTitle();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
  }

  @Subscribe
  public void onImageUploadEvent(final ImageUtils.ImageUploadedEvent event) {
    Utils.updateProfile(null, null, null, null, null, event.getUrl(), null, new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          mAivBg.setUrl(event.getUrl());
        }
      }
    });
  }

  @Subscribe
  public void onPortraitUpdatedEvent(PortraitUpdatedEvent event) {
    mAivPortrait.setUrl(event.getUrl());
  }

  @Subscribe
  public void onGenderUpdatedEvent(GenderUpdatedEvent event) {
    mTvGender.setText(event.getGender());
  }

  @Subscribe
  public void onNameUpdatedEvent(NameUpdatedEvent event) {
    mTvName.setText(event.getName());
  }

  @Subscribe
  public void onBirthdayUpdatedEvent(BirthdayUpdatedEvent event) {
    mTvBirthday.setText(TimeUtil.getDate(event.getCalendar()));
  }

  private void updateTopTitle() {
    getBaseActivity().setTopTitle("我");
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().getTopBar().getAbRight().hide();
    getBaseActivity().getTopBar().getAbLeft().hide();
    if (mIsVisitor) {
      getBaseActivity().getTopBar().getAbRight().setText("拉黑");
      getBaseActivity().getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });
    } else {
      getBaseActivity().getTopBar().getAbRight().setText("编辑");
      getBaseActivity().getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startActivity(new Intent(getActivity(), ProfileEditActivity.class));
        }
      });
    }
  }
}