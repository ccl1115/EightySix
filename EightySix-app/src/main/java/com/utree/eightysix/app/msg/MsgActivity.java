package com.utree.eightysix.app.msg;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import butterknife.InjectView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.IRefreshable;
import com.utree.eightysix.widget.RefresherView;
import java.util.List;
import java.util.Random;

/**
 * @author simon
 */
@Layout (R.layout.activity_msg)
@TopTitle (R.string.messages)
public class MsgActivity extends BaseActivity {

  @InjectView (R.id.rv_msg)
  public RefresherView mRvMsg;

  @InjectView (R.id.tv_no_new_msg)
  public TextView mTvEmpty;

  @InjectView (R.id.alv_msg)
  public AdvancedListView mAivMsg;

  @InjectView (R.id.tv_head)
  public TextView mTvHead;

  private Random mRandom = new Random();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (U.useFixture()) {
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          List<Post> valid = U.getFixture(Post.class, 23, "valid");

          mTvHead.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/refresh_icon.ttf"));

          for (Post p : valid) {
            if (p.read == 0) {
              mTvEmpty.setVisibility(View.INVISIBLE);
            }
          }
          mAivMsg.setAdapter(new MsgAdapter(valid));
        }
      }, 1000);
    }

    mRvMsg.setOnRefreshListener(new IRefreshable.OnRefreshListener() {
      @Override
      public void onStateChanged(IRefreshable.State state) {
        switch (state) {
          case idle:
            break;
          case pulling_no_refresh:
            mTvHead.setText(String.format("%c", (char) (0xe801 + mRandom.nextInt(14))));
            break;
          case pulling_refresh:
            mTvHead.setText(String.format("%c", (char) (0xe801 + mRandom.nextInt(14))));
            break;

        }
      }

      @Override
      public void onPreRefresh() {
        mTvHead.setText("\ue800");
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTvHead, "rotation", 0f, 360f * 10f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        mTvHead.setTag(animator);
      }

      @Override
      public void onRefreshData() {
        try {
          synchronized (this) {
            wait(2000);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onRefreshUI() {
        ObjectAnimator animator = (ObjectAnimator) mTvHead.getTag();
        if (animator != null) animator.cancel();
        ViewHelper.setRotation(mTvHead, 0);
      }
    });
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }

  @Subscribe
  public void onItemClicked(Post post) {
    PostActivity.start(this, post);
  }
}