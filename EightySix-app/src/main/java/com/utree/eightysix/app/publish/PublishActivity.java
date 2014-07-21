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
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.sun.org.apache.bcel.internal.generic.IMUL;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.request.PublishRequest;
import com.utree.eightysix.response.PublishPostResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.PostEditText;
import com.utree.eightysix.widget.TopBar;
import com.utree.eightysix.widget.panel.GridPanel;
import com.utree.eightysix.widget.panel.Item;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.util.List;
import java.util.Random;

/**
 */
@TopTitle (R.string.publish_content)
public class PublishActivity extends BaseActivity {

  private static final String FIRST_RUN_KEY = "post_activity";

  private static final int REQUEST_CODE_CAMERA = 0x1;
  private static final int REQUEST_CODE_ALBUM = 0x2;
  private static final int REQUEST_CODE_CROP = 0x4;

  @InjectView (R.id.et_post_content)
  public PostEditText mPostEditText;

  @InjectView (R.id.tv_bottom)
  public TextView mTvBottom;

  @InjectView (R.id.aiv_post_bg)
  public AsyncImageView mAivPostBg;

  @InjectView (R.id.tv_post_tip)
  public TextView mTvPostTip;

  @InjectView (R.id.ll_bottom)
  public LinearLayout mLlBottom;

  @InjectView (R.id.gp_color)
  public GridPanel mGpColor;

  protected PublishLayout mPublishLayout;

  private Dialog mCameraDialog;
  private Dialog mDescriptionDialog;
  private Dialog mConfirmQuitDialog;

  private File mOutputFile;

  private boolean mIsOpened;
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
  public void onActionLeftClicked() {
    confirmFinish();
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
          getTopBar().getActionView(0).setEnabled(false);
        } else {
          mTvPostTip.setVisibility(View.INVISIBLE);
          getTopBar().getActionView(0).setEnabled(true);
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
        return getString(R.string.publish_post);
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
            requestPublish();
          }
        }
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
      }
    });

    List<Item> itemsByPage = mGpColor.getItemsByPage(0);
    Item item = itemsByPage.get(new Random().nextInt(itemsByPage.size()));
    int color = item.getValue().data;
    mPostEditText.setTextColor(ColorUtil.monochromizing(color));
    mTvPostTip.setTextColor(ColorUtil.monochromizing(color));
    mAivPostBg.setImageDrawable(null);
    mAivPostBg.setBackgroundColor(color);
    mBgColor = color;
  }

  @Override
  protected void onDestroy() {
    Log.d("ImageUtils", "onDestroy PublishActivity");
    Env.setFirstRun(FIRST_RUN_KEY, false);
    super.onDestroy();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onImageUploaded(ImageUtils.ImageUploadedEvent event) {
    if (event.getHash().equals(mFileHash)) {
      mImageUploadFinished = true;
      mImageUploadUrl = event.getUrl();
    }

    if (mRequestStarted) {
      requestPublish();
    }
  }

  @Subscribe
  public void onGridPanelItemClicked(Item item) {
    final TypedValue tv = item.getValue();
    if (tv.type == TypedValue.TYPE_INT_COLOR_ARGB8) {
      ValueAnimator.clearAllAnimations();
      ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), mBgColor, tv.data);
      animator.setDuration(500);
      animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          mAivPostBg.setBackgroundColor((Integer) animation.getAnimatedValue());
        }
      });
      animator.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
          mPostEditText.setTextColor(ColorUtil.monochromizing(tv.data));
          mTvPostTip.setTextColor(ColorUtil.monochromizing(tv.data));
          mBgColor = tv.data;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      });
      animator.start();
      mAivPostBg.setImageDrawable(null);
    } else if (tv.type == TypedValue.TYPE_STRING) {
      mAivPostBg.setBackgroundColor(Color.TRANSPARENT);
      mAivPostBg.setUrl(tv.string.toString());
      mImageUploadFinished = true;
      mImageUploadUrl = tv.string.toString();
      mUseColor = false;
      mBgColor = Color.WHITE;
    } else if (tv.type == TypedValue.TYPE_REFERENCE) {
      mAivPostBg.setBackgroundColor(Color.TRANSPARENT);

      mImageUploadUrl = U.getCloudStorage().getUrl(U.getConfig("storage.bg.bucket.name"),
          "",
          getResources().getResourceEntryName(tv.resourceId) + ".png");
      mImageUploadFinished = true;

      Bitmap bitmap = ImageUtils.syncLoadResourceBitmap(tv.resourceId, ImageUtils.getUrlHash(mImageUploadUrl));
      mAivPostBg.setImageBitmap(bitmap);
      ColorUtil.asyncThemedColor(bitmap);
      //Log.d("PublishActivity", "URL: " + mImageUploadUrl);
      mUseColor = false;
      mBgColor = Color.WHITE;
    }
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (!TextUtils.isEmpty(mImageUploadUrl)) {
      if (event.getHash().equals(ImageUtils.getUrlHash(mImageUploadUrl))) {
        ColorUtil.asyncThemedColor(event.getBitmap());
      }
    }
  }

  @Subscribe
  public void onThemedColorEvent(ColorUtil.ThemedColorEvent event) {
    int monochromizing = ColorUtil.monochromizing(event.getColor());
    mPostEditText.setTextColor(monochromizing);
    mTvPostTip.setTextColor(monochromizing);
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

  @Override
  public void onBackPressed() {
    confirmFinish();
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
    mAivPostBg.setImageBitmap(bitmap);
    mAivPostBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
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

  private void requestPublish() {
    mRequestStarted = true;

    if (mImageUploadFinished || mUseColor) {

      final PublishRequest request = new PublishRequest(mFactoryId, mPostEditText.getText().toString(),
          mUseColor ? String.format("%h", mBgColor) : "", mImageUploadUrl);

      request(request, new OnResponse<PublishPostResponse>() {
        @Override
        public void onResponse(PublishPostResponse response) {
          if (RESTRequester.responseOk(response)) {
            showToast(R.string.send_succeed, false);

            Post post = new Post();
            post.bgColor = String.format("%h", mBgColor);
            post.bgUrl = mImageUploadUrl;
            post.id = response.object.id;
            post.content = mPostEditText.getText().toString();
            post.source = "认识的人";
            post.type = BaseItem.TYPE_POST;
            U.getBus().post(new PostPublishedEvent(post));

            finish();
          }
          hideProgressBar();
        }
      }, PublishPostResponse.class);
    }

    hideSoftKeyboard(mPostEditText);
    showProgressBar(true);
  }
}