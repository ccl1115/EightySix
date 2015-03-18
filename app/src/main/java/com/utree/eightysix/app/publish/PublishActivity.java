package com.utree.eightysix.app.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.request.PublishRequest;
import com.utree.eightysix.request.TagsRequest;
import com.utree.eightysix.response.PublishPostResponse;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.InputValidator;
import com.utree.eightysix.widget.*;
import com.utree.eightysix.widget.panel.GridPanel;
import com.utree.eightysix.widget.panel.Item;

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
  public EditText mPostEditText;

  @InjectView (R.id.tv_bottom)
  public TextView mTvBottom;

  @InjectView (R.id.aiv_post_bg)
  public AsyncImageView mAivPostBg;

  @InjectView (R.id.tv_post_tip)
  public TextView mTvPostTip;

  @InjectView (R.id.gp_panel)
  public GridPanel mGpPanel;

  @InjectView (R.id.in_panel)
  public IndicatorView mInPanel;

  @InjectView (R.id.tv_tag_1)
  public TagView mTvTag1;

  @InjectView (R.id.tv_tag_2)
  public TagView mTvTag2;

  @InjectView(R.id.rb_tag)
  public RoundedButton mRbTag;

  @InjectView (R.id.tl_tags)
  public TagsLayout mTagsLayout;

  @InjectView(R.id.cb_check)
  public CheckBox mCbAnonymous;

  protected PublishLayout mPublishLayout;

  protected int mFactoryId;
  protected int mTopicId;

  private Dialog mDescriptionDialog;
  private boolean mIsOpened;
  private boolean mRequestStarted;
  private boolean mImageUploadFinished;
  private boolean mUseColor = true;
  private String mImageUploadUrl;
  private int mBgColor = Color.WHITE;

  private ThemedDialog mQuitConfirmDialog;

  private List<Tag> mTags;
  private int mSendType;

  private CameraUtil mCameraUtil;

  public static void start(Context context, int factoryId, List<Tag> tags) {
    Intent intent = new Intent(context, PublishActivity.class);
    intent.putExtra("factoryId", factoryId);
    if (tags != null) {
      intent.putParcelableArrayListExtra("tags", (java.util.ArrayList<? extends android.os.Parcelable>) tags);
    }

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void startHometown(Context context) {
    Intent intent = new Intent(context, PublishActivity.class);
    intent.putExtra("sendType", 1);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  public static void startWithTopicId(Context context, int topicId, List<Tag> tags) {
    Intent intent = new Intent(context, PublishActivity.class);
    intent.putExtra("topicId", topicId);
    if (tags != null) {
      intent.putParcelableArrayListExtra("tags", (java.util.ArrayList<? extends android.os.Parcelable>) tags);
    }

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick (R.id.ll_bottom)
  public void onLlBottomClicked() {
    showDescriptionDialog();
  }

  @OnClick (R.id.iv_shuffle)
  public void onIvShuffleClicked() {
    if (mIsOpened) {
      hideSoftKeyboard(mPostEditText);
      mPublishLayout.switchToPanel(PublishLayout.PANEL_COLOR);
    } else if (mPublishLayout.getCurrentPanel() == PublishLayout.PANEL_COLOR) {
      mPublishLayout.switchToPanel(PublishLayout.PANEL_INFO);
    } else {
      mPublishLayout.switchToPanel(PublishLayout.PANEL_COLOR);
    }
  }

  @OnClick(R.id.rb_tag)
  public void onRbTagClicked() {

    U.getAnalyser().trackEvent(this, "publish_tag_panel", "publish_tag_panel");

    if (mIsOpened) {
      hideSoftKeyboard(mPostEditText);
      mPublishLayout.switchToPanel(PublishLayout.PANEL_TAGS);

      if (!mTagsLayout.hasTags()) {
        requestTags();
      }
    } else if (mPublishLayout.getCurrentPanel() == PublishLayout.PANEL_TAGS) {
      mPublishLayout.switchToPanel(PublishLayout.PANEL_INFO);
    } else {
      mPublishLayout.switchToPanel(PublishLayout.PANEL_TAGS);

      if (!mTagsLayout.hasTags()) {
        requestTags();
      }
    }
  }


  @OnClick (R.id.iv_camera)
  public void onIvCameraClicked() {
    mCameraUtil.showCameraDialog();
  }

  @OnFocusChange (R.id.et_post_content)
  public void onPostEditTextFocusChanged(boolean focused) {
    if (focused) {
      mPostEditText.setHintTextColor(0x88ffffff);
    } else {
      mPostEditText.setHintTextColor(0xffffffff);
    }
  }

  @OnCheckedChanged(R.id.cb_check)
  public void onCbAnonymousCheckChanged(boolean checked) {
    if (!checked) {
      if (Env.firstRun("cancel_anonymous")) {
        showCancelAnonymousDialog();
      }
    }
  }

  @Override
  public void onActionLeftClicked() {
    confirmFinish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        setBgImage(path);
      }
    });

    mFactoryId = getIntent().getIntExtra("factoryId", -1);
    mTopicId = getIntent().getIntExtra("topicId", -1);

    mSendType = getIntent().getIntExtra("sendType", 0);

    mPublishLayout = new PublishLayout(this);
    setContentView(mPublishLayout);

    List<Tag> tags = getIntent().getParcelableArrayListExtra("tags");
    if (tags != null) {
      setSelectedTags(tags);
      mPublishLayout.switchToPanel(PublishLayout.PANEL_TAGS);
    }

    mTvPostTip.setText(getHintText());

    //region To detect soft keyboard visibility change
    // works after ICM
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      final View activityRootView = findViewById(android.R.id.content);
      activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
          if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.

            if (!mIsOpened) {
              mPublishLayout.hidePanel();
              mTvPostTip.setText("");
            }
            mIsOpened = true;
          } else if (mIsOpened) {
            mPublishLayout.showPanel();
            mTvPostTip.setText(getHintText());
            mIsOpened = false;
          }
        }
      });
    }
    //endregion

    AlertDialog.Builder builder = new AlertDialog.Builder(this);


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

    mPostEditText.addTextChangedListener(new TextWatcher() {

      private CharSequence mBefore;
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mBefore = s;
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
          mTvPostTip.setVisibility(View.VISIBLE);
          disablePublishButton();
        } else {
          mTvPostTip.setVisibility(View.INVISIBLE);
          enablePublishButton();
        }

        final int length = U.getConfigInt("post.length");
        if (s.length() > length) {
          mPostEditText.setText(mBefore);
          mPostEditText.setSelection(mBefore.length());
        } else if (mPostEditText.getLineCount() > 7) {
          mPostEditText.setText(mBefore);
          mPostEditText.setSelection(mBefore.length());
        }

        if (!InputValidator.post(s)) {
          showToast(U.gfs(R.string.post_over_length, length));
        }
      }
    });

    mPostEditText.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
          int editTextLineCount = ((EditText) view).getLineCount();
          if (editTextLineCount >= 7) {
            return true;
          }
        }
        return false;
      }
    });

    getTopBar().getAbRight().setText("发表");
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mPostEditText.getText().length() == 0) {
          showToast(getString(R.string.cannot_post_empty_content));
        } else {
          requestPublish();
        }
      }
    });

    randomItem();

    mGpPanel.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInPanel.setPosition(position + positionOffset);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    showDescriptionDialogWhenFirstRun();

    cacheOutTags();

    mTagsLayout.setOnSelectedTagsChangedListener(new TagsLayout.OnSelectedTagsChangedListener() {
      @Override
      public void onSelectedTagsChanged(List<Tag> tags) {
        setSelectedTags(tags);

      }
    });
  }

  protected void setSelectedTags(List<Tag> tags) {
    if (tags.size() > 0) {
      mRbTag.setVisibility(View.GONE);
    } else {
      mRbTag.setVisibility(View.VISIBLE);
    }
    mTvTag1.setText("");
    mTvTag2.setText("");
    for (int i = 0; i < tags.size(); i++) {
      Tag g = tags.get(i);
      switch(i) {
        case 0:
          mTvTag1.setText("#" + g.content);
          break;
        case 1:
          mTvTag2.setText("#" + g.content);
          break;
      }
    }

    mTagsLayout.setSelectedTags(tags);
  }

  @Override
  protected void onDestroy() {
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
    if (event.getHash() == null || event.getUrl() == null) {
      mImageUploadFinished = false;
      showToast("上传图片失败");
      randomItem();
      return;
    }

    mImageUploadFinished = true;
    mImageUploadUrl = event.getUrl();

    if (mRequestStarted) {
      requestPublish();
    }
  }

  @Subscribe
  public void onGridPanelItemClicked(Item item) {
    switchItem(item, true);
  }

  protected String getHintText() {
    return getString(R.string.post_anonymously);

  }

  protected void showDescriptionDialogWhenFirstRun() {
    if (Env.firstRun(FIRST_RUN_KEY)) {
      showDescriptionDialog();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBackPressed() {
    confirmFinish();
  }

  protected void showDescriptionDialog() {
    if (mDescriptionDialog == null) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
    }

    mDescriptionDialog.show();
  }

  protected void disablePublishButton() {
  }

  protected void enablePublishButton() {
  }

  private void randomItem() {
    List<Item> itemsByPage = mGpPanel.getItemsByPage(0);
    Item item = itemsByPage.get(new Random().nextInt(itemsByPage.size()));
    switchItem(item, false);
  }

  private void switchItem(Item item, boolean animation) {
    final TypedValue tv = item.getValue();
    if (tv.type == TypedValue.TYPE_INT_COLOR_ARGB8) {
      ValueAnimator.clearAllAnimations();
      ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), mBgColor, tv.data);
      animator.setDuration(animation ? 500 : 0);
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
      mUseColor = true;
      mBgColor = tv.data;
      mImageUploadUrl = "";
    } else if (tv.type == TypedValue.TYPE_STRING) {
      mAivPostBg.setBackgroundColor(Color.TRANSPARENT);
      mAivPostBg.setUrl(tv.string.toString());
      if (animation) {
        fadeInAnimation(mAivPostBg);
      }
      mImageUploadFinished = true;
      mImageUploadUrl = tv.string.toString();
      mUseColor = false;
      mBgColor = Color.WHITE;
    } else if (tv.type == TypedValue.TYPE_REFERENCE) {
      mAivPostBg.setBackgroundColor(Color.TRANSPARENT);
      if (animation) {
        fadeInAnimation(mAivPostBg);
      }
      mImageUploadUrl = U.getCloudStorage().getUrl(U.getBgBucket(),
          "",
          getResources().getResourceEntryName(tv.resourceId) + ".jpg");
      mImageUploadFinished = true;

      Bitmap bitmap = ImageUtils.syncLoadResourceBitmap(tv.resourceId, ImageUtils.getUrlHash(mImageUploadUrl));
      mAivPostBg.setImageBitmap(bitmap);
      mUseColor = false;
      mBgColor = Color.WHITE;
    }
  }

  private void fadeInAnimation(View view) {
    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
    animator.setDuration(500);
    animator.start();
  }

  private void confirmFinish() {
    if (TextUtils.isEmpty(mPostEditText.getText())) {
      super.onBackPressed();
    } else {
      if (mQuitConfirmDialog == null) {
        mQuitConfirmDialog = new ThemedDialog(this);
        mQuitConfirmDialog.setTitle("你有内容未发表，确认离开？");
        mQuitConfirmDialog.setPositive(R.string.okay, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            finish();
          }
        });
        mQuitConfirmDialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mQuitConfirmDialog.dismiss();
          }
        });
      }
      if (!mQuitConfirmDialog.isShowing()) {
        mQuitConfirmDialog.show();
      }
    }
  }

  private void showCancelAnonymousDialog() {
    final ThemedDialog dialog = new ThemedDialog(this);

    View view = LayoutInflater.from(this).inflate(R.layout.dialog_cancel_anonymouse, null, false);

    dialog.setContent(view);

    ((CheckBox) view.findViewById(R.id.cb_check)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Env.setFirstRun("cancel_anonymous", !isChecked);
      }
    });

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        mCbAnonymous.setChecked(false);
      }
    });

    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        mCbAnonymous.setChecked(true);
      }
    });

    dialog.show();
  }

  private void setBgImage(String p) {
    final File file = new File(p);
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


  protected void requestPublish() {
    mRequestStarted = true;

    if (mImageUploadFinished || mUseColor) {

      PublishRequest.Builder builder = new PublishRequest.Builder();
      builder
          .bgColor(mUseColor ? String.format("%h", mBgColor) : "")
          .bgUrl(mImageUploadUrl)
          .content(mPostEditText.getText().toString());

      if (mTopicId != -1) {
        builder.topicId(mTopicId);
      }

      if (mFactoryId != -1) {
        builder.factoryId(mFactoryId);
      }

      builder.sendType(mSendType);

      builder.realName(0);

      String tags = "";
      for (Tag t : mTagsLayout.getSelectedTags()) {
        tags = tags.concat(String.valueOf(t.id)).concat(",");
      }

      if (!TextUtils.isEmpty(tags)) {
        tags = tags.substring(0, tags.length() - 1);
        builder.tags(tags);
      }

      builder.realName(mCbAnonymous.isChecked() ? 0 : 1);

      disablePublishButton();

      request(builder.build(), new OnResponse<PublishPostResponse>() {
        @Override
        public void onResponse(PublishPostResponse response) {
          if (RESTRequester.responseOk(response)) {
            showToast(R.string.send_succeed, false);

            Post post = new Post();
            post.bgColor = String.format("%h", mBgColor);
            post.bgUrl = mImageUploadUrl;
            post.id = response.object.id;
            post.content = mPostEditText.getText().toString();
            post.source = ("认识的人");
            post.type = BaseItem.TYPE_POST;
            post.tags = mTagsLayout.getSelectedTags();
            U.getBus().post(new PostPublishedEvent(post, mFactoryId));

            finish();
          }
          hideProgressBar();
          enablePublishButton();
        }
      }, PublishPostResponse.class);
    }

    hideSoftKeyboard(mPostEditText);
    showProgressBar(true);
  }

  private void cacheOutTags() {
    cacheOut(new TagsRequest(), new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        requestTags();
      }

      @Override
      public void onResponse(TagsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mTags = response.object.tags;

          mTagsLayout.setTag(mTags);
        }
        requestTags();
      }
    }, TagsResponse.class);
  }

  private void requestTags() {
    request(new TagsRequest(), new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(TagsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mTags = response.object.tags;

          mTagsLayout.setTag(mTags);
        }
      }
    }, TagsResponse.class);
  }

}