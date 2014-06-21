package com.utree.eightysix.demo;

import android.os.Bundle;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.location.Location;

/**
 */
public class LocationDemoActivity extends BaseActivity implements Location.OnResult {

  public static final int REQUEST_LOCATION_DELAY_MILLIS = 1000;
  private ViewHolder mViewHolder;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_location_demo);

    mViewHolder = U.viewBinding(findViewById(android.R.id.content), ViewHolder.class);

    setTopTitle(getString(R.string.title_location_demo_activity));
  }

  @Override
  protected void onResume() {
    super.onResume();
    U.getLocation().onResume(this);

    getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        showProgressBar();
        U.getLocation().requestLocation();
      }
    }, REQUEST_LOCATION_DELAY_MILLIS);

  }

  @Override
  protected void onPause() {
    super.onPause();
    U.getLocation().onPause(this);
  }

  @Override
  public void onResult(Location.Result result) {
    if (result != null) {
      mViewHolder.mAddress.setText(result.address);
      mViewHolder.mCity.setText(result.city);
      mViewHolder.mLatitude.setText(String.valueOf(result.latitude));
      mViewHolder.mLongitude.setText(String.valueOf(result.longitude));
      mViewHolder.mPoi.setText(result.poi);
    } else {
      setTopTitle(getString(R.string.title_location_demo_activity) + " - 获取位置失败");
    }
    hideProgressBar();
  }

  @Keep
  public static class ViewHolder {

    @InjectView (R.id.address)
    public TextView mAddress;

    @InjectView (R.id.city)
    public TextView mCity;

    @InjectView (R.id.longitude)
    public TextView mLongitude;

    @InjectView (R.id.latitude)
    public TextView mLatitude;

    @InjectView (R.id.poi)
    public TextView mPoi;
  }

}