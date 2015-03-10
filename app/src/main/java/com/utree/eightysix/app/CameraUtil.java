/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.utree.eightysix.R;
import com.utree.eightysix.app.publish.ImageCropActivity;
import com.utree.eightysix.utils.IOUtils;

import java.io.File;

/**
 * For pick photo from gallery or take picture from system camera;
 * <p/>
 * Used by {@link com.utree.eightysix.app.publish.PublishActivity} and {@link com.utree.eightysix.app.chat.ChatActivity}
 */
public class CameraUtil {

  private static final int REQUEST_CODE_CAMERA = 0x1000;
  private static final int REQUEST_CODE_ALBUM = 0x1001;
  private static final int REQUEST_CODE_CROP = 0x1002;
  private BaseActivity mActivity;
  private BaseFragment mFragment;
  private AlertDialog mCameraDialog;
  private boolean mStartCamera;
  private boolean mStartAlbum;
  private File mOutputFile;
  private Callback mCallback;

  private boolean mFixedRatio = true;

  public CameraUtil(BaseActivity activity, Callback callback) {
    mActivity = activity;
    mCallback = callback;
  }

  public CameraUtil(BaseFragment fragment, Callback callback) {
    mFragment = fragment;
    mActivity = fragment.getBaseActivity();
    mCallback = callback;
  }

  /**
   * Default is true
   * @param fixedRatio true if want to fix the ratio to 1:1
   */
  public void setFixedRatioWhenCrop(boolean fixedRatio) {
    mFixedRatio = fixedRatio;
  }

  public void showCameraDialog() {

    if (mCameraDialog == null) {
      mCameraDialog = new AlertDialog.Builder(mActivity).setTitle(R.string.add_photo).setItems(new String[]{
          mActivity.getString(R.string.use_camera),
          mActivity.getString(R.string.select_album)
      }, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          switch (which) {
            case 0:
              if (!(mStartCamera = startCamera())) {
                mActivity.showToast(R.string.error_start_camera);
              }
              break;
            case 1:
              if (!(mStartAlbum = startAlbum())) {
                mActivity.showToast(R.string.error_start_album);
              }
              break;
            case Dialog.BUTTON_NEGATIVE:
              dialog.dismiss();
              break;
          }
        }
      }).create();
    }

    mCameraDialog.show();
  }

  public boolean startCamera() {
    try {
      Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      mOutputFile = IOUtils.createTmpFile("camera_output");
      i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutputFile));
      if (mFragment != null) {
        mFragment.startActivityForResult(i, REQUEST_CODE_CAMERA);
      } else {
        mActivity.startActivityForResult(i, REQUEST_CODE_CAMERA);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean startAlbum() {
    try {
      Intent i = new Intent(Intent.ACTION_PICK);
      i.setType("image/*");
      if (mFragment != null) {
        mFragment.startActivityForResult(i, REQUEST_CODE_ALBUM);
      } else {
        mActivity.startActivityForResult(i, REQUEST_CODE_ALBUM);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean startCrop() {
    if (mFragment != null) {
      ImageCropActivity.startForResult(mFragment, REQUEST_CODE_CROP, Uri.fromFile(mOutputFile), mFixedRatio);
    } else {
      ImageCropActivity.startForResult(mActivity, REQUEST_CODE_CROP, Uri.fromFile(mOutputFile), mFixedRatio);
    }
    return true;
  }

  public File getOutputFile() {
    return mOutputFile;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_CODE_ALBUM:
        if (resultCode == Activity.RESULT_CANCELED) return;
        if (data != null) {
          Uri uri = data.getData();

          Cursor cursor = mActivity.getContentResolver()
              .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

          if (cursor != null && cursor.moveToFirst()) {
            String p = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            mOutputFile = new File(p);
            if (!startCrop()) {
              mCallback.onImageReturn(mOutputFile.getAbsolutePath());
            }
          }
        }
        break;
      case REQUEST_CODE_CAMERA:
        if (resultCode == Activity.RESULT_CANCELED) return;
        if (mOutputFile != null) {
          if (!startCrop()) {
            mCallback.onImageReturn(mOutputFile.getAbsolutePath());
          }
        }
        break;
      case REQUEST_CODE_CROP:
        if (resultCode != Activity.RESULT_CANCELED) {
          if (data != null) {
            Uri uri = data.getData();
            mCallback.onImageReturn(uri.getPath());
          }
        } else {
          if (mStartAlbum) {
            mStartAlbum = startAlbum();
            mStartCamera = false;
          } else if (mStartCamera) {
            mStartCamera = startCamera();
            mStartAlbum = false;
          } else {
            mStartAlbum = false;
            mStartCamera = false;
          }
        }
        break;
      default:
        break;
    }
  }

  public void setCallback(Callback callback) {
    mCallback = callback;

  }


  public interface Callback {
    void onImageReturn(String path);
  }

}
