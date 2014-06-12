package com.utree.eightysix.app.circle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
@Layout (R.layout.activity_circle_create)
@TopTitle (R.string.create_circle)
public class CircleCreateActivity extends BaseActivity {

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

  @OnClick(R.id.ctv_invite)
  public void onCtvInviteClicked() {
    mCtvInvite.setChecked(!mCtvInvite.isChecked());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}