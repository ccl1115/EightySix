package com.utree.eightysix.app.publish;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.widget.panel.Item;

/**
 * @author simon
 */
@TopTitle (R.string.feedback)
public class FeedbackActivity extends PublishActivity {

  @InjectView (R.id.fl_portrait)
  public FrameLayout mFlPortrait;

  private int mFeedbackCircleId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mTvPostTip.setText(getString(R.string.give_us_feedback));

    mTvBottom.setVisibility(View.INVISIBLE);
    mFlPortrait.setVisibility(View.INVISIBLE);

    mFeedbackCircleId = U.getConfigInt("feedback.circle.id");

    showSoftKeyboard(mPostEditText);
  }

  @Subscribe
  @Override
  public void onGridPanelItemClicked(Item item) {
    super.onGridPanelItemClicked(item);
  }

  @OnClick(R.id.iv_shuffle)
  public void onIvShuffleClicked() {
    hideSoftKeyboard(mPostEditText);
    mPublishLayout.switchToPanel(PublishLayout.PANEL_COLOR);
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}