package com.utree.eightysix.app.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
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

/**
 */
public class ProfileFragment extends HolderFragment {

  public static void start(Context context, int viewId, String userName) {
    Bundle args = new Bundle();
    args.putBoolean("isVisitor", true);
    args.putInt("viewId", viewId);
    args.putString("userName", userName);

    FragmentHolder.start(context, ProfileFragment.class, args);
  }

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

  private CameraUtil mCameraUtil;
  private boolean mIsVisitor;
  private int mViewId;
  private String mFileHash;
  private Profile mProfile;

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
  public void onRbChageBgClicked() {
    mCameraUtil.showCameraDialog();
  }

  @OnClick(R.id.tv_settings)
  public void onTvSettingsClicked() {
    startActivity(new Intent(getActivity(), MainSettingsActivity.class));
  }

  @OnClick(R.id.aiv_portrait)
  public void onAivPortraitClicked() {
    if (mIsVisitor) {
      AvatarViewerActivity.start(getActivity(), 0, mViewId);
    } else {
      AvatarViewerActivity.start(getActivity(), 0, -1);
    }
  }

  @OnClick(R.id.ll_signature)
  public void onLlSignatureClicked() {
    SignaturesActivity.start(getActivity(), mIsVisitor, mViewId);
  }

  @OnClick(R.id.tv_my_posts)
  public void onMyPostsClicked() {
    if (mIsVisitor) {
      VisitorPostsActivity.start(getActivity(), mViewId);
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

          String format = String.format("%s\n\n%s\n\n%s",
              "等级是随着经验值增长的,即不同的经验值对应不同的等级。\n" +
                  " \n" +
                  "如何获取经验值？", response.object.howToGetExperience.content, "等级对应的经验值，可在帮助中查看");

          textView.setText(format);
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

    ThemedDialog dialog = new ThemedDialog(getActivity());

    dialog.setTitle("等级帮助");
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
      @Override
      public void onRefresh() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
            ObjectAnimator.ofFloat(mAivBg, "scaleX", mAivBg.getScaleX(), 1f),
            ObjectAnimator.ofFloat(mAivBg, "scaleY", mAivBg.getScaleY(), 1f)

        );
        set.setDuration(200);
        set.start();
        requestProfile(mIsVisitor ? mViewId : null);
      }

      @Override
      public void onDrag(int value) {
        mAivBg.setScaleX(1f + (value / (float) U.dp2px(500)));
        mAivBg.setScaleY(1f + (value / (float) U.dp2px(500)));
      }

      @Override
      public void onCancel() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
            ObjectAnimator.ofFloat(mAivBg, "scaleX", mAivBg.getScaleX(), 1f),
            ObjectAnimator.ofFloat(mAivBg, "scaleY", mAivBg.getScaleY(), 1f)

        );
        set.setDuration(200);
        set.start();
      }
    });

    mRefreshLayout.setColorSchemeResources(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    if (getArguments() != null) {
      mIsVisitor = getArguments().getBoolean("isVisitor", false);
      mViewId = getArguments().getInt("viewId");
      getBaseActivity().getTopBar().setTitle(getArguments().getString("userName"));
      getBaseActivity().getTopBar().getAbRight().hide();
    }

    if (mIsVisitor) {
      getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
      getTopBar().getAbLeft().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          getBaseActivity().finish();
        }
      });
    }

    mCameraUtil = new CameraUtil(this, new CameraUtil.Callback() {
      @Override
      public void onImageReturn(String path) {
        File file = new File(path);
        mFileHash = IOUtils.fileHash(file);
        ImageUtils.asyncUpload(file);
      }
    });


    if (mIsVisitor) {
      mRbChangeBg.setVisibility(View.GONE);
      mTvSettings.setVisibility(View.GONE);
      mTvMyCircles.setVisibility(View.GONE);
      mTvMyFriends.setVisibility(View.GONE);
      mTvMyPosts.setText("他的帖子");
      mTvTitleSignature.setText("他的签名");
      mTvAction.setVisibility(View.VISIBLE);
    } else {
      mRbChangeBg.setVisibility(View.VISIBLE);
      mTvSettings.setVisibility(View.VISIBLE);
      mTvMyCircles.setVisibility(View.VISIBLE);
      mTvMyFriends.setVisibility(View.VISIBLE);
      mTvAction.setVisibility(View.GONE);
    }

    mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

      private int mScrollY;

      @Override
      public void onScrollChanged() {
        mScrollY = -mScrollView.getScrollY();
        mRbChangeBg.setTranslationY(mScrollY);
        mAivBg.setTranslationY(mScrollY);
      }
    });

    requestProfile(mIsVisitor ? mViewId : null);
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
      public void onResponse(final ProfileResponse response) {
        getBaseActivity().hideRefreshIndicator();
        mRefreshLayout.setRefreshing(false);
        if (RESTRequester.responseOk(response)) {

          mProfile = response.object;
          if (TextUtils.isEmpty(mProfile.userName)) {
            mFlGuide.setVisibility(View.VISIBLE);
            return;
          } else {
            mFlGuide.setVisibility(View.GONE);
          }

          mTvName.setText(mProfile.userName);
          mTvAge.setText(String.valueOf(mProfile.age) + "岁");
          if (mProfile.birthday == -1) {
            mTvBirthday.setVisibility(View.GONE);
          } else {
            mTvBirthday.setVisibility(View.VISIBLE);
            mTvBirthday.setText(TimeUtil.getDate(mProfile.birthday));
          }
          mTvGender.setText(mProfile.sex);
          mTvConstellation.setText(mProfile.constellation);
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
            if ("男".equals(mProfile.sex)) {
              mTvMyPosts.setText("他的帖子");
              mTvTitleSignature.setText("他的签名");
            } else if ("女".equals(mProfile.sex)) {
              mTvMyPosts.setText("她的帖子");
              mTvTitleSignature.setText("她的签名");
            } else {
              mTvMyPosts.setText("Ta的帖子");
              mTvTitleSignature.setText("Ta的签名");
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
              getTopBar().getAbRight().setDrawable(getResources().getDrawable(R.drawable.ic_action_ban));
              getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  showMenuDialog();
                }
              });
            } else {
              getTopBar().getAbRight().hide();
            }
          }

          mAivLevelIcon.setUrl(mProfile.levelIcon);
          mTvExp.setText(String.format("%d/%d", mProfile.experience, mProfile.nextExperience));
          mPbExp.setMax(mProfile.nextExperience);
          mPbExp.setProgress(mProfile.experience);
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
    mTvGender.setText(event.getGender());
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
    getBaseActivity().getTopBar().getAbRight().hide();
    getBaseActivity().getTopBar().getAbLeft().hide();
    getBaseActivity().showTopBar(true);
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
          requestProfile(mViewId);
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
          requestProfile(mViewId);
        }
      }
    }, Response.class, mViewId);
  }

  private void showBanConfirmDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());

    dialog.setTitle("确认拉黑此用户？");

    TextView text = new TextView(getActivity());

    text.setText("拉黑将不再接收此用户的聊天、悄悄话、好友请求等信息");
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
              requestProfile(mViewId);
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
}