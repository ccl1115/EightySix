package com.utree.eightysix.app.publish;

import android.content.Context;
import android.content.Intent;
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
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.TextActionButton;
import com.utree.eightysix.widget.panel.Item;

/**
 * @author simon
 */
@TopTitle (R.string.feedback)
public class FeedbackActivity extends PublishActivity {

  @InjectView (R.id.fl_portrait)
  public FrameLayout mFlPortrait;

  public static void start(Context context) {
    Intent intent = new Intent(context, FeedbackActivity.class);
    intent.putExtra("factoryId", U.getConfigInt("feedback.circle.id"));
    context.startActivity(intent);
  }

  @Override
  protected String getHintText() {
    return getString(R.string.give_us_feedback);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mTvBottom.setVisibility(View.INVISIBLE);
    mFlPortrait.setVisibility(View.INVISIBLE);

    showSoftKeyboard(mPostEditText);

    onIvShuffleClicked();

    ((TextActionButton) getTopBar().getActionView(0)).setText(R.string.publish_submit);
  }

  @Override
  protected void showDescriptionDialogWhenFirstRun() {
  }

  @Subscribe
  @Override
  public void onImageUploaded(ImageUtils.ImageUploadedEvent event) {
    super.onImageUploaded(event);
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