package com.utree.eightysix.app.region;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.circle.BaseCirclesActivity;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.request.FactoryRegionRequest;
import com.utree.eightysix.response.FactoryRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
public class FactoryRegionFragment extends BaseFragment {

  @InjectView (R.id.alv_factories)
  public AdvancedListView mAlvFactories;

  private FactoryRegionAdapter mAdapter;

  private int mRegionType;

  @OnClick (R.id.fl_parent)
  public void onFlParentClicked() {
    detach();
  }

  @OnClick (R.id.tv_more)
  public void onTvMoreClicked() {
    BaseCirclesActivity.startRegion(getActivity(), mRegionType);
    detach();
  }


  @OnItemClick (R.id.alv_factories)
  public void onAlvItemClicked(int position, View view) {
    Circle item = mAdapter.getItem(position);
    if (item.currFactory == 1) {
      HomeActivity.start(view.getContext(), 0);
    } else {
      FeedActivity.start(view.getContext(), item);
    }

    detach();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_factory_region, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);
  }

  public void setRegionType(int regionType) {
    mRegionType = regionType;
    if (mAlvFactories != null) mAlvFactories.setAdapter(null);
    requestRegionFactories(regionType);
  }

  public void requestRegionFactories(int regionType) {
    U.getRESTRequester().request(new FactoryRegionRequest(regionType, 1), new OnResponse2<FactoryRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FactoryRegionResponse response) {
        mAdapter = new FactoryRegionAdapter(response.object.lists);
        mAlvFactories.setAdapter(mAdapter);
      }
    }, FactoryRegionResponse.class);
  }

  protected void detach() {
    getFragmentManager().beginTransaction()
        .detach(this).commit();
  }

  @Override
  public boolean onBackPressed() {
    if (!isDetached()) {
      detach();
      return true;
    } else {
      return false;
    }
  }
}
