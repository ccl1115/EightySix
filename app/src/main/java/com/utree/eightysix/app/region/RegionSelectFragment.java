package com.utree.eightysix.app.region;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.feed.SelectAreaFragment;
import com.utree.eightysix.app.region.event.RegionResponseEvent;

/**
 */
public class RegionSelectFragment extends BaseFragment {

  @InjectView (R.id.tv_distance)
  public TextView mTvDistance;

  @InjectView (R.id.rb_region)
  public RadioButton mRbRegion;

  @InjectView (R.id.rb_area)
  public RadioButton mRbArea;

  @InjectView (R.id.sb_distance)
  public SeekBar mSbDistance;

  @InjectView (R.id.tv_area_name)
  public TextView mTvAreaName;

  @InjectView (R.id.iv_select_area)
  public ImageView mIvSelectArea;

  RegionResponseEvent mRegionResponseEvent;

  private SelectAreaFragment mSelectAreaFragment;

  private Callback mCallback;

  private int mLastRegion;

  private int mAreaId;
  private int mAreaType;

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  @OnClick (R.id.fl_distance_selector)
  public void onLlDistanceSelector() {
    hideSelf();
  }

  @OnClick (R.id.rb_select)
  public void onRbSelect() {
    hideSelf();
    if (mRbRegion.isChecked()) {
      int progress = mSbDistance.getProgress();

      if (mCallback != null) {
        mCallback.onRegionChanged(4, progress + 1000, -1, -1);
      }
      mLastRegion = 4;
    } else if (mRbArea.isChecked()) {
      if (mCallback != null) {
        mCallback.onRegionChanged(5, -1, mAreaType, mAreaId);
      }
      mLastRegion = 5;
    }
  }

  @OnClick (R.id.iv_select_area)
  public void onIvSelectArea() {
    if (mSelectAreaFragment == null) {
      mSelectAreaFragment = new SelectAreaFragment();
      mSelectAreaFragment.setCallback(new SelectAreaFragment.Callback() {
        @Override
        public void onAreaSelected(int areaType, int areaId, String areaName) {
          mTvAreaName.setText(areaName);
          mTvDistance.setText(areaName);
          mAreaId = areaId;
          mAreaType = areaType;
        }
      });
      getFragmentManager().beginTransaction()
          .add(R.id.fl, mSelectAreaFragment)
          .commit();
    } else if (mSelectAreaFragment.isDetached()) {
      getFragmentManager().beginTransaction()
          .attach(mSelectAreaFragment)
          .commit();
    }
  }

  @OnCheckedChanged (R.id.rb_area)
  public void onRbArea(boolean checked) {
    if (checked) {
      mTvDistance.setText(mTvAreaName.getText());
      mSbDistance.setEnabled(false);
      mIvSelectArea.setEnabled(true);
    }
  }

  @OnCheckedChanged (R.id.rb_region)
  public void onRbRegion(boolean checked) {
    if (checked) {
      mTvDistance.setText(String.format("%.2fkm", mSbDistance.getProgress() / 1000f + 1));
      mSbDistance.setEnabled(true);
      mIvSelectArea.setEnabled(false);
    }
  }

  @Subscribe
  public void onRegionResponseEvent(RegionResponseEvent event) {
    if (event.getRegion() == 3) {
      mRbRegion.setChecked(true);
      mSbDistance.setProgress(10000);
      mTvDistance.setText(String.format("%.2fkm", mSbDistance.getProgress() / 1000f + 1));
    } else if (event.getRegion() == 4) {
      mRbRegion.setChecked(true);
      mSbDistance.setProgress(event.getDistance() - 1000);
      mTvDistance.setText(String.format("%.2fkm", mSbDistance.getProgress() / 1000f + 1));
    } else if (event.getRegion() == 5) {
      mRbArea.setChecked(true);
      mTvDistance.setText(event.getCityName());
    }
    mTvAreaName.setText(event.getCityName());
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.widget_distance_selector, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    if (mRegionResponseEvent != null) {
      onRegionResponseEvent(mRegionResponseEvent);
    }

    mSbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float value = progress / 1000f + 1;
        mTvDistance.setText(String.format("%.2fkm", value));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
  }

  public interface Callback {

    void onRegionChanged(int regionType, int distance, int areaType, int areaId);
  }
}