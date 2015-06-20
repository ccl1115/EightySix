package com.utree.eightysix.app.devmode;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.account.ProfileFillActivity;
import com.utree.eightysix.utils.Env;

/**
 */
@Layout(R.layout.activity_dev_mode)
public class DevModeActivity extends BaseActivity {

  @InjectView(R.id.et_latitude)
  public EditText mEtLatitude;

  @InjectView(R.id.et_longitude)
  public EditText mEtLongitude;

  @InjectView(R.id.sp_api)
  public Spinner mSpApi;

  @InjectView(R.id.sp_api_seccond)
  public Spinner mSpApiSecond;

  private boolean mNeedLogout;

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

  @OnClick(R.id.tv_profile_file)
  public void onTvProfileFillClicked() {
    startActivity(new Intent(this, ProfileFillActivity.class));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mEtLatitude.setText(Env.getLastLatitude());
    mEtLongitude.setText(Env.getLastLongitude());

//    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{
//        "http://c.lanmeiquan.com",
//        "http://182.254.172.170",
//        "http://192.168.0.118:8088"
//    });
//    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    mSpApi.setAdapter(adapter);
//
//    mSpApi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//      @Override
//      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        U.getRESTRequester().setHost(adapter.getItem(position));
//        mNeedLogout = true;
//      }
//
//      @Override
//      public void onNothingSelected(AdapterView<?> parent) {
//
//      }
//    });
//
//    final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {
//        "http://testing.gz.1251114078.cee.myqcloud.com",
//        "http://production.gz.1251114078.cee.myqcloud.com"
//    });
//
//    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    mSpApiSecond.setAdapter(adapter2);
//
//    mSpApiSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//      @Override
//      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        U.getRESTRequester().setSecondHost(adapter2.getItem(position));
//        mNeedLogout = true;
//      }
//
//      @Override
//      public void onNothingSelected(AdapterView<?> parent) {
//
//      }
//    });
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (mNeedLogout) {
      Account.inst().logout();
    }
  }
}