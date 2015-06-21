package com.utree.eightysix.app.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.ProfileFillActivity;
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
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.*;
import com.utree.eightysix.widget.panel.GridPanel;
import com.utree.eightysix.widget.panel.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 */
@TopTitle(R.string.publish_content)
public class PublishActivity extends BaseActivity implements
    EmojiconGridFragment.OnEmojiconClickedListener,
    EmojiconsFragment.OnEmojiconBackspaceClickedListener {

  private static final String FIRST_RUN_KEY = "post_activity";

  @InjectView(R.id.et_post_content)
  public EditText mPostEditText;

  @InjectView(R.id.tv_bottom)
  public TextView mTvBottom;

  @InjectView(R.id.aiv_post_bg)
  public AsyncImageView mAivPostBg;

  @InjectView(R.id.tv_post_tip)
  public TextView mTvPostTip;

  @InjectView(R.id.gp_panel)
  public GridPanel mGpPanel;

  @InjectView(R.id.in_panel)
  public IndicatorView mInPanel;

  @InjectView(R.id.tv_tag_1)
  public TagView mTvTag1;

  @InjectView(R.id.tv_tag_2)
  public TagView mTvTag2;

  @InjectView(R.id.rb_tag)
  public RoundedButton mRbTag;

  @InjectView(R.id.tl_tags)
  public TagsLayout mTagsLayout;

  @InjectView(R.id.cb_check)
  public CheckBox mCbAnonymous;

  @InjectView(R.id.fl_emotion)
  public EmojiViewPager mFlEmotion;

  @InjectView(R.id.et_temp_name)
  public EditText mEtTempName;

  @InjectView(R.id.iv_temp_name)
  public ImageView mIvTempName;

  @InjectView(R.id.rb_page)
  public RoundedButton mRbPage;

  protected PublishLayout mPublishLayout;

  protected int mFactoryId;
  protected int mTopicId;

  private String mImageUploadUrl;
  private String mImageHash;

  private Dialog mDescriptionDialog;
  private boolean mIsOpened;
  private boolean mRequestStarted;
  private boolean mImageUploadFinished;
  private boolean mUseColor = true;


  private int mBgColor = Color.WHITE;

  private ThemedDialog mQuitConfirmDialog;

  private List<Tag> mTags;
  private int mSendType;

  private CameraUtil mCameraUtil;
  private Instrumentation mInstrumentation = new Instrumentation();

  public static void start(Context context, int factoryId, List<Tag> tags) {
    Intent intent = new Intent(context, PublishActivity.class);
    intent.putExtra("factoryId", factoryId);
    if (tags != null) {
      intent.putParcelableArrayListExtra("tags", (ArrayList<? extends Parcelable>) tags);
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

  public static void startWithTopicId(Context context, int topicId, List<Tag> tags, String hint) {
    Intent intent = new Intent(context, PublishActivity.class);
    intent.putExtra("topicId", topicId);
    if (tags != null) {
      intent.putParcelableArrayListExtra("tags", (ArrayList<? extends Parcelable>) tags);
    }
    intent.putExtra("hint", hint);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick(R.id.iv_temp_name)
  public void onIvTempNameClicked(View v) {
    mIvTempName.setSelected(!v.isSelected());
  }

  @OnTextChanged(R.id.et_temp_name)
  public void onEtTempNameTextChanged(CharSequence cs) {
    if (cs.length() > 0) {
      mIvTempName.setSelected(true);
    }
  }

  @OnClick(R.id.ll_bottom)
  public void onLlBottomClicked() {
    showDescriptionDialog();
  }

  @OnClick(R.id.iv_shuffle)
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

  @OnClick(R.id.iv_emotion)
  public void onIvEmotionClicked() {
    if (mIsOpened) {
      hideSoftKeyboard(mPostEditText);
      mPublishLayout.switchToPanel(PublishLayout.PANEL_EMOTION);
    } else if (mPublishLayout.getCurrentPanel() == PublishLayout.PANEL_EMOTION) {
      mPublishLayout.switchToPanel(PublishLayout.PANEL_INFO);
    } else {
      mPublishLayout.switchToPanel(PublishLayout.PANEL_EMOTION);
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

  @OnClick(R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    mPublishLayout.switchToPanel(PublishLayout.PANEL_TAGS);
  }

  @OnClick(R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    mPublishLayout.switchToPanel(PublishLayout.PANEL_TAGS);
  }


  @OnClick(R.id.iv_camera)
  public void onIvCameraClicked() {
    mCameraUtil.showCameraDialog();
  }

  @OnFocusChange(R.id.et_post_content)
  public void onPostEditTextFocusChanged(boolean focused) {
    if (focused) {
      mPostEditText.setHintTextColor(0x88ffffff);
    } else {
      mPostEditText.setHintTextColor(0xffffffff);
    }
  }

  @Override
  public void onActionLeftClicked() {
    confirmFinish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        setBgImage(path);
      }
    });

    mFactoryId = getIntent().getIntExtra("factoryId", -1);
    mTopicId = getIntent().getIntExtra("topicId", -1);

    mSendType = getIntent().getIntExtra("sendType", 0);

    final String hint = getIntent().getStringExtra("hint");
    if (hint != null) {
      mPostEditText.setHint(hint);
    }

    mPublishLayout = new PublishLayout(this);
    setContentView(mPublishLayout);

    List<Tag> tags = getIntent().getParcelableArrayListExtra("tags");
    if (tags != null) {
      setSelectedTags(tags);
      mPublishLayout.switchToPanel(PublishLayout.PANEL_TAGS);
    }

    mTvPostTip.setText(getHintText());

    mCbAnonymous.setChecked(Account.inst().getPostAnonymous());

    if (Account.inst().getPostAnonymous()) {
      if (mSendType == 0) {
        mEtTempName.setVisibility(View.VISIBLE);
        mIvTempName.setVisibility(View.VISIBLE);
      }
    } else {
      mEtTempName.setVisibility(View.INVISIBLE);
      mIvTempName.setVisibility(View.INVISIBLE);
    }

    mCbAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
        if (!checked) {
          if (Account.inst().getCancelPostAnonymousDialog()) {
            showCancelAnonymousDialog();
          }
        }

        Account.inst().setPostAnonymous(checked);

        if (checked) {
          if (mSendType == 0) {
            mEtTempName.setVisibility(View.VISIBLE);
            mIvTempName.setVisibility(View.VISIBLE);
          }
        } else {
          mEtTempName.setVisibility(View.INVISIBLE);
          mIvTempName.setVisibility(View.INVISIBLE);
        }
      }
    });

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
        } else if (mPostEditText.getLineCount() > U.getConfigInt("post.lines")) {
          mPostEditText.setText(mBefore.subSequence(0, mBefore.length() - 1));
          mPostEditText.setSelection(mBefore.length() - 1);
        }

        mPostEditText.getSelectionStart();

        mRbPage.setVisibility(View.VISIBLE);
        mRbPage.setText(String.valueOf(mPostEditText.getLineCount() / 7 + 1));
      }

      @Override
      public void afterTextChanged(Editable s) {
      }

    });

    mPostEditText.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
          int editTextLineCount = ((EditText) view).getLineCount();
          if (editTextLineCount >= U.getConfigInt("post.lines")) {
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
        requestPublish();
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

    mFlEmotion.setFragmentManager(getSupportFragmentManager());
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
      switch (i) {
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

    if (mImageHash != null && mImageHash.equals(event.getHash())) {
      mImageUploadFinished = true;
      mImageUploadUrl = event.getUrl();

      if (mRequestStarted) {
        requestPublish();
      }
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

    dialog.setTitle("确认取消匿名么？");

    View view = LayoutInflater.from(this).inflate(R.layout.dialog_cancel_post_anonymouse, null, false);
    dialog.setContent(view);
    ((CheckBox) view.findViewById(R.id.cb_check)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Account.inst().setCancelPostAnonymousDialog(!isChecked);
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

    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        mCbAnonymous.setChecked(true);
      }
    });

    dialog.show();
  }

  private void setBgImage(String p) {
    final File file = new File(p);
    Bitmap bitmap = ImageUtils.safeDecodeBitmap(file);
    mImageHash = IOUtils.fileHash(file);
    ImageUtils.asyncUpload(file, 50);
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

      String tags = "";
      for (Tag t : mTagsLayout.getSelectedTags()) {
        tags = tags.concat(String.valueOf(t.id)).concat(",");
      }

      if (!TextUtils.isEmpty(tags)) {
        tags = tags.substring(0, tags.length() - 1);
        builder.tags(tags);
      }

      builder.realName(mCbAnonymous.isChecked() ? 0 : 1);

      if (mCbAnonymous.isChecked()) {
        if (!TextUtils.isEmpty(mEtTempName.getText()) && mIvTempName.isSelected()) {
          builder.tempName(mEtTempName.getText().toString());
          builder.sourceType(2);
        }
      }

      disablePublishButton();

      PublishRequest build = builder.build();

      RequestData<PublishPostResponse> requestData = new RequestData<PublishPostResponse>(build);
      requestData.setHost(U.getConfig("api.host.second"));

      request(requestData, new OnResponse<PublishPostResponse>() {
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
          } else if (response.code == 0x32309) {
            ProfileFillActivity.start(PublishActivity.this, false);
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

          mEtTempName.setText(response.object.lastTempName);
          mTagsLayout.setTag(mTags);
          mIvTempName.setSelected(false);
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

          mEtTempName.setText(response.object.lastTempName);
          mTagsLayout.setTag(mTags);
          mIvTempName.setSelected(false);
        }
      }
    }, TagsResponse.class);
  }

  @Override
  public void onEmojiconBackspaceClicked(View v) {

  }

  @Override
  public void onEmojiconClicked(Emojicon emojicon) {
    if ("\u274c".equals(emojicon.getEmoji())) {
      (new Thread() {
        @Override
        public void run() {
          mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
        }
      }).start();
    } else {
      String text = mPostEditText.getText().toString();
      String before = text.substring(0, mPostEditText.getSelectionStart());
      String after = text.substring(mPostEditText.getSelectionEnd());

      mPostEditText.setText(before + emojicon.getEmoji() + after);
      mPostEditText.setSelection(before.length() + emojicon.getEmoji().length());
    }
  }
}