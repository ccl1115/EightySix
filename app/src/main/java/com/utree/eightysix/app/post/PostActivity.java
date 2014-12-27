package com.utree.eightysix.app.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.*;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.OverlayTipUtil;
import com.utree.eightysix.app.bs.BlueStarFragment;
import com.utree.eightysix.app.chat.ChatUtils;
import com.utree.eightysix.app.feed.event.*;
import com.utree.eightysix.app.msg.ReadMsgStore;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.*;
import com.utree.eightysix.response.CommentDeleteResponse;
import com.utree.eightysix.response.PostCommentsResponse;
import com.utree.eightysix.response.PublishCommentResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.ThemedDialog;
import com.utree.eightysix.widget.guide.Guide;
import de.akquinet.android.androlog.Log;

import java.util.regex.Pattern;

/**
 * @author simon
 */
@Layout (R.layout.activity_post)
public class PostActivity extends BaseActivity {

  @InjectView (R.id.lv_comments)
  public AdvancedListView mLvComments;

  @InjectView (R.id.et_post_content)
  public EditText mEtPostContent;

  @InjectView(R.id.iv_post)
  public ImageView mIvPost;

  @InjectView(R.id.fl_banner)
  public FrameLayout mFlBanner;

  @InjectView(R.id.fl_post_comment)
  public FrameLayout mFlPostComment;

  private Post mPost;

  private String mPostId;

  private PostCommentsAdapter mPostCommentsAdapter;
  private Guide mPortraitTip;
  private ThemedDialog mQuitConfirmDialog;
  private AlertDialog mCommentContextDialog;

  private boolean mGotoBottom;

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

  @OnTextChanged (R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence text) {
    if (TextUtils.isEmpty(text) || POST_CONTENT_PATTERN.matcher(text).matches()) {
      mIvPost.setEnabled(false);
    } else {
      mIvPost.setEnabled(true);
    }
  }

  @OnClick(R.id.fl_banner)
  public void onFlBannerClicked() {
    mLvComments.setSelection(0);
  }

  @OnClick(R.id.iv_close)
  public void onIvCloseClicked() {
    U.getAnalyser().trackEvent(U.getContext(), "post_close", "post_close");
    finishOrShowQuitConfirmDialog();
  }

  @OnClick(R.id.iv_more)
  public void onIvMoreClicked() {
    if (mPost == null) return;

    U.getAnalyser().trackEvent(U.getContext(), "post_more", "post_more");
    String[] items;
    if (mPost.owner == 1) {
      items = new String[]{U.gs(R.string.share),
          getString(R.string.start_chat),
          getString(R.string.report),
          getString(R.string.like),
          getString(R.string.delete)};
    } else {
      items = new String[]{U.gs(R.string.share),
          getString(R.string.start_chat),
          getString(R.string.report),
          getString(R.string.like)};
    }
    new AlertDialog.Builder(this).setTitle(U.gs(R.string.post_action))
        .setItems(items,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_share", "post_more_share");
                    U.getShareManager().sharePostDialog(PostActivity.this, mPost).show();
                    break;
                  case 1:
                    ChatUtils.startChat(PostActivity.this, mPost);
                    break;
                  case 2:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_report", "post_more_report");
                    new ReportDialog(PostActivity.this, mPost.id).show();
                    break;
                  case 3:
                    if (mPost == null) return;
                    if (mPost.praised != 1) {
                      U.getAnalyser().trackEvent(U.getContext(), "post_more_praise", "praise");
                      mPostCommentsAdapter.getPostPostView().doPraise();
                    }
                    mPostCommentsAdapter.notifyDataSetChanged();
                    break;
                  case 4:
                    U.getAnalyser().trackEvent(U.getContext(), "post_more_delete", "post_more_delete");
                    U.getBus().post(new PostDeleteRequest(mPost.id));
                    break;
                }
              }
            }).create().show();
  }


  @OnItemClick (R.id.lv_comments)
  public void onLvCommentsItemClicked(final int position) {
    if (position == 0) return;

    if (mCommentContextDialog != null && mCommentContextDialog.isShowing()) {
      return;
    }

    final Comment comment = (Comment) mLvComments.getAdapter().getItem(position);
    if (comment == null) return;

    U.getAnalyser().trackEvent(this, "comment_more", "comment_more");

    String[] items;
    if (comment.self == 1) {
      items = new String[]{getString(R.string.start_chat), getString(R.string.like), getString(R.string.share), getString(R.string.report), getString(R.string.delete)};
    } else {
      items = new String[]{getString(R.string.start_chat), getString(R.string.like), getString(R.string.share), getString(R.string.report)};
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
                    new ReportDialog(PostActivity.this, mPostId, comment.id).show();
                    break;
                  case 4:
                    U.getBus().post(new PostCommentDeleteRequest(mPost.id, comment.id));
                    break;
                }
              }
            }).create();

    mCommentContextDialog.show();
  }

  @OnClick(R.id.iv_post)
  public void onRbPostClicked() {
    U.getAnalyser().trackEvent(this, "post_comment", "post_comment");
    showProgressBar();
    mEtPostContent.setEnabled(false);
    mIvPost.setEnabled(false);
    requestPublishComment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

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
          PostPostView postPostView = mPostCommentsAdapter.getPostPostView();
          if (postPostView.getParent() != null) {
            int top = postPostView.getTop();
            if (top <= 0 && top >= -40) {
              mFlBanner.setClickable(false);
            } else {
              mFlBanner.setClickable(true);
            }
            mFlBanner.setBackgroundColor((int) (0x88 * ((-top) / (float) postPostView.getMeasuredHeight())) << 24);
          } else {
            mFlBanner.setBackgroundColor(0x88000000);
          }
        }
      }
    });

    mIvPost.setEnabled(false);

    onNewIntent(getIntent());
  }

  @Override
  protected void onResume() {
    super.onResume();
    M.getRegisterHelper().register(mLvComments);
  }

  @Override
  protected void onPause() {
    super.onPause();
    M.getRegisterHelper().unregister(mLvComments);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mPostId != null) {
      U.getBus().post(new RefreshFeedEvent());
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    mPost = intent.getParcelableExtra("post");
    mPostId = intent.getStringExtra("id");
    mGotoBottom = intent.getBooleanExtra("bottom", false);

    Log.d("PostActivity", "postId: " + ((mPost != null) ? mPost.id : mPostId));

    if (mPost == null && TextUtils.isEmpty(mPostId)) {
      showToast(getString(R.string.post_not_found), false);
      finish();
    } else {
      mPostCommentsAdapter = new PostCommentsAdapter(this, mPost, null);
      mLvComments.setAdapter(mPostCommentsAdapter);
    }

    cacheOutComments(1, mGotoBottom);
  }

  @Override
  public void onBackPressed() {
    if (mPortraitTip != null && mPortraitTip.isShowing()) {
      mPortraitTip.dismiss();
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
      request(new CommentPraiseRequest(mPost.id, event.getComment().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (!RESTRequester.responseOk(response)) {
            event.getComment().praised = 0;
            event.getComment().praise--;
            mPostCommentsAdapter.notifyDataSetChanged();
          }
        }
      }, Response.class);
    }
  }

  @Subscribe
  public void onPostPostPraiseEvent(final PostPostPraiseEvent event) {

    if (!event.isCancel()) {
      request(new PostPraiseRequest(event.getPost().id), new OnResponse2<Response>() {
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

        @Override
        public void onResponseError(Throwable e) {
        }
      }, Response.class);
    }
  }

  @Subscribe
  public void onPostCommentDeleteRequest(final PostCommentDeleteRequest request) {
    request(request, new OnResponse<CommentDeleteResponse>() {
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
    }, CommentDeleteResponse.class);
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
    request(request, new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.getBus().post(new PostDeleteEvent(mPost));
          finish();
        }
      }
    }, Response.class);
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

  private void requestComment(final int page, final boolean bottom) {
    final String id = mPost == null ? mPostId : mPost.id;
    final int viewType = mPost == null ? 0 : mPost.viewType;
    final int isHot = mPost == null ? 0 : mPost.isHot;
    final int isRepost = mPost == null ? 0 : mPost.isRepost;
    showProgressBar();
    ReadMsgStore.inst().addRead(id);
    U.getAnalyser().trackEvent(this, "post_load", "post_load");
    request(new PostCommentsRequest(id, viewType, isHot, isRepost, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mPostCommentsAdapter = new PostCommentsAdapter(PostActivity.this, response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);
          mPost = response.object.post;
          mPostCommentsAdapter.setNeedReload(false);


          if (response.object.blueStar == 1) {
            showBlueStarFragment(response.object.blueStarType, response.object.starToken);
          }

          U.getBus().post(mPost);
        } else {
          if (mPostCommentsAdapter != null && mPostCommentsAdapter.getCount() == 1) {
            mPostCommentsAdapter.setNeedReload(true);
          }
        }

        if (bottom) {
          mLvComments.setSelection(Integer.MAX_VALUE);
        }

        hideProgressBar();
      }
    }, PostCommentsResponse.class);
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
    request(new PublishCommentRequest(mEtPostContent.getText().toString(), mPost.id),
        new OnResponse<PublishCommentResponse>() {
          @Override
          public void onResponse(PublishCommentResponse response) {
            if (RESTRequester.responseOk(response)) {
              mPostCommentsAdapter.add(response.object);
              mPost.comments++;
              mPost.relation = 1;
              U.getBus().post(mPost);
              mEtPostContent.setText("");
            }

            hideProgressBar();
            mEtPostContent.setEnabled(true);
            mIvPost.setEnabled(false);
            mLvComments.setSelection(Integer.MAX_VALUE);

            requestComment(1, true);
          }
        }, PublishCommentResponse.class);
  }

}