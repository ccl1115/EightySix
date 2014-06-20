package com.utree.eightysix.app.feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.response.data.Comment;
import com.utree.eightysix.response.data.Post;
import com.utree.eightysix.rest.FixtureUtil;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RoundedButton;
import java.util.ArrayList;

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
  private PostCommentsAdapter mPostCommentsAdapter;

  private AlertDialog mCommentContextDialog;

  public static void start(Context context, Post post) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("post", post);
    context.startActivity(intent);
  }

  public static void start(Context context, int postId) {
    Intent intent = new Intent(context, PostActivity.class);
    intent.putExtra("id", postId);
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

    final Comment comment = (Comment) mPostCommentsAdapter.getItem(position);
    new AlertDialog.Builder(this).setTitle(getString(R.string.comment_action))
        .setItems(new String[]{comment.praised == 1 ? getString(R.string.unlike) : getString(R.string.like), getString(R.string.report)},
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
                }
              }
            }).create().show();
  }

  @OnClick (R.id.rb_post)
  public void onRbPostClicked() {
    showToast("TODO request post");
    showProgressBar();
    mEtPostContent.setEnabled(false);
    mRbPost.setEnabled(false);
    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        hideProgressBar();
        mEtPostContent.setText("");
        mEtPostContent.setEnabled(true);
        mRbPost.setEnabled(true);
        mLvComments.setSelection(mLvComments.getCount());
      }
    }, 3000);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mPost = (Post) getIntent().getSerializableExtra("post");

    if (mPost == null) {
      if (BuildConfig.DEBUG) {
        mPost = FixtureUtil.from(Post.class).gimme("valid");
      }
    }

    mPostCommentsAdapter = new PostCommentsAdapter(mPost, new ArrayList<Comment>());
    mLvComments.setAdapter(mPostCommentsAdapter);

    mLvComments.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView() {
        return View.inflate(PostActivity.this, R.layout.footer_load_more, null);
      }

      @Override
      public boolean hasMore() {
        return mPostCommentsAdapter.getCount() <= 1;
      }

      @Override
      public boolean onLoadMoreStart() {
        getHandler().postDelayed(new Runnable() {
          @Override
          public void run() {
            mPostCommentsAdapter.add(FixtureUtil.from(Comment.class).<Comment>gimme(20, "valid"));
            mLvComments.stopLoadMore();
          }
        }, 2000);
        return true;
      }
    });

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
}