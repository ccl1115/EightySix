package com.utree.eightysix.app.devmode;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.utils.Env;

/**
 */
@Layout(R.layout.activity_dev_mode)
public class DevModeActivity extends BaseActivity {

  @InjectView(R.id.et_latitude)
  public EditText mEtLatitude;

  @InjectView(R.id.et_longitude)
  public EditText mEtLongitude;

  @OnClick(R.id.rb_latitude)
  public void onRbLatitudeClicked() {
    try {
      Env.setLastLatitude(Double.parseDouble(mEtLatitude.getText().toString()));
    } catch (NumberFormatException e) {
      showToast("错误的经纬度格式");
    }
  }

  @OnClick(R.id.rb_longitude)
  public void onRbLongitudeClicked() {
    try {
      Env.setLastLongitude(Double.parseDouble(mEtLongitude.getText().toString()));
    } catch (NumberFormatException e) {
      showToast("错误的经纬度格式");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mEtLatitude.setText(Env.getLastLatitude());
    mEtLongitude.setText(Env.getLastLongitude());
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {

  }
}