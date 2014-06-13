package com.utree.eightysix.app.circle;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
@Layout (R.layout.activity_circle_create)
@TopTitle (R.string.create_circle)
public class CircleCreateActivity extends BaseActivity implements Location.OnResult {

  @InjectView (R.id.et_circle_name)
  public EditText mEtCircleName;

  @InjectView (R.id.et_circle_abbreviation)
  public EditText mEtCircleAbbreviation;

  @InjectView (R.id.tv_location)
  public TextView mTvLocation;

  @InjectView (R.id.et_captcha)
  public EditText mEtCaptcha;

  @InjectView (R.id.iv_captcha)
  public ImageView mIvCaptcha;

  @InjectView (R.id.rb_reget_captcha)
  public RoundedButton mRbRegetCaptcha;

  @InjectView (R.id.rb_create)
  public RoundedButton mRbCreate;

  @InjectView (R.id.ctv_invite)
  public CheckedTextView mCtvInvite;

  @OnClick (R.id.ctv_invite)
  public void onCtvInviteClicked() {
    mCtvInvite.setChecked(!mCtvInvite.isChecked());
  }

  @OnTextChanged (R.id.et_circle_name)
  public void onEtCircleNameTextChanged(CharSequence t) {
    final int length = U.getConfigInt("circle.length");
    if (t.length() > length) {
      showToast(getString(R.string.circle_name_length_long));
      mEtCircleName.setText(t.subSequence(0, length));
    }
  }

  @OnTextChanged (R.id.et_circle_abbreviation)
  public void onEtCircleAbbreviationTextChanged(CharSequence t) {
    final int length = U.getConfigInt("circle.short.length");
    if (t.length() > length) {
      showToast(getString(R.string.circle_short_name_length_long));
      mEtCircleAbbreviation.setText(t.subSequence(0, length));
    }
  }

  @OnClick (R.id.rb_reget_captcha)
  public void onRbRegetCaptchaClicked() {
    showToast("TODO renew captcha");
  }

  @OnClick (R.id.rb_create)
  public void onRbCreateClicked() {
    showToast("TODO create circle");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    U.getLocation().requestLocation();
  }

  @Override
  protected void onResume() {
    super.onResume();

    U.getLocation().onResume(this);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        U.getLocation().requestLocation();
        mTvLocation.setText(getString(R.string.locating));
        showProgressBar();
      }
    }, 1000);
  }

  @Override
  protected void onPause() {
    super.onPause();

    U.getLocation().onPause(this);
  }

  @Override
  public void onResult(Location.Result result) {
    if (result != null) {
      mTvLocation.setText(result.address);
    } else {
      mTvLocation.setText(getString(R.string.locating_failed));
    }
    hideProgressBar();
  }
}