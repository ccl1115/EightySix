package com.utree.eightysix.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.utils.ViewMapping;

/**
 */
public class LocationDemoActivity extends BaseActivity implements Location.OnResult {

    private ViewHolder mViewHolder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_demo);

        mViewHolder = U.viewMapping(findViewById(android.R.id.content), ViewHolder.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        U.getLocation().onResume(this);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                U.getLocation().requestLocation();
            }
        }, 2000);
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
        }
    }

    private static class ViewHolder {

        @ViewMapping.ViewId(R.id.address)
        public TextView mAddress;

        @ViewMapping.ViewId(R.id.city)
        public TextView mCity;

        @ViewMapping.ViewId(R.id.longitude)
        public TextView mLongitude;

        @ViewMapping.ViewId(R.id.latitude)
        public TextView mLatitude;

        @ViewMapping.ViewId(R.id.poi)
        public TextView mPoi;
    }

}