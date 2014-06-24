package com.utree.eightysix.app.msg;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.AdvancedListView;
import java.util.List;

/**
 * @author simon
 */
@Layout (R.layout.activity_msg)
@TopTitle (R.string.messages)
public class MsgActivity extends BaseActivity {

  @InjectView (R.id.tv_no_new_msg)
  public TextView mTvEmpty;

  @InjectView (R.id.alv_msg)
  public AdvancedListView mAivMsg;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    List<Post> valid = U.getFixture(Post.class, 23, "valid");

    for (Post p : valid) {
      if (p.read == 0) {
        mTvEmpty.setVisibility(View.INVISIBLE);
      }
    }
    mAivMsg.setAdapter(new MsgAdapter(valid));
  }

  @Subscribe public void onItemClicked(Post post) {
    PostActivity.start(this, post);
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }
}