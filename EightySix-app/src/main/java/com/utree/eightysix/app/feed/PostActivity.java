package com.utree.eightysix.app.feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
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
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.RoundedButton;

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

  public static void start(Context context, int factoryId, Post post) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("factoryId", factoryId);
    context.startActivity(intent);
  }

  public static void start(Context context, int factoryId, int postId) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("id", postId);
    intent.putExtra("factoryId", factoryId);
    context.startActivity(intent);
  }

  @OnTextChanged (R.id.et_post_content)
  public void onEtPostContentTextChanged(CharSequence text) {
    if (TextUtils.isEmpty(text)) {
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
      items = new String[]{like, getString(R.string.report), getString(R.string.delete)};
    } else {
      items = new String[]{like, getString(R.string.report)};
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
                    U.getBus().post(new AdapterDataSetChangedEvent());
                    break;
                  case 1:
                    showToast("TODO report");
                    break;
                  case 2:
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
    mFactoryId = getIntent().getIntExtra("factoryId", -1);

    if (mFactoryId == -1) {
      finish();
    }

    if (mPost == null && U.useFixture()) {
      mPost = U.getFixture(Post.class, "valid");
    }

    cacheOutComments(1);
  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getBus().register(mLvComments);
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
            U.getBus().post(new AdapterDataSetChangedEvent());
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
            U.getBus().post(new AdapterDataSetChangedEvent());
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
            U.getBus().post(new AdapterDataSetChangedEvent());
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
            U.getBus().post(new AdapterDataSetChangedEvent());
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

  private void requestComment(final int page) {
    request(new PostCommentsRequest(mFactoryId, mPost.id, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mPostCommentsAdapter = new PostCommentsAdapter(response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);

          mPost = response.object.post;
          U.getBus().post(mPost);
        }
        hideProgressBar();
      }
    }, PostCommentsResponse.class);
  }

  private void cacheOutComments(final int page) {
    cacheOut(new PostCommentsRequest(mFactoryId, mPost.id, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mPostCommentsAdapter = new PostCommentsAdapter(response.object.post, response.object.comments.lists);
          mLvComments.setAdapter(mPostCommentsAdapter);
          mPost = response.object.post;
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
            if (response != null && response.code == 0 && response.object != null) {
              mPostCommentsAdapter.add(response.object);
              mPost.comments++;
              U.getBus().post(mPost);
            }

            hideProgressBar();
            mEtPostContent.setText("");
            mEtPostContent.setEnabled(true);
            mRbPost.setEnabled(true);
            mLvComments.setSelection(mLvComments.getCount());
            mLvComments.stopLoadMore();
          }
        }, PublishCommentResponse.class);
  }

  private void requestDeletePost() {
    request(new PostDeleteRequest(mPost.id), new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.getBus().post(new PostDeleteEvent(mPost));
          finish();
        }
      }
    }, Response.class);
  }
}