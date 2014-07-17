package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.contact.ContactsSyncService;
import com.utree.eightysix.widget.RoundedButton;
import java.util.Random;

/**
 */
@Layout (R.layout.activity_import_cantact)
@TopTitle (R.string.find_friend)
public class ImportContactActivity extends BaseActivity {

  public final static int MSG_ANIMATE = 0x1;

  @InjectView (R.id.fl_import)
  public FrameLayout mFlImport;

  @InjectView (R.id.ll_scroll)
  public LinearLayout mLlScroll;

  @InjectView (R.id.tv_loading)
  public TextView mTvLoading;

  @InjectView (R.id.rb_done)
  public RoundedButton mRbDone;

  @InjectView (R.id.tv_result)
  public TextView mTvResult;

  @InjectView (R.id.rb_import)
  public RoundedButton mRbImport;

  @InjectView (R.id.v_mask)
  public View mVMask;

  private Random mRandom = new Random();

  @OnClick (R.id.rb_done)
  public void onRbDoneClicked() {
    finish();
    BaseCirclesActivity.startSelect(this);
  }

  @OnClick (R.id.rb_import)
  public void onRbImportClicked() {

    mRbImport.setEnabled(false);

    mVMask.setVisibility(View.VISIBLE);
    mFlImport.setVisibility(View.VISIBLE);

    AnimatorSet set = new AnimatorSet();

    set.playTogether(
        ObjectAnimator.ofFloat(mVMask, "alpha", 0f, 1f),
        ObjectAnimator.ofFloat(mFlImport, "alpha", 0f, 1f)
    );

    set.setDuration(500);
    set.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        getHandler().sendEmptyMessageDelayed(MSG_ANIMATE, 500);

        if (U.useFixture()) {

          getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
              getHandler().removeMessages(MSG_ANIMATE);

              mTvResult.setText(String.format("为你找到%d个朋友", mRandom.nextInt(100)));

              ObjectAnimator animator = ObjectAnimator.ofFloat(mLlScroll, "translationY", 0, -U.dp2px(180));
              animator.setDuration(500);
              animator.start();
            }
          }, 5000);
        } else {
          //startService(new Intent(ImportContactActivity.this, ContactsSyncService.class));
          ContactsSyncService.start(ImportContactActivity.this, true);
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

  }

  @OnClick (R.id.v_mask)
  public void onVMaskClicked() {

  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mTvLoading.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/refresh_icon.ttf"));

    setActionLeftDrawable(null);
  }

  @Override
  protected void onHandleMessage(Message message) {
    switch (message.what) {
      case MSG_ANIMATE:
        mTvLoading.setText(String.valueOf((char) (0xe801 + mRandom.nextInt(14))));
        mTvLoading.invalidate();
        getHandler().sendEmptyMessageDelayed(MSG_ANIMATE, 500);
        break;
    }
  }

  @Override
  public void onActionLeftClicked() {
  }

  @Override
  public void onBackPressed() {
  }

  /**
   * When LogoutEvent fired, finish myself
   *
   * @param event the logout event
   */
  @Subscribe
  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onContactSync(ContactsSyncEvent event) {
    if (event.isSucceed()) {
      mTvResult.setText(String.format("为你找到%d个朋友", event.getFriendCount()));

      ObjectAnimator animator = ObjectAnimator.ofFloat(mLlScroll, "translationY", 0, -U.dp2px(180));
      animator.setDuration(500);
      animator.start();
    } else {
      showToast(getString(R.string.sync_contact_failed));
      mRbImport.setEnabled(true);
      mVMask.setVisibility(View.INVISIBLE);
      mFlImport.setVisibility(View.INVISIBLE);
    }
    getHandler().removeMessages(MSG_ANIMATE);
  }
}