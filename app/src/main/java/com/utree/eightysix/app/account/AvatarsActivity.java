/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import butterknife.InjectView;
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
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.ThemedDialog;

import java.io.File;

/**
 */

@Layout(R.layout.activity_avatars)
@TopTitle(R.string.avatars)
public class AvatarsActivity extends BaseActivity {

  public static void start(Context context, int viewId) {
    Intent intent = new Intent(context, AvatarsActivity.class);
    intent.putExtra("viewId", viewId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

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

  @InjectViews({
      R.id.iv_selected_1,
      R.id.iv_selected_2,
      R.id.iv_selected_3,
      R.id.iv_selected_4,
      R.id.iv_selected_5,
      R.id.iv_selected_6,
      R.id.iv_selected_7,
      R.id.iv_selected_8,
      R.id.iv_selected_9
  })
  public ImageView[] mIvSelected;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private CameraUtil mCameraUtil;
  private String mFileHash;
  private Integer mViewId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        File file = new File(path);
        mFileHash = IOUtils.fileHash(file);
        ImageUtils.asyncUpload(file, 50);
      }
    });

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    int viewId = getIntent().getIntExtra("viewId", -1);
    mViewId = viewId == -1 ? null : viewId;

    requestAvatars();
  }

  private void requestAvatars() {
    U.request("user_avatars", new OnResponse2<UserAvatarsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final UserAvatarsResponse response) {

        if (RESTRequester.responseOk(response)) {
          final int size = response.object == null ? 0 : response.object.size();

          if (size == 0) {
            if (mViewId == null) {
              mCameraUtil.showCameraDialog();
            } else {
              mRstvEmpty.setVisibility(View.VISIBLE);
            }
          }

          for (int i = 0; i < size; i++) {
            final UserAvatarsResponse.Avatar avatar = response.object.get(i);
            if (!TextUtils.isEmpty(avatar.avatar)) {
              if (avatar.beUsed == 1) {
                mIvSelected[i].setVisibility(View.VISIBLE);
              } else {
                mIvSelected[i].setVisibility(View.GONE);
              }
              mAivAvatars[i].setUrl(avatar.avatar);
              final int finalI = i;
              mAivAvatars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  AvatarViewerActivity.start(v.getContext(), finalI, mViewId == null ? -1 : mViewId);
                }
              });
              mAivAvatars[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                  if (avatar.beUsed == 1) {
                    return false;
                  } else {
                    showMenuDialog(avatar.avatar);
                    return true;
                  }
                }
              });
            }
          }

          if (size < 9) {
            if (mViewId == null) {
              mAivAvatars[size].setImageResource(R.drawable.ic_avatar_add);
              mIvSelected[size].setVisibility(View.GONE);
              mAivAvatars[size].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  mCameraUtil.showCameraDialog();
                }
              });
            } else {
              mAivAvatars[size].setImageResource(0);
              mIvSelected[size].setVisibility(View.GONE);
            }

            for (int i = size + 1; i < 9; i++) {
              mAivAvatars[i].setImageResource(0);
              mIvSelected[size].setVisibility(View.GONE);
            }
          }
        }
      }
    }, UserAvatarsResponse.class, mViewId);
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

  private void showMenuDialog(final String avatar) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setItems(
        new String[]{
            "设置为当前头像",
            "删除此头像"
        },
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case 0:
                showSelectConfirmDialog(avatar);
                break;
              case 1:
                showDeleteDialog(avatar);
                break;
            }
          }
        });

    builder.show();
  }

  private void showDeleteDialog(final String avatar) {
    final ThemedDialog dialog = new ThemedDialog(this);

    dialog.setTitle("确认删除此头像");

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        U.request("user_avatar_del", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              requestAvatars();
            }
          }
        }, Response.class, avatar);
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