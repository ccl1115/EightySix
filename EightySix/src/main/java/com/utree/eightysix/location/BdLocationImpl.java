package com.utree.eightysix.location;

import android.os.Handler;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.utree.eightysix.U;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class BdLocationImpl implements Location, BDLocationListener {

    private final LocationClient mLocationClient;

    private final Handler mHandler;

    private final List<OnResult> mOnResults;

    public BdLocationImpl() {
        mLocationClient = new LocationClient(U.getContext());
        mHandler = new Handler();
        mOnResults = new ArrayList<OnResult>();
        mLocationClient.registerLocationListener(this);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation != null) {
            final Result result = new Result();
            result.address = bdLocation.getAddrStr();
            result.city = bdLocation.getCity();
            result.latitude = bdLocation.getLatitude();
            result.longitude = bdLocation.getLongitude();
            result.poi = bdLocation.getPoi();

            for (OnResult onResult : mOnResults) {
                onResult.onResult(result);
            }
        }
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
        mLocationClient.requestLocation();
    }
}
