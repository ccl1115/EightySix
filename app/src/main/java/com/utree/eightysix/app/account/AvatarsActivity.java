/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import butterknife.InjectViews;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.event.PortraitUpdatedEvent;
import com.utree.eightysix.response.UserAvatarsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.ThemedDialog;

import java.io.File;

/**
 */

@Layout(R.layout.activity_avatars)
@TopTitle(R.string.avatars)
public class AvatarsActivity extends BaseActivity {

  @InjectViews({
      R.id.aiv_portrait_1,
      R.id.aiv_portrait_2,
      R.id.aiv_portrait_3,
      R.id.aiv_portrait_4,
      R.id.aiv_portrait_5,
      R.id.aiv_portrait_6,
      R.id.aiv_portrait_7,
      R.id.aiv_portrait_8,
      R.id.aiv_portrait_9
  })
  public AsyncImageView[] mAivAvatars;

  private CameraUtil mCameraUtil;
  private String mFileHash;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        File file = new File(path);
        mFileHash = IOUtils.fileHash(file);
        ImageUtils.asyncUpload(file);
      }
    });

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));

    requestAvatars();
  }

  private void requestAvatars() {
    U.request("user_avatars", new OnResponse2<UserAvatarsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(UserAvatarsResponse response) {

        if (RESTRequester.responseOk(response)) {
          int size = response.object.size();
          for (int i = 0; i < size; i++) {
            final String avatar = response.object.get(i).avatar;
            if (!TextUtils.isEmpty(avatar)) {
              mAivAvatars[i].setUrl(avatar);
              final int finalI = i;
              mAivAvatars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  AvatarViewerActivity.start(v.getContext(), finalI);
                }
              });
              mAivAvatars[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                  showSelectConfirmDialog(avatar);
                  return true;
                }
              });
            }
          }

          if (size < 9) {
            mAivAvatars[size].setImageResource(R.drawable.ic_add);
            mAivAvatars[size].setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                mCameraUtil.showCameraDialog();
              }
            });

            for (int i = size + 1; i < 9; i++) {
              mAivAvatars[i].setImageResource(0);
            }
          }
        }
      }
    }, UserAvatarsResponse.class);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
  }

  @Subscribe
  public void onImageUploadedEvent(final ImageUtils.ImageUploadedEvent event) {
    if (event.getHash().equals(mFileHash)) {
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
  }

  @Subscribe
  public void onPortraitUpdatedEvent(PortraitUpdatedEvent event) {
    requestAvatars();
  }

  private void showSelectConfirmDialog(final String avatar) {
    final ThemedDialog dialog = new ThemedDialog(this);

    dialog.setTitle("确认更改此图片为头像");

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.updateProfile(avatar, null, null, null, null, null, null, null,
            new OnResponse2<Response>() {
              @Override
              public void onResponseError(Throwable e) {

              }

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  U.getBus().post(new PortraitUpdatedEvent(avatar));
                }
              }
            });
        dialog.dismiss();
      }
    });

    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.show();
  }
}