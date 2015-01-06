package com.utree.eightysix.app.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.region.event.RegionResponseEvent;
import com.utree.eightysix.data.Circle;

/**
 */
public class RegionFragment extends BaseFragment {

  @InjectView (R.id.rb_current)
  public TextView mRbCurrent;

  @InjectView (R.id.rb_range_1)
  public TextView mRbRange1;

  @InjectView (R.id.rb_range_2)
  public TextView mRbRange2;

  @InjectView (R.id.rb_range_3)
  public TextView mRbRange3;

  @InjectView(R.id.tv_fellow)
  public TextView mTvFellow;

  private Circle mCurrent;
  private Callback mCallback;

  private int mRegionType;

  @OnClick (R.id.rb_more)
  public void onRbMoreClicked() {
    BaseCirclesActivity.startMyCircles(getBaseActivity());
  }


  @OnClick (R.id.rb_current)
  public void onRbCurrentClicked(View v) {
    if (mCurrent == null) {
      BaseCirclesActivity.startSelect(getBaseActivity(), false);
    } else {
      mRegionType = 0;
      setRangeSelected(0, v);
    }
  }

  @OnClick(R.id.rb_fellow_setting)
  public void onRbFellowSettingClicked() {
    if (mCallback != null) {
      mCallback.onFellowSettingClicked();
    }
  }

  @OnClick(R.id.tv_fellow)
  public void onTvFellowClicked(View v) {
    clearSelected();
    if (v.isSelected()) {
      if (mCallback != null) {
        mCallback.onFellowClicked(false);
      }
    } else {
      v.setSelected(true);
      if (mCallback != null) {
        mCallback.onFellowClicked(true);
      }
    }
  }

  @OnClick (R.id.rb_range_1)
  public void onRbRegion1Clicked(View v) {
    mRegionType = 1;
    setRangeSelected(1, v);
  }

  @OnClick (R.id.rb_range_2)
  public void onRbRegion2Clicked(View v) {
    mRegionType = 2;
    setRangeSelected(2, v);
  }

  @OnClick (R.id.rb_range_3)
  public void onRbRegion3Clicked(View v) {
    mRegionType = 3;
    setRangeSelected(3, v);
  }

  public int getRegionType() {
    return mRegionType;
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public void setCurrentCircle(Circle circle) {
    mCurrent = circle;

    if (mCurrent == null) {
      mRbCurrent.setText(U.gs(R.string.set_current_factory));
    } else {
      mRbCurrent.setText(mCurrent.shortName + "(在职)");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_range, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

  }

  @Subscribe
  public void onRegionUpdateEvent(RegionResponseEvent event) {
    setCurrentCircle(event.getCircle());

    clearSelected();
    switch (event.getRegion()) {
      case 0:
        mRbCurrent.setSelected(true);
        break;
      case 1:
        mRbRange1.setSelected(true);
        break;
      case 2:
        mRbRange2.setSelected(true);
        break;
      case 3:
        mRbRange3.setSelected(true);
        break;
    }
  }

  protected void setRangeSelected(int i, View v) {
    if (v.isSelected()) {
      if (mCallback != null) {
        mCallback.onRegionClicked(i, false);
      }
    } else {
      clearSelected();
      v.setSelected(true);
      if (mCallback != null) {
        mCallback.onRegionClicked(i, true);
      }
    }
  }

  private void clearSelected() {
    mRbCurrent.setSelected(false);
    mRbRange1.setSelected(false);
    mRbRange2.setSelected(false);
    mRbRange3.setSelected(false);
    mTvFellow.setSelected(false);
  }

  public interface Callback {
    void onRegionClicked(int regionType, boolean selected);

    void onFellowSettingClicked();

    void onFellowClicked(boolean selected);
  }

  @Subscribe
  public void onRegionResponseEvent(RegionResponseEvent event) {
    mRegionType = event.getRegion();
  }
}
