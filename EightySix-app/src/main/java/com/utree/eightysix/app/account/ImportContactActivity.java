package com.utree.eightysix.app.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.circle.SelectCircleActivity;
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

  @InjectView (R.id.v_mask)
  public View mVMask;

  private Random mRandom = new Random();

  private AlertDialog mQuitConfirmDialog;

  @OnClick(R.id.rb_done)
  public void onRbDoneClicked() {
    startActivity(new Intent(this, SelectCircleActivity.class));
  }

  @OnClick (R.id.rb_import)
  public void onRbImportClicked() {

    mVMask.setVisibility(View.VISIBLE);
    mFlImport.setVisibility(View.VISIBLE);

    AnimatorSet set = new AnimatorSet();

    set.playTogether(
        ObjectAnimator.ofFloat(mVMask, "alpha", 0f, 1f),
        ObjectAnimator.ofFloat(mFlImport, "alpha", 0f, 1f)
    );

    set.setDuration(500);
    set.start();

    getHandler().sendEmptyMessageDelayed(MSG_ANIMATE, 500);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getHandler().removeMessages(MSG_ANIMATE);

        mTvResult.setText("为你找到" + mRandom.nextInt(100) + "个朋友");

        ObjectAnimator animator = ObjectAnimator.ofFloat(mLlScroll, "translationY", 0, -U.dp2px(180));
        animator.setDuration(500);
        animator.start();
      }
    }, 5000);

  }

  @OnClick (R.id.v_mask)
  public void onVMaskClicked() {

  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mTvLoading.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/refresh_icon.ttf"));

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    mQuitConfirmDialog = builder.setTitle("建议完成设置以便更好的和朋友互动")
        .setPositiveButton("停止", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        }).setNegativeButton("继续", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).create();
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
  protected void onActionLeftOnClicked() {
    onBackPressed();
  }

  @Override
  public void onBackPressed() {
    if (mQuitConfirmDialog.isShowing()) {
      mQuitConfirmDialog.dismiss();
    } else {
      mQuitConfirmDialog.show();
    }
  }
}