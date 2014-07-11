package com.utree.eightysix.app.feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.event.PostCommentPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.PostPostPraiseEvent;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.request.CommentPraiseCancelRequest;
import com.utree.eightysix.request.CommentPraiseRequest;
import com.utree.eightysix.request.PostCommentDeleteRequest;
import com.utree.eightysix.request.PostCommentsRequest;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.request.PostPraiseCancelRequest;
import com.utree.eightysix.request.PostPraiseRequest;
import com.utree.eightysix.request.PublishCommentRequest;
import com.utree.eightysix.response.PostCommentsResponse;
import com.utree.eightysix.response.PublishCommentResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;
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

  @InjectView (R.id.rb_post)
  public RoundedButton mRbPost;

  private Post mPost;

  private int mFactoryId;

  private PostCommentsAdapter mPostCommentsAdapter;
  private boolean mResumed;

  public static void start(Context context, Post post, Rect rect) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("rect", rect);
    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, String postId) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("id", postId);
    return intent;
  }

  public static void start(Context context, String postId) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("id", postId);
    context.startActivity(intent);
  }

  private static final Pattern POST_CONTENT_PATTERN = Pattern.compile("[ \r\n]*");

  @OnTextChanged (R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence text) {
    if (TextUtils.isEmpty(text) || POST_CONTENT_PATTERN.matcher(text).matches()) {
      mRbPost.setEnabled(false);
    } else {
      mRbPost.setEnabled(true);
    }
  }

  @OnItemClick (R.id.lv_comments)
  public void onLvCommentsItemClicked(final int position) {
    if (position == 0) return;

    final Comment comment = (Comment) mLvComments.getAdapter().getItem(position);
    if (comment == null) return;

    String[] items;
    String like = comment.praised == 1 ? getString(R.string.unlike) : getString(R.string.like);
    if (comment.self == 1) {
      items = new String[]{like, getString(R.string.share), getString(R.string.report), getString(R.string.delete)};
    } else {
      items = new String[]{like, getString(R.string.share), getString(R.string.report)};
    }
    new AlertDialog.Builder(this).setTitle(getString(R.string.comment_action))
        .setItems(items,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    comment.praised = comment.praised == 1 ? 0 : 1;
                    if (comment.praised == 1) {
                      comment.praise++;
                      U.getBus().post(new PostCommentPraiseEvent(comment, false));
                    } else {
                      comment.praise--;
                      U.getBus().post(new PostCommentPraiseEvent(comment, true));
                    }
                    mPostCommentsAdapter.notifyDataSetChanged();
                    break;
                  case 1:
                    ShareUtils.shareCommentToQQ(PostActivity.this, comment);
                    break;
                  case 2:
                    showToast("TODO report");
                    break;
                  case 3:
                    U.getBus().post(new PostCommentDeleteRequest(mPost.id, comment.id));
                    break;
                }
              }
            }).create().show();
  }

  @OnClick (R.id.rb_post)
  public void onRbPostClicked() {
    showProgressBar();
    mEtPostContent.setEnabled(false);
    mRbPost.setEnabled(false);
    requestPublishComment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mPost = (Post) getIntent().getSerializableExtra("post");

    if (mPost == null && U.useFixture()) {
      mPost = U.getFixture(Post.class, "valid");
    } else {
      mPostCommentsAdapter = new PostCommentsAdapter(mPost, null);
      mLvComments.setAdapter(mPostCommentsAdapter);
    }

  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getBus().register(mLvComments);

    if (!mResumed) {
      Rect rect = getIntent().getParcelableExtra("rect");
      if (rect != null) {
        overridePendingTransition(0, 0);
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        mLvComments.setVisibility(View.INVISIBLE);
        final PostPostView tmp = (PostPostView) mPostCommentsAdapter.getView(0, null, mLvComments);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rect.width(), rect.height());
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        ((ViewGroup) findViewById(android.R.id.content)).addView(tmp, lp);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
            ObjectAnimator.ofFloat(tmp, "scaleX", 1f, screenWidth / (float) rect.width()),
            ObjectAnimator.ofFloat(tmp, "scaleY", 1f, screenWidth / (float) rect.height()),
            ObjectAnimator.ofFloat(tmp, "translationY", rect.top - U.dp2px(22), U.dp2px(8))
        );
        set.setDuration(500);
        set.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {

          }

          @Override
          public void onAnimationEnd(Animator animation) {
            ((ViewGroup) findViewById(android.R.id.content)).removeView(tmp);
            mLvComments.setVisibility(View.VISIBLE);

            if (mPost != null) {
              cacheOutComments(mPost.id, 1);
            } else {
              cacheOutComments(getIntent().getStringExtra("id"), 1);
            }
          }

          @Override
          public void onAnimationCancel(Animator animation) {

          }

          @Override
          public void onAnimationRepeat(Animator animation) {

          }
        });
        set.start();
      } else {
        if (mPost != null) {
          cacheOutComments(mPost.id, 1);
        } else {
          cacheOutComments(getIntent().getStringExtra("id"), 1);
        }
      }
    }
    mResumed = true;

  }

  @Override
  protected void onPause() {
    super.onPause();
    U.getBus().unregister(mLvComments);
  }

  @Override
  protected void onActionLeftOnClicked() {
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onPostCommentPraiseEvent(final PostCommentPraiseEvent event) {
    if (event.isCancel()) {
      request(new CommentPraiseCancelRequest(mPost.id, event.getComment().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (response == null || response.code != 0) {
            event.getComment().praised = 1;
            event.getComment().praise++;
            mPostCommentsAdapter.notifyDataSetChanged();
          }
        }
      }, Response.class);
    } else {
      request(new CommentPraiseRequest(mPost.id, event.getComment().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (response == null || response.code != 0) {
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
    if (event.isCancel()) {
      request(new PostPraiseCancelRequest(event.getPost().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(event.getPost());
          } else {
            event.getPost().praised = 0;
            event.getPost().praise--;
            mPostCommentsAdapter.notifyDataSetChanged();
          }
        }
      }, Response.class);
    } else {
      request(new PostPraiseRequest(event.getPost().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (response == null || response.code != 0) {
            event.getPost().praised = 1;
            event.getPost().praise--;
            mPostCommentsAdapter.notifyDataSetChanged();
          } else {
            U.getBus().post(event.getPost());
          }
        }
      }, Response.class);
    }
  }

  @Subscribe
  public void onPostCommentDeleteRequest(final PostCommentDeleteRequest request) {
    request(request, new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          mPostCommentsAdapter.remove(request.commentId);
          mPost.comments = Math.max(0, mPost.comments - 1);
          U.getBus().post(mPost);
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

  private void requestComment(final int page) {
    request(new PostCommentsRequest(mPost.id, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mPostCommentsAdapter = new PostCommentsAdapter(response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);
          mPost = response.object.post;
          U.getBus().post(mPost);
        }
        hideProgressBar();
      }
    }, PostCommentsResponse.class);
  }

  private void cacheOutComments(final String id, final int page) {
    cacheOut(new PostCommentsRequest(id, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mPostCommentsAdapter = new PostCommentsAdapter(response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);
          if (mPost == null) mPost = response.object.post;
        } else {
          showProgressBar();
        }
        requestComment(page);
      }
    }, PostCommentsResponse.class);
  }

  private void requestPublishComment() {
    request(new PublishCommentRequest(mEtPostContent.getText().toString(), mFactoryId, mPost.id),
        new OnResponse<PublishCommentResponse>() {
          @Override
          public void onResponse(PublishCommentResponse response) {
            if (RESTRequester.responseOk(response)) {
              mPostCommentsAdapter.add(response.object);
              mPost.comments++;
              U.getBus().post(mPost);
            }

            hideProgressBar();
            mEtPostContent.setText("");
            mEtPostContent.setEnabled(true);
            mLvComments.setSelection(Integer.MAX_VALUE);
          }
        }, PublishCommentResponse.class);
  }
}