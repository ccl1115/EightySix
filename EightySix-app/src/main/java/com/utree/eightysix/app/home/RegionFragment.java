package com.utree.eightysix.app.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.region.event.RegionUpdateEvent;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.widget.RoundedButton;

/**
 */
public class RegionFragment extends BaseFragment {

  @InjectView (R.id.rb_current)
  public RoundedButton mRbCurrent;

  @InjectView (R.id.rb_range_1)
  public RoundedButton mRbRange1;

  @InjectView (R.id.rb_range_2)
  public RoundedButton mRbRange2;

  @InjectView (R.id.rb_range_3)
  public RoundedButton mRbRange3;

  private Circle mCurrent;
  private Callback mCallback;

  @OnClick (R.id.rb_current)
  public void onRbCurrentClicked() {
    if (mCurrent == null) {
      BaseCirclesActivity.startSelect(getBaseActivity());
    } else if (mRbCurrent.isSelected()) {
      if (mCallback != null) {
        mCallback.onItemClicked(0, false);
      }
    } else {
      clearSelected();
      mRbCurrent.setSelected(true);
      if (mCallback != null) {
        mCallback.onItemClicked(0, true);
      }
    }
  }

  @OnClick (R.id.rb_range_1)
  public void onRbRegion1Clicked(View v) {
    setRangeSelected(1, v);
  }

  @OnClick(R.id.rb_range_2)
  public void onRbRegion2Clicked(View v) {
    setRangeSelected(2, v);
  }

  @OnClick(R.id.rb_range_3)
  public void onRbRegion3Clicked(View v) {
    setRangeSelected(3, v);
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public void setCurrentCircle(Circle circle) {
    mCurrent = circle;

    if (mCurrent == null) {
      mRbCurrent.setText(U.gs(R.string.set_current_factory));
    } else {
      mRbCurrent.setText(mCurrent.shortName);
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

  protected void setRangeSelected(int i, View v) {
    if (v.isSelected()) {
      if (mCallback != null) {
        mCallback.onItemClicked(i, false);
      }
    } else {
      clearSelected();
      v.setSelected(true);
      if (mCallback != null) {
        mCallback.onItemClicked(i, true);
      }
    }
  }

  private void clearSelected() {
    mRbCurrent.setSelected(false);
    mRbRange1.setSelected(false);
    mRbRange2.setSelected(false);
    mRbRange3.setSelected(false);
  }

  public interface Callback {
    void onItemClicked(int regionType, boolean selected);
  }

  @Subscribe
  public void onRegionUpdateEvent(RegionUpdateEvent event) {
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
}
