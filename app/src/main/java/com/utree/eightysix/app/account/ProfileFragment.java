package com.utree.eightysix.app.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.annotations.SerializedName;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.CameraUtil;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.HolderFragment;
import com.utree.eightysix.app.account.event.*;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.app.circle.FollowCircleListActivity;
import com.utree.eightysix.app.friends.FriendListActivity;
import com.utree.eightysix.app.friends.SendRequestActivity;
import com.utree.eightysix.app.settings.MainSettingsActivity;
import com.utree.eightysix.data.Profile;
import com.utree.eightysix.response.CopywritingResponse;
import com.utree.eightysix.response.ProfileResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.TimeUtil;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.RoundedButton;
import com.utree.eightysix.widget.ThemedDialog;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 */
public class ProfileFragment extends HolderFragment {

  private static final long REFRESH_INTERVAL = 60000 * 30;

  public static void start(Context context, int viewId, String userName) {
    Bundle args = new Bundle();
    args.putBoolean("isVisitor", true);
    args.putInt("viewId", viewId);
    args.putString("userName", userName);

    FragmentHolder.start(context, ProfileFragment.class, args);
  }

  public static void start(Context context) {
    Bundle args = new Bundle();
    args.putBoolean("isVisitor", false);
    FragmentHolder.start(context, ProfileFragment.class, args);
  }

  @InjectView(R.id.tv_name)
  public TextView mTvName;

  @InjectView(R.id.tv_signature)
  public TextView mTvSignature;

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

  @InjectView(R.id.fl_guide)
  public FrameLayout mFlGuide;

  @InjectView(R.id.rb_change_bg)
  public RoundedButton mRbChangeBg;

  @InjectView(R.id.tv_my_friends)
  public TextView mTvMyFriends;

  @InjectView(R.id.tv_my_circles)
  public TextView mTvMyCircles;

  @InjectView(R.id.tv_settings)
  public TextView mTvSettings;

  @InjectView(R.id.tv_title_signature)
  public TextView mTvTitleSignature;

  @InjectView(R.id.tv_my_posts)
  public TextView mTvMyPosts;

  @InjectView(R.id.tv_exp)
  public TextView mTvExp;

  @InjectView(R.id.pb_exp)
  public ProgressBar mPbExp;

  @InjectView(R.id.aiv_level_icon)
  public AsyncImageView mAivLevelIcon;

  @InjectView(R.id.tv_action)
  public TextView mTvAction;

  @InjectView(R.id.scroll_view)
  public ScrollView mScrollView;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshLayout;

  @InjectView(R.id.tv_float_exp)
  public TextView mTvFloatExp;

  @InjectView(R.id.iv_praise)
  public ImageView mIvPraise;

  @InjectView(R.id.tv_praise)
  public TextView mTvPraise;

  @InjectView(R.id.ll_praise_me)
  public LinearLayout mLlPraiseMe;

  @InjectView(R.id.tv_praise_me)
  public TextView mTvPraiseMe;

  @InjectView(R.id.ll_portraits)
  public LinearLayout mLlPortraits;

  private CameraUtil mCameraUtil;

  private boolean mIsVisitor;

  private int mViewId;

  private String mFileHash;

  private Profile mProfile;

  private long mLastRefreshTimestamp;

  @OnClick(R.id.rb_edit)
  public void onRbEditClicked() {
    ProfileFillActivity.start(getActivity(), false);
  }

  @OnClick(R.id.tv_my_friends)
  public void onLlFriendsClicked() {
    startActivity(new Intent(getActivity(), FriendListActivity.class));
  }

  @OnClick(R.id.tv_my_circles)
  public void onLlMyCirclesClicked() {
    startActivity(new Intent(getActivity(), FollowCircleListActivity.class));
  }

  @OnClick(R.id.rb_change_bg)
  public void onRbChangeBgClicked() {
    mCameraUtil.showCameraDialog();
  }

  @OnClick(R.id.tv_settings)
  public void onTvSettingsClicked() {
    startActivity(new Intent(getActivity(), MainSettingsActivity.class));
  }

  @OnClick(R.id.aiv_portrait)
  public void onAivPortraitClicked() {
    if (mIsVisitor && !isSelf()) {
      AvatarsActivity.start(getActivity(), mViewId);
    } else {
      AvatarsActivity.start(getActivity(), -1);
    }
  }

  @OnClick(R.id.ll_signature)
  public void onLlSignatureClicked() {
    SignaturesActivity.start(getActivity(), mIsVisitor, mViewId, mProfile == null ? "女" : mProfile.sex);
  }

  @OnClick(R.id.tv_my_posts)
  public void onMyPostsClicked() {
    if (mIsVisitor && !isSelf()) {
      VisitorPostsActivity.start(getActivity(), mViewId, mProfile == null ? "女" : mProfile.sex);
    } else {
      startActivity(new Intent(getActivity(), MyPostsActivity.class));
    }
  }

  @OnClick(R.id.rb_exp)
  public void onRbExpClicked() {

    getBaseActivity().showProgressBar(true);

    U.request("copywriting", new OnResponse2<CopywritingResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideProgressBar();
      }

      @Override
      public void onResponse(CopywritingResponse response) {
        if (RESTRequester.responseOk(response)) {
          final ThemedDialog dialog = new ThemedDialog(getBaseActivity());

          dialog.setTitle("等级帮助");

          TextView textView = new TextView(getBaseActivity());

          int px = U.dp2px(16);
          textView.setPadding(px, px, px, px);

          SpannableStringBuilder builder = new SpannableStringBuilder();
          builder.append("等级是随着经验值增长的,即不同的经验值对应不同的等级。\n\n");
          SpannableString s = new SpannableString("如何获取经验值？\n\n");
          s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.apptheme_primary_light_color)), 0, s.length(), 0);
          builder.append(s);
          builder.append(response.object.howToGetExperience.content);
          builder.append("\n\n等级对应的经验值，可在帮助中查看");

          textView.setText(builder);
          textView.setEms(16);

          dialog.setContent(textView);

          dialog.setPositive(R.string.got_it, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dialog.dismiss();
            }
          });
          dialog.show();
        }

        getBaseActivity().hideProgressBar();
      }
    }, CopywritingResponse.class);
  }

  @OnClick(R.id.iv_praise)
  public void onIvPraiseClicked(final View v) {
    U.request("user_praise_add", new OnResponse2<PraiseResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(final PraiseResponse response) {
        if (RESTRequester.responseOk(response)) {
          addExperienceAnimation(response.object.experience);
          mTvExp.setText(String.format("%d/%d",
              mProfile.experience + response.object.experience, mProfile.nextExperience));
          mPbExp.setProgress(mProfile.experience + response.object.experience);

          if (mIsVisitor) {
            if ("男".equals(mProfile.sex)) {
              mTvPraiseMe.setText(String.format("谁赞过他（%d）", mProfile.praisedCount + 1));
            } else if ("女".equals(mProfile.sex)) {
              mTvPraiseMe.setText(String.format("谁赞过她（%d）", mProfile.praisedCount + 1));
            }
          } else {
            mTvPraiseMe.setText(String.format("谁赞过我（%d）", mProfile.praisedCount + 1));
          }

          buildPraisedList(response.object.praisedList);

          AnimatorSet set = new AnimatorSet();
          set.playTogether(
              ObjectAnimator.ofFloat(v, "translationY", 0f, 0f, -U.dp2px(50)),
              ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.9f, 1.2f, 0.9f, 1.4f),
              ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.9f, 1.2f, 0.9f, 1.4f),
              ObjectAnimator.ofFloat(v, "alpha", 1f, 0f),
              ObjectAnimator.ofFloat(mTvPraise, "alpha", 0f, 0f, 1f),
              ObjectAnimator.ofFloat(mTvPraise, "scaleX", 1.8f, 1.8f, 1f),
              ObjectAnimator.ofFloat(mTvPraise, "scaleY", 1.8f, 1.8f, 1f)
          );
          set.setDuration(1000);

          set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
              mTvPraise.setVisibility(View.VISIBLE);
              mTvPraise.setText(String.format("已赞\n连续%d天", response.object.consecutiveTimes));
              v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
              v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
          });

          set.start();
        }
      }
    }, PraiseResponse.class, mViewId);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_profile, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    getBaseActivity().setFillContent(true);

    mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      private final int MAX_HEIGHT = U.dp2px(48);

      @Override
      public void onRefresh() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
            ObjectAnimator.ofFloat(mAivBg, "translationY", mAivBg.getTranslationY(), 0)
        );
        set.setDuration(200);
        set.start();
        requestProfile();
      }

      @Override
      public void onDrag(int value) {
        getBaseActivity().showRefreshIndicator(false);
        ViewHelper.setTranslationY(mAivBg, Math.max(Math.min(MAX_HEIGHT, value), 0));
      }

      @Override
      public void onCancel() {
        getBaseActivity().hideRefreshIndicator();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
            ObjectAnimator.ofFloat(mAivBg, "translationY", mAivBg.getTranslationY(), 0)
        );
        set.setDuration(200);
        set.start();
      }
    });

    if (getArguments() != null) {
      mIsVisitor = getArguments().getBoolean("isVisitor", false);
      mViewId = getArguments().getInt("viewId");
    }

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        File file = new File(path);
        mFileHash = IOUtils.fileHash(file);
        ImageUtils.asyncUpload(file, 75);
      }
    }, "上传背景图片");


    mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

      private int mScrollY;

      @Override
      public void onScrollChanged() {
        mScrollY = -mScrollView.getScrollY();
        ViewHelper.setTranslationY(mRbChangeBg, mScrollY);
        ViewHelper.setTranslationY(mAivBg, mScrollY);
      }
    });

    updateTopTitle();

    requestProfile();
    mLastRefreshTimestamp = System.currentTimeMillis();
  }

  private void requestProfile() {
    requestProfile(mIsVisitor && !isSelf() ? mViewId : null);
    mLastRefreshTimestamp = System.currentTimeMillis();
  }

  private boolean isSelf() {
    return mProfile != null && mProfile.isMyself == 1;
  }

  public void requestProfile(Integer userId) {
    getBaseActivity().showRefreshIndicator(true);
    U.request("profile", new OnResponse2<ProfileResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideRefreshIndicator();
        mRefreshLayout.setRefreshing(false);
        if (getBaseActivity() instanceof FragmentHolder) {
          getBaseActivity().finish();
        }
      }

      @Override
      public void onResponse(final ProfileResponse response) {
        getBaseActivity().hideRefreshIndicator();
        mRefreshLayout.setRefreshing(false);
        if (RESTRequester.responseOk(response)) {

          mProfile = response.object;
          if (TextUtils.isEmpty(mProfile.userName) && !mIsVisitor) {
            getTopBar().getAbRight().setText(getString(R.string.settings));
            getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), MainSettingsActivity.class));
              }
            });
            mFlGuide.setVisibility(View.VISIBLE);
            return;
          } else {
            getTopBar().getAbRight().setText("编辑");
            getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), ProfileEditActivity.class));
              }
            });
            mFlGuide.setVisibility(View.GONE);
          }

          SpannableStringBuilder builder = new SpannableStringBuilder(mProfile.userName);
          ImageSpan span = new ImageSpan(getActivity(),
              mProfile.sex.equals("男") ? R.drawable.ic_profile_male : R.drawable.ic_profile_female);
          builder.append("    ");
          builder.setSpan(span, builder.length() - 1, builder.length(), 0);

          mTvName.setText(builder);
          if (mProfile.birthday == -1) {
            mTvBirthday.setVisibility(View.INVISIBLE);
            mTvAge.setVisibility(View.INVISIBLE);
            mTvConstellation.setVisibility(View.INVISIBLE);
          } else {
            mTvBirthday.setVisibility(View.VISIBLE);
            mTvBirthday.setText(TimeUtil.getDate(mProfile.birthday));
            mTvAge.setVisibility(View.VISIBLE);
            mTvAge.setText(String.valueOf(mProfile.age) + "岁");
            mTvConstellation.setVisibility(View.VISIBLE);
            mTvConstellation.setText(mProfile.constellation);
          }
          mAivBg.setUrl(mProfile.background);
          mAivPortrait.setUrl(mProfile.avatar);
          if (TextUtils.isEmpty(mProfile.signature)) {
            mTvSignature.setText("还没有设置签名");
          } else {
            mTvSignature.setText(mProfile.signature);
          }
          mTvCircleName.setText(mProfile.workinFactoryName);
          mTvHometown.setText(mProfile.hometown);

          if (mIsVisitor) {
            if (isSelf()) {
              mRbChangeBg.setVisibility(View.VISIBLE);
              mTvSettings.setVisibility(View.VISIBLE);
              mTvMyCircles.setVisibility(View.VISIBLE);
              mTvMyFriends.setVisibility(View.VISIBLE);
              mTvAction.setVisibility(View.INVISIBLE);
              getBaseActivity().getTopBar().setTitle("我");

              mTvPraiseMe.setText(String.format("谁赞过我（%d）", mProfile.praisedCount));
            } else {
              mRbChangeBg.setVisibility(View.GONE);
              mTvSettings.setVisibility(View.GONE);
              mTvMyCircles.setVisibility(View.GONE);
              mTvMyFriends.setVisibility(View.GONE);
              getBaseActivity().getTopBar().setTitle(mProfile.userName);

              if (response.object.postPrivacy.equals("on")) {
                mTvMyPosts.setVisibility(View.GONE);
              } else if (response.object.postPrivacy.equals("off")) {
                mTvMyPosts.setVisibility(View.VISIBLE);
              }

              mTvAction.setVisibility(View.VISIBLE);
              if ("男".equals(mProfile.sex)) {
                mTvMyPosts.setText("他的帖子");
                mTvTitleSignature.setText("他的签名");
                mTvPraiseMe.setText(String.format("谁赞过他（%d）", mProfile.praisedCount));
              } else if ("女".equals(mProfile.sex)) {
                mTvMyPosts.setText("她的帖子");
                mTvTitleSignature.setText("她的签名");
                mTvPraiseMe.setText(String.format("谁赞过她（%d）", mProfile.praisedCount));
              } else {
                mTvMyPosts.setText("Ta的帖子");
                mTvTitleSignature.setText("Ta的签名");
                mTvPraiseMe.setText(String.format("谁赞过Ta（%d）", mProfile.praisedCount));
              }

              if (mProfile.isFriend == 1) {
                mTvAction.setText("发起聊天");
                mTvAction.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    ChatUtils.startFriendChat(getBaseActivity(), mViewId);
                  }
                });
              } else {
                mTvAction.setText("添加朋友");
                mTvAction.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    SendRequestActivity.start(v.getContext(), mViewId);
                  }
                });
              }

              if (mProfile.isFriend == 1) {
                getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_action_overflow));
                getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    showMenuDialog();
                  }
                });
              } else {
                getTopBar().getAbRight().hide();
              }

              if (mProfile.praiseConsecutiveTimes == 0) {
                mIvPraise.setVisibility(View.VISIBLE);
                mTvPraise.setVisibility(View.GONE);
              } else {
                mIvPraise.setVisibility(View.GONE);
                mTvPraise.setVisibility(View.VISIBLE);
                mTvPraise.setText(String.format("已赞\n连续%d天", mProfile.praiseConsecutiveTimes));
              }

            }
          } else {
            mRbChangeBg.setVisibility(View.VISIBLE);
            mTvSettings.setVisibility(View.VISIBLE);
            mTvMyCircles.setVisibility(View.VISIBLE);
            mTvMyFriends.setVisibility(View.VISIBLE);
            mTvAction.setVisibility(View.GONE);
            mTvPraiseMe.setText(String.format("谁赞过我（%d）", mProfile.praisedCount));

            if (Account.inst().getLastExp() != 0 && Account.inst().getLastExp() < response.object.experience) {
              addExperienceAnimation(response.object.experience - Account.inst().getLastExp());
            }

            Account.inst().setLastExp(response.object.experience);
          }

          mAivLevelIcon.setUrl(mProfile.levelIcon);
          mTvExp.setText(String.format("%d/%d", mProfile.experience, mProfile.nextExperience));
          mPbExp.setMax(mProfile.nextExperience);
          mPbExp.setProgress(mProfile.experience);

          buildPraisedList(mProfile.praisedList);

        } else {
          if (getBaseActivity() instanceof FragmentHolder) {
            getBaseActivity().finish();
          }
        }
      }
    }, ProfileResponse.class, userId);

  }

  private void addExperienceAnimation(int value) {
    mTvFloatExp.setText("+" + value);
    mTvFloatExp.setVisibility(View.VISIBLE);
    mTvFloatExp.setAlpha(0f);
    AnimatorSet set = new AnimatorSet();
    set.playTogether(
        ObjectAnimator.ofFloat(mTvFloatExp, "alpha", 0.2f, 1f, 0f),
        ObjectAnimator.ofFloat(mTvFloatExp, "translationY", 0f, -U.dp2px(20))
    );
    set.setDuration(1300);
    set.setStartDelay(300);
    set.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        mTvFloatExp.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    set.start();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      updateTopTitle();

      if (System.currentTimeMillis() - mLastRefreshTimestamp > REFRESH_INTERVAL) {
        requestProfile();
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    if (System.currentTimeMillis() - mLastRefreshTimestamp > REFRESH_INTERVAL) {
      requestProfile();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCameraUtil.onActivityResult(requestCode, resultCode, data);
  }

  @Subscribe
  public void onImageUploadEvent(final ImageUtils.ImageUploadedEvent event) {
    if (event.getHash().equals(mFileHash)) {
      Utils.updateProfile(null, null, null, null, null, event.getUrl(), null, null,
          new OnResponse2<Response>() {
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
  }

  @Subscribe
  public void onPortraitUpdatedEvent(PortraitUpdatedEvent event) {
    if (event.getUrl() != null) {
      mAivPortrait.setUrl(event.getUrl());
    }
  }

  @Subscribe
  public void onGenderUpdatedEvent(GenderUpdatedEvent event) {
    SpannableStringBuilder builder = new SpannableStringBuilder(mProfile.userName);
    ImageSpan span = new ImageSpan(getActivity(),
        event.getGender().equals("男") ? R.drawable.ic_profile_male : R.drawable.ic_profile_female);
    builder.append("   ");
    builder.setSpan(span, builder.length() - 1, builder.length(), 0);

    mTvName.setText(builder);
  }

  @Subscribe
  public void onNameUpdatedEvent(NameUpdatedEvent event) {
    mTvName.setText(event.getName());
  }

  @Subscribe
  public void onBirthdayUpdatedEvent(BirthdayUpdatedEvent event) {
    mTvBirthday.setText(TimeUtil.getDate(event.getCalendar()));
    mTvAge.setText(String.valueOf(Utils.computeAge(Calendar.getInstance(), event.getCalendar())) + "岁");
    mTvConstellation.setText(event.getConstellation());
  }

  @Subscribe
  public void onCurrentCircleNameUpdatedEvent(CurrentCircleNameUpdatedEvent event) {
    mTvCircleName.setText(event.getName());
  }

  @Subscribe
  public void onHometownUpdatedEvent(HometownUpdatedEvent event) {
    mTvHometown.setText(event.getName());
  }

  @Subscribe
  public void onSignatureUpdateEvent(SignatureUpdatedEvent event) {
    mTvSignature.setText(event.getText());
  }

  @Subscribe
  public void onProfileFilledEvent(ProfileFilledEvent event) {
    if (!mIsVisitor) {
      requestProfile(null);
    }
  }

  private void updateTopTitle() {
    getBaseActivity().setTopTitle("我");
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().showTopBar(true);
    getBaseActivity().hideRefreshIndicator();
    if (mIsVisitor && !isSelf()) {
      getBaseActivity().getTopBar().getAbRight().setText("拉黑");
      getBaseActivity().getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });
    } else {
      if (mProfile != null && TextUtils.isEmpty(mProfile.userName)) {
        getBaseActivity().getTopBar().getAbRight().setText("设置");
        getBaseActivity().getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            startActivity(new Intent(getActivity(), MainSettingsActivity.class));
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

    if (getBaseActivity() instanceof FragmentHolder) {
      getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
      getTopBar().getAbLeft().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          getBaseActivity().finish();
        }
      });
    } else {
      getTopBar().getAbLeft().hide();
    }
  }

  @Override
  protected void onTitleClicked() {

  }

  @Override
  protected void onActionLeftClicked() {
    getActivity().finish();
  }

  @Override
  protected void onActionOverflowClicked() {

  }

  private void showMenuDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    if (mProfile.isFriend == 1) {
      if (mProfile.isShielded == 1) {
        builder.setItems(
            new String[]{
                "取消屏蔽此朋友",
                "解除朋友关系",
            }, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    requestUnshield();
                    break;
                  case 1:
                    showDeleteConfirmDialog();
                    break;
                }
              }
            });
      } else {
        builder.setItems(
            new String[]{
                "屏蔽此朋友",
                "解除朋友关系",
            }, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    showBanConfirmDialog();
                    break;
                  case 1:
                    showDeleteConfirmDialog();
                    break;
                }
              }
            });
      }
    } else {
      if (mProfile.isShielded == 1) {
        builder.setItems(
            new String[]{
                "取消屏蔽此用户",
                "添加朋友"
            }, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    requestUnshield();
                    break;
                  case 1:
                    SendRequestActivity.start(getActivity(), mViewId);
                    break;
                }
              }
            });
      } else {
        builder.setItems(
            new String[]{
                "屏蔽此用户",
                "添加朋友"
            }, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    showBanConfirmDialog();
                    break;
                  case 1:
                    SendRequestActivity.start(getActivity(), mViewId);
                    break;
                }
              }
            });
      }
    }
    builder.show();
  }

  private void requestUnshield() {
    U.request("user_unshield", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          requestProfile();
        }
      }
    }, Response.class, mViewId);
  }

  private void requestDelete() {
    U.request("user_friend_del", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          requestProfile();
        }
      }
    }, Response.class, mViewId);
  }

  private void showBanConfirmDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());

    if (mProfile.isFriend == 1) {
      dialog.setTitle("确认屏蔽此朋友？");
    } else {
      dialog.setTitle("确认屏蔽此用户？");
    }

    TextView text = new TextView(getActivity());

    if (mProfile.isFriend == 1) {
      text.setText("屏蔽后将不再接收此朋友的聊天、悄悄话消息");
    } else {
      text.setText("屏蔽后将不再接收此用户的朋友请求、悄悄话消息");
    }

    int px = U.dp2px(24);
    text.setPadding(px, px, px, px);

    dialog.setContent(text);

    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        U.request("user_shield", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              requestProfile();
            }
          }
        }, Response.class, mViewId);
      }
    });

    dialog.show();
  }

  private void showDeleteConfirmDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());

    dialog.setTitle("确认解除朋友关系？");
    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        requestDelete();
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

  private void buildPraisedList(List<Profile.Portrait> praisedList) {
    mLlPortraits.removeAllViews();
    if (praisedList == null || praisedList.size() == 0) {
      mLlPraiseMe.setVisibility(View.GONE);
    } else {
      mLlPraiseMe.setVisibility(View.VISIBLE);

      for (int i = 0, s = Math.min(5, praisedList.size()); i < s; i++) {
        final Profile.Portrait portrait = praisedList.get(i);
        AsyncImageViewWithRoundCorner view = new AsyncImageViewWithRoundCorner(getActivity());
        final int size = U.dp2px(30);
        final int px = U.dp2px(4);
        view.setRadius(px);
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ProfileFragment.start(v.getContext(), portrait.viewId, "");
          }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(px, px, px, px);
        view.setLayoutParams(lp);
        view.setUrl(portrait.avatar, size, size);
        mLlPortraits.addView(view);
      }

      TextView tv = new TextView(getActivity());
      tv.setText("更多>");
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
      lp.gravity = Gravity.CENTER_VERTICAL;
      tv.setLayoutParams(lp);
      tv.setGravity(Gravity.RIGHT);
      tv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PraiseHistoryActivity.start(v.getContext(), mIsVisitor ? mViewId : -1, mIsVisitor ? mProfile.sex : "");
        }
      });

      mLlPortraits.addView(tv);
    }
  }

  @Keep
  public static class PraiseResponse extends Response {

    @SerializedName("object")
    public Praise object;

    @Keep
    public static class Praise {

      @SerializedName("consecutiveTimes")
      public int consecutiveTimes;

      @SerializedName("experience")
      public int experience;

      @SerializedName("praisedList")
      public List<Profile.Portrait> praisedList;
    }
  }

}