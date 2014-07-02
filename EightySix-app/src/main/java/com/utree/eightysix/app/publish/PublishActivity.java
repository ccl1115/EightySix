package com.utree.eightysix.app.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.PostRequest;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.PostEditText;
import com.utree.eightysix.widget.TopBar;
import com.utree.eightysix.widget.panel.Item;
import java.io.File;

/**
 */
@TopTitle (R.string.post)
public class PublishActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "post_activity";

  private static final int REQUEST_CODE_CAMERA = 0x1;
  private static final int REQUEST_CODE_ALBUM = 0x2;
  private static final int REQUEST_CODE_CROP = 0x4;

  @InjectView (R.id.et_post_content)
  public PostEditText mPostEditText;

  @InjectView (R.id.tv_bottom)
  public TextView mTvBottom;

  @InjectView (R.id.iv_post_bg)
  public ImageView mIvPostBg;

  @InjectView (R.id.tv_post_tip)
  public TextView mTvPostTip;

  @InjectView (R.id.ll_bottom)
  public LinearLayout mLlBottom;

  protected PublishLayout mPublishLayout;

  private Dialog mCameraDialog;
  private Dialog mDescriptionDialog;
  private Dialog mConfirmQuitDialog;

  private File mOutputFile;

  private boolean mIsOpened;
  private boolean mToastShown;
  private boolean mRequestStarted;
  private boolean mImageUploadFinished;
  private boolean mUseColor = true;

  private String mFileHash;

  private String mImageUploadUrl;

  private int mBgColor = Color.WHITE;

  private int mFactoryId;

  public static void start(Context context, int factoryId) {
    Intent intent = new Intent(context, PublishActivity.class);
    intent.putExtra("factoryId", factoryId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick (R.id.ll_bottom)
  public void onLlBottomClicked() {
    mDescriptionDialog.show();
  }

  @OnClick (R.id.iv_shuffle)
  public void onIvShuffleClicked() {
    if (mIsOpened) {
      hideSoftKeyboard(mPostEditText);
      mPublishLayout.switchToPanel(PublishLayout.PANEL_COLOR);
    } else {
      mPublishLayout.switchPanel();
    }
  }

  @OnClick (R.id.iv_camera)
  public void onIvCameraClicked() {
    mCameraDialog.show();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mFactoryId = getIntent().getIntExtra("factoryId", -1);

    if (mFactoryId == -1) {
      showToast("没有找到圈子", false);
      finish();
    }

    mPublishLayout = new PublishLayout(this);
    setContentView(mPublishLayout);

    onIvShuffleClicked();

    //region To detect soft keyboard visibility change
    final View activityRootView = findViewById(android.R.id.content);
    activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
        if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.

          if (!mIsOpened) {
            mPublishLayout.hidePanel();
          }
          mIsOpened = true;
        } else if (mIsOpened) {
          mPublishLayout.showPanel();
          mIsOpened = false;
        }
      }
    });
    //endregion

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setTitle(R.string.add_photo).setItems(new String[]{
        getString(R.string.use_camera),
        getString(R.string.select_album)
    }, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case 0:
            if (!startCamera()) {
              showToast(R.string.error_start_camera);
            }
            break;
          case 1:
            if (!startAlbum()) {
              showToast(R.string.error_start_album);
            }
            break;
          case Dialog.BUTTON_NEGATIVE:
            dialog.dismiss();
            break;
        }
      }
    });

    mCameraDialog = builder.create();

    builder = new AlertDialog.Builder(this);

    builder.setTitle(R.string.who_will_see_this_secret).setMessage(R.string.post_description)
        .setPositiveButton(getString(R.string.got_it), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (which == Dialog.BUTTON_POSITIVE) {
              dialog.dismiss();
            }
          }
        });

    mDescriptionDialog = builder.create();

    builder = new AlertDialog.Builder(this);

    builder.setTitle(getString(R.string.quit_confirm))
        .setPositiveButton(getString(R.string.resume_editing), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        });

    mConfirmQuitDialog = builder.create();

    if (Env.firstRun(FIRST_RUN_KEY)) {
      mDescriptionDialog.show();
    }

    mPostEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
          mTvPostTip.setVisibility(View.VISIBLE);
        } else {
          mTvPostTip.setVisibility(View.INVISIBLE);
        }

        if (s.length() > U.getConfigInt("post.length")) {
          final int selection = mPostEditText.getSelectionStart();
          s = s.subSequence(0, U.getConfigInt("post.length"));
          mPostEditText.setText(s);
          mPostEditText.setSelection(Math.min(selection, s.length()));
        }

        if (!InputValidator.post(s) && count > 0) {
          showToast(U.gfs(R.string.post_over_length, U.getConfigInt("post.length")));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return getString(R.string.post);
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return new RoundRectDrawable(dp2px(2), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
      }

      @Override
      public void onClick(View view, int position) {
        if (position == 0) {

          if (mPostEditText.getText().length() == 0) {
            showToast(getString(R.string.cannot_post_empty_content));
          } else {
            requestPost();
          }
        }
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public FrameLayout.LayoutParams getLayoutParams(int position) {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
      }
    });
  }

  @Override
  protected void onDestroy() {
    Env.setFirstRun(FIRST_RUN_KEY, false);

    super.onDestroy();
  }

  @Override
  protected void onActionLeftOnClicked() {
    confirmFinish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onBackPressed() {
    confirmFinish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_CANCELED) return;

    switch (requestCode) {
      case REQUEST_CODE_ALBUM:
        if (data != null) {
          Uri uri = data.getData();

          Cursor cursor = getContentResolver()
              .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

          if (cursor.moveToFirst()) {
            String p = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            mOutputFile = new File(p);
            if (!startCrop()) {
              setBgImage(p);
            }
          }
        }
        break;
      case REQUEST_CODE_CAMERA:
        if (mOutputFile != null) {
          if (!startCrop()) {
            setBgImage(mOutputFile.getAbsolutePath());
          }
        }
        break;
      case REQUEST_CODE_CROP:
        if (data != null) {
          Uri uri = data.getData();

          Cursor cursor = getContentResolver()
              .query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

          if (cursor.moveToFirst()) {
            String p = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            setBgImage(p);
          }
        }
        break;
      default:
        break;
    }
  }

  @Subscribe
  public void onImageUploaded(ImageUtils.ImageUploadedEvent event) {
    if (event.getHash().equals(mFileHash)) {
      mImageUploadFinished = true;
      mImageUploadUrl = event.getUrl();
    }

    if (mRequestStarted) {
      requestPost();
    }
  }

  @Subscribe
  public void onGridPanelItemClicked(Item item) {
    for (TypedValue tv : item.getValues()) {
      if (tv.type == TypedValue.TYPE_INT_COLOR_ARGB8) {
        mIvPostBg.setImageDrawable(new ColorDrawable(tv.data));
        mPostEditText.setTextColor(monochromizing(tv.data));
        mTvPostTip.setTextColor(monochromizing(tv.data));
        mBgColor = tv.data;
      }
    }
  }

  private void confirmFinish() {
    if (TextUtils.isEmpty(mPostEditText.getText())) {
      super.onBackPressed();
    } else {
      mConfirmQuitDialog.show();
    }
  }

  private void setBgImage(String p) {
    final File file = new File(p);
    mFileHash = IOUtils.fileHash(file);
    Bitmap bitmap = ImageUtils.safeDecodeBitmap(file);
    ImageUtils.asyncUpload(file);
    mPostEditText.setTextColor(Color.WHITE);
    mPostEditText.setShadowLayer(2, 0, 0, Color.BLACK);
    mTvPostTip.setTextColor(Color.WHITE);
    mIvPostBg.setImageBitmap(bitmap);
    mIvPostBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
    mUseColor = false;
    mImageUploadFinished = false;
  }

  private boolean startCamera() {
    try {
      Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      mOutputFile = IOUtils.createTmpFile("camera_output");
      i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mOutputFile));
      startActivityForResult(i, REQUEST_CODE_CAMERA);
      return true;
    } catch (Exception e) {
      U.getAnalyser().reportException(this, e);
      return false;
    }
  }

  private boolean startAlbum() {
    try {
      Intent i = new Intent(Intent.ACTION_PICK);
      i.setType("image/*");
      startActivityForResult(i, REQUEST_CODE_ALBUM);
      return true;
    } catch (Exception e) {
      U.getAnalyser().reportException(this, e);
      return false;
    }
  }

  private boolean startCrop() {
    try {
      Intent cropIntent = new Intent("com.android.camera.action.CROP");
      // indicate image type and Uri
      cropIntent.setDataAndType(Uri.fromFile(mOutputFile), "image/*");
      // set crop properties
      cropIntent.putExtra("crop", "true");
      // indicate aspect of desired crop
      cropIntent.putExtra("aspectX", 1);
      cropIntent.putExtra("aspectY", 1);
      // start the activity - we handle returning in onActivityResult
      startActivityForResult(cropIntent, REQUEST_CODE_CROP);
      return true;
    } catch (Exception e) {
      U.getAnalyser().reportException(this, e);
      return false;
    }
  }

  private int monochromizing(int color) {
    return (color & 0xff) > 0x88 && ((color >> 8) & 0xff) > 0x88 && ((color >> 16) & 0xff) > 0x88
        ? Color.BLACK : Color.WHITE;
  }

  private void requestPost() {
    mRequestStarted = true;

    if (mImageUploadFinished || mUseColor) {

      final PostRequest request = new PostRequest(mFactoryId, mPostEditText.getText().toString(),
          mUseColor ? String.format("%h", mBgColor) : "", mImageUploadUrl);

      request(request, new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (response != null) {
            if (response.code == 0) {
              showToast(R.string.send_succeed, false);

              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(mPostEditText.getWindowToken(), 0);

              finish();
            }
          }
          hideProgressBar();
        }
      }, Response.class);
    }

    showProgressBar();
  }
}