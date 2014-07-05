package com.utree.eightysix.app.feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.request.PostCommentsRequest;
import com.utree.eightysix.request.PublishCommentRequest;
import com.utree.eightysix.response.PostCommentsResponse;
import com.utree.eightysix.response.PublishCommentResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.ShareUtils;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RoundedButton;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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

    new AlertDialog.Builder(this).setTitle(getString(R.string.comment_action))
        .setItems(new String[]{comment.praised == 1 ? getString(R.string.unlike) : getString(R.string.like), getString(R.string.report), getString(R.string.share)},
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case 0:
                    comment.praised = comment.praised == 1 ? 0 : 1;
                    if (comment.praised == 1) {
                      comment.praise++;
                    } else {
                      comment.praise--;
                    }
                    U.getBus().post(new AdapterDataSetChangedEvent());
                    break;
                  case 1:
                    showToast("TODO report");
                    break;
                  case 2:
                    if (mPost != null) ShareUtils.sharePostToQQ(PostActivity.this, mPost);
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

    mPostCommentsAdapter = new PostCommentsAdapter(mPost, new ArrayList<Comment>());
    mLvComments.setAdapter(mPostCommentsAdapter);

    mLvComments.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(PostActivity.this).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPostCommentsAdapter.getCount() <= 1;
      }

      @Override
      public boolean onLoadMoreStart() {
        return true;
      }
    });

    requestComment(1);
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

  private void requestComment(final int page) {
    request(new PostCommentsRequest(mFactoryId, mPost.id, page), new OnResponse<PostCommentsResponse>() {
      @Override
      public void onResponse(PostCommentsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          mPostCommentsAdapter.setPost(response.object.post);
          mPostCommentsAdapter.add(response.object.comments.lists);
          mLvComments.stopLoadMore();
          hideProgressBar();
        }
      }
    }, PostCommentsResponse.class);
  }

  private void requestPublishComment() {
    request(new PublishCommentRequest(mEtPostContent.getText().toString(), mFactoryId, mPost.id),
        new OnResponse<PublishCommentResponse>() {
          @Override
          public void onResponse(PublishCommentResponse response) {
            if (response != null && response.code == 0 && response.object != null) {
              Comment comment = new Comment();
              comment.avatar = "\ue801";
              comment.avatarColor = "ff837827";
              comment.id = response.object.id;
              comment.isHost = 0;
              comment.timestamp = new Date().getTime();
              comment.content = mEtPostContent.getText().toString();
              mPostCommentsAdapter.add(comment);
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
}