package com.utree.eightysix.location;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Location service back-end by Baidu LBS
 * <p/>
 * Note, this implementation will store the last succeeded result to {@link com.utree.eightysix.utils.Env}
 */
public class BdLocationImpl implements Location, BDLocationListener {

  private static final int MSG_REQ_TIMEOUT = 0x1;
  private static final int MSG_REQ_LOCATION = 0x2;

  private static final int REQ_TIMEOUT = 10000; // ms
  private static final int REQ_LOCATION_DELAY = 1000;

  private final LocationClient mLocationClient;
  private final List<OnResult> mOnResults;
  private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_REQ_TIMEOUT:
          mLocationClient.stop();
          for (OnResult onResult : mOnResults) {
            onResult.onResult(null);
          }
          break;
        case MSG_REQ_LOCATION:
          if (mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
          } else {
            for (OnResult onResult : mOnResults) {
              onResult.onResult(null);
            }
          }
          break;
        default:
          break;
      }
    }
  };
  private final List<OnResult> mTransientOnResult;

  public BdLocationImpl(Context context) {
    mLocationClient = new LocationClient(context.getApplicationContext());
    LocationClientOption option = new LocationClientOption();
    option.setOpenGps(true);
    option.setProdName("eightysix");
    option.setAddrType("all");// 返回的定位结果包含地址信息
    option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
    option.setScanSpan(2000);// 设置发起定位请求的间隔时间为500ms,一次定位
    mLocationClient.setLocOption(option);
    mLocationClient.registerLocationListener(this);

    mOnResults = new ArrayList<OnResult>();
    mTransientOnResult = new ArrayList<OnResult>();

    mLocationClient.start();
  }

  @Override
  public void onReceiveLocation(BDLocation bdLocation) {
    final Result result;
    if (bdLocation != null) {
      mHandler.removeMessages(MSG_REQ_TIMEOUT);
      if (bdLocation.getLocType() != BDLocation.TypeCriteriaException
          && bdLocation.getLocType() != BDLocation.TypeNetWorkException
          && bdLocation.getLocType() != BDLocation.TypeOffLineLocationFail
          && bdLocation.getLocType() != BDLocation.TypeOffLineLocationNetworkFail
          && bdLocation.getLocType() <= BDLocation.TypeNetWorkLocation) {
        result = new Result();
        result.address = bdLocation.getAddrStr();
        result.city = bdLocation.getCity();
        result.latitude = bdLocation.getLatitude();
        result.longitude = bdLocation.getLongitude();
        result.poi = bdLocation.getPoi();

        Env.setLastLatitude(result.latitude);
        Env.setLastLongitude(result.longitude);
        Env.setLastCity(result.city);
      } else {
        result = null;
      }
    } else {
      result = null;
    }

    for (OnResult onResult : mOnResults) {
      onResult.onResult(result);
    }

    for (OnResult onResult : mTransientOnResult) {
      onResult.onResult(result);
    }

    mTransientOnResult.clear();

    mLocationClient.stop();
  }

  @Override
  public void onReceivePoi(BDLocation bdLocation) {

  }

  @Override
  public void onResume(OnResult onResult) {
    mOnResults.add(onResult);
    if (!mLocationClient.isStarted()) {
      mLocationClient.start();
    }
  }

  @Override
  public void onPause(OnResult onResult) {
    mOnResults.remove(onResult);
    if (mOnResults.size() == 0) {
      mLocationClient.stop();
    }
  }

  @Override
  public void requestLocation() {
    if (!mLocationClient.isStarted()) {
      mLocationClient.start();
    }

    mHandler.sendEmptyMessageDelayed(MSG_REQ_LOCATION, REQ_LOCATION_DELAY);

    mHandler.sendEmptyMessageDelayed(MSG_REQ_TIMEOUT, REQ_TIMEOUT);
  }

  @Override
  public void requestLocation(OnResult onResult) {
    requestLocation();

    mTransientOnResult.add(onResult);
  }
}
