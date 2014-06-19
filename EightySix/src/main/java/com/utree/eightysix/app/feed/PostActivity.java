package com.utree.eightysix.app.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
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

  public static void start(Post post) {
    Intent intent = new Intent(U.getContext(), PostActivity.class);
    intent.putExtra("post", post);
    U.getContext().startActivity(intent);
  }

  public static void start(int postId) {
    Intent intent = new Intent(U.getContext(), PostActivity.class);
    intent.putExtra("id", postId);
    U.getContext().startActivity(intent);
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
        return true;
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
}