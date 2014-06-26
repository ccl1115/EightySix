package com.utree.eightysix.app.msg;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
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

  private static final int MSG_ANIMATE = 0x1;
  @InjectView (R.id.rv_msg)
  public RefresherView mRvMsg;

  @InjectView (R.id.tv_no_new_msg)
  public TextView mTvNoNewMsg;

  @InjectView(R.id.tv_no_msg)
  public TextView mTvNoMsg;

  @InjectView (R.id.alv_msg)
  public AdvancedListView mAivMsg;

  @InjectView (R.id.tv_head)
  public TextView mTvHead;

  private Random mRandom = new Random();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mTvHead.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/refresh_icon.ttf"));

    if (U.useFixture()) {
      showProgressBar();
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          List<Post> valid = U.getFixture(Post.class, 23, "valid");
          for (Post p : valid)
          {
            if (p.read == 0) {
              mTvNoNewMsg.setVisibility(View.INVISIBLE);
            }
          }
          mAivMsg.setAdapter(new MsgAdapter(valid));
          hideProgressBar();
        }
      }, 1000);
    }

    mRvMsg.setOnRefreshListener(new IRefreshable.OnRefreshListener() {
      @Override
      public void onStateChanged(IRefreshable.State state) {
        switch (state) {
          case pulling_refresh:
            mTvHead.setText(String.format("%c", (char) (0xe801 + mRandom.nextInt(14))));
            break;

        }
      }

      @Override
      public void onPreRefresh() {
        getHandler().sendEmptyMessageDelayed(MSG_ANIMATE, 500);
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
        getHandler().removeMessages(MSG_ANIMATE);
      }
    });
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onHandleMessage(Message message) {
    switch (message.what) {
      case MSG_ANIMATE:
        mTvHead.setText(String.format("%c", (char) (0xe801 + mRandom.nextInt(14))));
        mRvMsg.invalidate();
        getHandler().sendEmptyMessageDelayed(MSG_ANIMATE, 500);
        break;
    }
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