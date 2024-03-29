package com.utree.eightysix.app.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.FragmentHolder;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.OverlayTipUtil;
import com.utree.eightysix.app.account.ProfileFillActivity;
import com.utree.eightysix.app.account.ProfileFragment;
import com.utree.eightysix.app.account.event.ProfileFilledEvent;
import com.utree.eightysix.app.bs.BlueStarFragment;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.app.feed.event.PostCommentPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.app.feed.event.ReloadCommentEvent;
import com.utree.eightysix.app.msg.ReadMsgStore;
import com.utree.eightysix.app.publish.EmojiFragment;
import com.utree.eightysix.app.publish.EmojiViewPager;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PostCommentDeleteRequest;
import com.utree.eightysix.request.PostCommentsRequest;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.request.ReportRequest;
import com.utree.eightysix.response.CommentDeleteResponse;
import com.utree.eightysix.response.PostCommentsResponse;
import com.utree.eightysix.response.PublishCommentResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.AsyncImageViewWithRoundCorner;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.guide.Guide;

import java.util.regex.Pattern;

/**
 * @author simon
 */
@Layout(R.layout.activity_post)
public class PostActivity extends BaseActivity
    implements EmojiconGridFragment.OnEmojiconClickedListener,
    EmojiconsFragment.OnEmojiconBackspaceClickedListener {

  @InjectView(R.id.lv_comments)
  public AdvancedListView mLvComments;

  @InjectView(R.id.et_post_content)
  public EmojiconEditText mEtPostContent;

  @InjectView(R.id.iv_post)
  public ImageView mIvPost;

  @InjectView(R.id.ll_banner)
  public LinearLayout mLlBanner;

  @InjectView(R.id.fl_post_comment)
  public FrameLayout mFlPostComment;

  @InjectView(R.id.iv_emotion)
  public ImageView mIvEmotion;

  @InjectView(R.id.fl_emotion)
  public EmojiViewPager mFlEmotion;

  @InjectView(R.id.iv_anonymous)
  public ImageView mIvAnonymous;

  @InjectView(R.id.aiv_portrait)
  public AsyncImageViewWithRoundCorner mAivPortrait;

  @InjectView(R.id.tv_name)
  public TextView mTvName;

  @InjectView(R.id.iv_close)
  public ImageView mIvClose;

  @InjectView(R.id.aiv_level_icon)
  public AsyncImageView mAivLevelIcon;

  private Post mPost;

  private String mPostId;

  private PostCommentsAdapter mPostCommentsAdapter;
  private Guide mPortraitTip;
  private ThemedDialog mQuitConfirmDialog;
  private AlertDialog mCommentContextDialog;

  private boolean mGotoBottom;

  private Instrumentation mInstrumentation = new Instrumentation();

  public static void start(Context context, Post post) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("post", post);
    context.startActivity(intent);
  }

  public static void start(Context context, Post post, boolean bottom) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("bottom", bottom);
    context.startActivity(intent);
  }

  public static void start(Context context, String postId) {
    context.startActivity(getIntent(context, postId, "start_"));
  }

  public static Intent getIntent(Context context, String postId, String action) {
    Intent intent = new Intent(context, PostActivity.class);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    intent.setAction(action + postId);
    intent.putExtra("id", postId);
    return intent;
  }

  public static Intent getIntent(Context context, String postId, String action, boolean bottom) {
    Intent intent = new Intent(context, PostActivity.class);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    intent.setAction(action + postId);
    intent.putExtra("id", postId);
    intent.putExtra("bottom", bottom);
    return intent;
  }

  private static final Pattern POST_CONTENT_PATTERN = Pattern.compile("[ \r\n]*");

  @OnTextChanged(R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence text) {
    if (TextUtils.isEmpty(text) || POST_CONTENT_PATTERN.matcher(text).matches()) {
      mIvPost.setEnabled(false);
    } else {
      mIvPost.setEnabled(true);
    }
  }

  @OnClick({R.id.aiv_portrait, R.id.tv_name})
  public void onAivPortraitClicked() {
    if (!TextUtils.isEmpty(mPost.viewUserId)) {
      Bundle args = new Bundle();
      args.putInt("viewId", Integer.valueOf(mPost.viewUserId));
      args.putBoolean("isVisitor", true);
      args.putString("userName", mPost.userName);
      FragmentHolder.start(this, ProfileFragment.class, args);
    }
  }

  @OnClick(R.id.ll_banner)
  public void onLlBannerClicked() {
    mLvComments.setSelection(0);
  }

  @OnClick(R.id.iv_close)
  public void onIvCloseClicked() {
    U.getAnalyser().trackEvent(U.getContext(), "post_close", "post_close");
    finishOrShowQuitConfirmDialog();
  }

  @OnClick(R.id.iv_anonymous)
  public void onIvAnonymousClicked() {
    boolean selected = mIvAnonymous.isSelected();

    mIvAnonymous.setSelected(!selected);
    mEtPostContent.setHint(!selected ? "匿名发表评论" : "发表评论");
    Account.inst().setCommentAnonymous(!selected);
  }

  @OnItemClick(R.id.lv_comments)
  public void onLvCommentsItemClicked(final int position) {
    if (position == 0) return;

    if (mCommentContextDialog != null && mCommentContextDialog.isShowing()) {
      return;
    }

    final Comment comment = (Comment) mLvComments.getAdapter().getItem(position);
    if (comment == null) return;

    U.getAnalyser().trackEvent(this, "comment_more", "comment_more");

    String[] items;
    if (mPost.owner == 1) {
      if (comment.self == 1) {
        items = new String[]{getString(R.string.chat_anonymous),
            getString(R.string.like),
            getString(R.string.share),
            getString(R.string.report),
            getString(R.string.delete),
        };
      } else {
        items = new String[]{getString(R.string.chat_anonymous),
            getString(R.string.like),
            getString(R.string.share),
            getString(R.string.report),
            getString(R.string.delete),
            comment.ban == 1 ? getString(R.string.unban_comment) : getString(R.string.ban_comment)
        };
      }
    } else if (comment.self == 1) {
      items = new String[]{getString(R.string.chat_anonymous),
          getString(R.string.like),
          getString(R.string.share),
          getString(R.string.report),
          getString(R.string.delete)
      };
    } else {
      items = new String[]{getString(R.string.chat_anonymous),
          getString(R.string.like),
          getString(R.string.share),
          getString(R.string.report)
      };
    }

    mCommentContextDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.comment_action))
        .setItems(items,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    ChatUtils.startChat(PostActivity.this, mPost, comment);
                    break;
                  case 1:
                    if (comment.praised != 1) {
                      comment.praised = 1;
                      comment.praise++;
                      U.getBus().post(new PostCommentPraiseEvent(comment, false));
                      U.getAnalyser().trackEvent(U.getContext(), "comment_more_praise", "praise");
                      mPostCommentsAdapter.notifyDataSetChanged();
                    }
                    break;
                  case 2:
                    U.getAnalyser().trackEvent(PostActivity.this, "comment_more_share", "comment_more_share");
                    U.getShareManager().shareCommentDialog(PostActivity.this, mPost, comment.content).show();
                    break;
                  case 3:
                    U.getAnalyser().trackEvent(PostActivity.this, "comment_more_report", "comment_more_report");
                    new ReportDialog(PostActivity.this, mPost.id, comment.id).show();
                    break;
                  case 4:
                    U.getBus().post(new PostCommentDeleteRequest(mPost.id, comment.id));
                    break;
                  case 5:
                    if (comment.ban == 1) {
                      U.request("comment_unban", new OnResponse2<Response>() {
                        @Override
                        public void onResponseError(Throwable e) {

                        }

                        @Override
                        public void onResponse(Response response) {
                          if (RESTRequester.responseOk(response)) {
                            comment.ban = 0;
                          }
                        }
                      }, Response.class, mPost.id, comment.id);
                    } else {
                      showConfirmBanCommentDialog(comment);
                    }
                    break;
                }
              }
            }).create();

    mCommentContextDialog.show();
  }

  @OnClick(R.id.iv_post)
  public void onRbPostClicked() {
    U.getAnalyser().trackEvent(this, "post_comment", "post_comment");
    requestPublishComment();
  }

  @OnClick(R.id.iv_emotion)
  public void onIvEmotionClicked() {
    if (mFlEmotion.getVisibility() == View.VISIBLE) {
      mFlEmotion.setVisibility(View.GONE);
      mIvEmotion.setSelected(false);
    } else {
      hideSoftKeyboard(mEtPostContent);
      mIvEmotion.setSelected(true);


      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          mFlEmotion.setVisibility(View.VISIBLE);
        }
      }, 200);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mIvEmotion.setVisibility(View.VISIBLE);

    mIvAnonymous.setVisibility(View.VISIBLE);
    mIvAnonymous.setSelected(Account.inst().getCommentAnonymous());
    mEtPostContent.setHint(mIvAnonymous.isSelected() ? "匿名发表评论" : "发表评论");

    mLvComments.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
          if (view.getCount() == 0) return;

          View last = view.getChildAt(view.getCount() >> 1);
          if (last == null) return;
          View portraitView = last.findViewById(R.id.fpv_portrait);
          if (portraitView == null) return;

          if (Env.firstRun("overlay_tip_portrait")) {
            if (mPortraitTip == null) {
              mPortraitTip = OverlayTipUtil.getPortraitTip(portraitView, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (mPortraitTip != null) mPortraitTip.dismiss();
                }
              });
            }
            mPortraitTip.show(PostActivity.this);
            Env.setFirstRun("overlay_tip_portrait", false);
          }
        }

      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mPostCommentsAdapter != null) {
          mPostCommentsAdapter.getPostPostView().mVpContent.setAlpha(1f);

          if (mPost != null) {
            PostPostView postPostView = mPostCommentsAdapter.getPostPostView();
            if (postPostView.getParent() != null) {
              int top = postPostView.getTop();
              if (top < -40) {
                mLlBanner.setClickable(true);
              } else {
                mLlBanner.setClickable(false);
              }
              int height = postPostView.getMeasuredHeight();
              if (TextUtils.isEmpty(mPost.userName)) {
                mLlBanner.setBackgroundColor((int) (0x88 * ((-top) / (float) height)) << 24);
              } else {
                mLlBanner.setBackgroundColor(((0xff - (int) (0x88 * (-top / (float) height))) << 24) | 0x00ffffff);
              }
            }
          }
        }
      }
    });

    mLvComments.setOnTouchListener(new View.OnTouchListener() {
      private float lastY;
      private float y;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (mPostCommentsAdapter == null) {
          return false;
        }

        if (mPostCommentsAdapter.getPostPostView().getTop() == 0) {
          switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
              lastY = MotionEventCompat.getY(event, 0);
            case MotionEvent.ACTION_MOVE:
              y = MotionEventCompat.getY(event, 0);

              if (y > lastY) {
                mPostCommentsAdapter.getPostPostView()
                    .mVpContent.setAlpha(Math.max(0f, 1f - ((y - lastY) / U.dp2px(80))));
              }
              break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
              lastY = 0;
              mPostCommentsAdapter.getPostPostView().mVpContent.setAlpha(1f);
              break;
          }
        }
        return false;
      }
    });

    mIvPost.setEnabled(false);

    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fl_emotion, EmojiFragment.newInstance())
        .commitAllowingStateLoss();

    mFlEmotion.setFragmentManager(getSupportFragmentManager());


    onNewIntent(getIntent());
  }

  @Override
  protected void onResume() {
    super.onResume();
    M.getRegisterHelper().register(mLvComments);

    hideSoftKeyboard(mEtPostContent);
  }

  @Override
  protected void onPause() {
    super.onPause();
    M.getRegisterHelper().unregister(mLvComments);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    mPost = intent.getParcelableExtra("post");
    mPostId = intent.getStringExtra("id");
    mGotoBottom = intent.getBooleanExtra("bottom", false);

    if (mPost == null) {
      if (TextUtils.isEmpty(mPostId)) {
        showToast(getString(R.string.post_not_found), false);
        finish();
      }
    } else {
      mPostCommentsAdapter = new PostCommentsAdapter(this, mPost, null);
      mLvComments.setAdapter(mPostCommentsAdapter);
      if (!TextUtils.isEmpty(mPost.userName)) {
        mTvName.setVisibility(View.VISIBLE);
        mAivPortrait.setVisibility(View.VISIBLE);
        mAivLevelIcon.setVisibility(View.VISIBLE);
        mTvName.setText(mPost.userName);
        mAivPortrait.setUrl(mPost.avatar);
        mAivLevelIcon.setUrl(mPost.levelIcon);
        mLlBanner.setBackgroundColor(Color.WHITE);
        mIvClose.setImageResource(R.drawable.ic_action_post_close);
      } else {
        mTvName.setVisibility(View.GONE);
        mAivPortrait.setVisibility(View.GONE);
        mAivLevelIcon.setVisibility(View.GONE);
        mLlBanner.setBackgroundColor(Color.TRANSPARENT);
        mIvClose.setImageResource(R.drawable.ic_action_post_close_white);
      }
    }

    cacheOutComments(1, mGotoBottom);
  }

  @Override
  public void onBackPressed() {
    if (mPortraitTip != null && mPortraitTip.isShowing()) {
      mPortraitTip.dismiss();
    } else if (mFlEmotion.getVisibility() == View.VISIBLE) {
      mFlEmotion.setVisibility(View.GONE);
      mIvEmotion.setSelected(false);
    } else {
      finishOrShowQuitConfirmDialog();
    }
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }


  @Subscribe
  public void onPostCommentPraiseEvent(final PostCommentPraiseEvent event) {
    if (!event.isCancel()) {

      U.request("comment_praise", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {
          if (!RESTRequester.responseOk(response)) {
            event.getComment().praised = 0;
            event.getComment().praise--;
            mPostCommentsAdapter.notifyDataSetChanged();
          }
        }
      }, Response.class, mPost.id, event.getComment().id);
    }
  }

  @Subscribe
  public void onPostPostPraiseEvent(final PostPostPraiseEvent event) {

    if (!event.isCancel()) {
      U.request("post_praise", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(event.getPost());
          } else if ((response.code & 0xffff) == 0x2286) {
            event.getPost().praised = 1;
          } else {
            event.getPost().praised = 1;
            event.getPost().praise = Math.max(0, event.getPost().praise - 1);
          }
          mPostCommentsAdapter.notifyDataSetChanged();
        }
      }, Response.class, event.getPost().id);
    }
  }

  @Subscribe
  public void onPostCommentDeleteRequest(final PostCommentDeleteRequest request) {
    U.request("comment_delete", new OnResponse2<CommentDeleteResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(CommentDeleteResponse response) {
        if (RESTRequester.responseOk(response)) {
          for (Comment c : mPostCommentsAdapter.mComments) {
            if (c == null) continue;
            if (c.id.equals(request.commentId)) {
              c.delete = 1;
              c.content = response.object.reason;
              mPostCommentsAdapter.notifyDataSetChanged();
              break;
            }
          }
        }
      }
    }, CommentDeleteResponse.class, request.postId, request.commentId);
  }

  @Subscribe
  public void onReportRequest(final ReportRequest reportRequest) {
    request(reportRequest, new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          showToast(getString(R.string.report_succeed));
        }
      }
    }, Response.class);
  }

  @Subscribe
  public void onPostDeleteRequest(PostDeleteRequest request) {
    U.request("post_delete", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.getBus().post(new PostDeleteEvent(mPost));
          finish();
        }
      }
    }, Response.class, request.postId);
  }

  @Subscribe
  public void onReloadCommentEvent(ReloadCommentEvent event) {
    requestComment(1, mGotoBottom);
  }

  void finishOrShowQuitConfirmDialog() {
    if (mEtPostContent.getText().length() == 0) {
      finish();
      return;
    }
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

  private BlueStarFragment mBlueStarFragment;

  private void showBlueStarFragment(int starType, String starToken) {
    if (mBlueStarFragment == null) {
      mBlueStarFragment = new BlueStarFragment();
      Bundle bundle = new Bundle();
      bundle.putInt("starType", starType);
      bundle.putParcelable("post", mPost);
      bundle.putString("starToken", starToken);
      mBlueStarFragment.setArguments(bundle);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content, mBlueStarFragment)
          .commit();
    } else {
      getSupportFragmentManager().beginTransaction()
          .attach(mBlueStarFragment)
          .commit();
    }
  }

  private void showConfirmBanCommentDialog(final Comment comment) {
    final ThemedDialog dialog = new ThemedDialog(this);
    dialog.setTitle("确认禁止TA评论此帖？");

    TextView textView = new TextView(this);
    textView.setText("提醒：确认后,TA将不能再评论此帖子");
    int px = U.dp2px(16);
    textView.setPadding(px, px, px, px);

    dialog.setContent(textView);

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        U.request("comment_ban", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              comment.ban = 1;
            }
          }
        }, Response.class, mPost.id, comment.id);
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

  private void requestComment(final int page, final boolean bottom) {
    final String id = mPost == null ? mPostId : mPost.id;
    final int viewType = mPost == null ? 0 : mPost.viewType;
    final int isHot = mPost == null ? 0 : mPost.isHot;
    final int isRepost = mPost == null ? 0 : mPost.isRepost;
    showProgressBar();
    ReadMsgStore.inst().addRead(id);
    U.getAnalyser().trackEvent(this, "post_load", "post_load");
    U.request("post_comments", new OnResponse2<PostCommentsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(PostCommentsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mPostCommentsAdapter = new PostCommentsAdapter(PostActivity.this, response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);
          mPost = response.object.post;
          mPostCommentsAdapter.setNeedReload(false);

          if (!TextUtils.isEmpty(mPost.userName)) {
            mTvName.setVisibility(View.VISIBLE);
            mAivPortrait.setVisibility(View.VISIBLE);
            mAivLevelIcon.setVisibility(View.VISIBLE);
            mTvName.setText(mPost.userName);
            mAivPortrait.setUrl(mPost.avatar);
            mAivLevelIcon.setUrl(mPost.levelIcon);
            mLlBanner.setBackgroundColor(Color.WHITE);
            mIvClose.setImageResource(R.drawable.ic_action_post_close);
          } else {
            mTvName.setVisibility(View.GONE);
            mAivPortrait.setVisibility(View.GONE);
            mAivLevelIcon.setVisibility(View.GONE);
            mLlBanner.setBackgroundColor(Color.TRANSPARENT);
            mIvClose.setImageResource(R.drawable.ic_action_post_close_white);
          }

          if (response.object.blueStar == 1) {
            showBlueStarFragment(response.object.blueStarType, response.object.starToken);
          }

          U.getBus().post(mPost);
        } else if ((response.code & 0xffff) == 0x2120) {
          finish();
        } else {
          if (mPostCommentsAdapter != null && mPostCommentsAdapter.getCount() == 1) {
            mPostCommentsAdapter.setNeedReload(true);
          }
        }

        if (!Account.inst().hasCancelCommentAnonymousSet()) {
          if (!TextUtils.isEmpty(mPost.userName) && mPost.owner == 1) {
            mIvAnonymous.setSelected(false);
          }
        }

        if (bottom) {
          mLvComments.setSelection(Integer.MAX_VALUE);
          mLlBanner.setClickable(true);
          if (TextUtils.isEmpty(mPost.userName)) {
            mLlBanner.setBackgroundColor(0x88000000);
          } else {
            mLlBanner.setBackgroundColor(0x88ffffff);
          }
        }

        hideProgressBar();

      }
    }, PostCommentsResponse.class, id, viewType, isHot, isRepost, page);
  }

  private void cacheOutComments(final int page, final boolean bottom) {
    final String id = mPost == null ? mPostId : mPost.id;
    final int viewType = mPost == null ? 0 : mPost.viewType;
    final int isHot = mPost == null ? 0 : mPost.isHot;
    final int isRepost = mPost == null ? 0 : mPost.isRepost;
    cacheOut(new PostCommentsRequest(id, viewType, isHot, isRepost, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mPostCommentsAdapter = new PostCommentsAdapter(PostActivity.this, response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);
          mPost = response.object.post;

          if (bottom) {
            mLvComments.setSelection(Integer.MAX_VALUE);
          }
        } else {
          showProgressBar();
        }
        requestComment(page, mGotoBottom);
      }
    }, PostCommentsResponse.class);
  }

  private void requestPublishComment() {
    if (TextUtils.isEmpty(mPost.viewerName)) {
      ProfileFillActivity.start(PostActivity.this, false);
      return;
    }

    mEtPostContent.setEnabled(false);
    mIvPost.setEnabled(false);
    mFlEmotion.setVisibility(View.GONE);
    mIvEmotion.setSelected(false);
    showProgressBar();

    U.request("post_comment", new OnResponse2<PublishCommentResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mIvPost.setEnabled(true);
      }

      @Override
      public void onResponse(PublishCommentResponse response) {
        if (RESTRequester.responseOk(response)) {
          mPostCommentsAdapter.add(response.object);
          mPost.comments++;
          mPost.relation = 1;
          U.getBus().post(mPost);
          mEtPostContent.setText("");
          mIvPost.setEnabled(false);
        } else {
          mIvPost.setEnabled(true);
        }

        hideProgressBar();
        mEtPostContent.setEnabled(true);
        mLvComments.setSelection(Integer.MAX_VALUE);

        requestComment(1, true);
      }
    }, PublishCommentResponse.class, mEtPostContent.getText().toString(), mPost.id, mIvAnonymous.isSelected() ? 0 : 1);
  }

  @Subscribe
  public void onProfileFilledEvent(ProfileFilledEvent event) {
    requestComment(1, true);
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
      String text = mEtPostContent.getText().toString();
      String before = text.substring(0, mEtPostContent.getSelectionStart());
      String after = text.substring(mEtPostContent.getSelectionEnd());

      mEtPostContent.setText(before + emojicon.getEmoji() + after);
      mEtPostContent.setSelection(before.length() + emojicon.getEmoji().length());
    }
  }

}