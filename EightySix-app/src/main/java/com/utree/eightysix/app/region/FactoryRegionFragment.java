package com.utree.eightysix.app.region;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.request.FactoryRegionRequest;
import com.utree.eightysix.response.FactoryRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
public class FactoryRegionFragment extends BaseFragment {

  @InjectView (R.id.alv_factories)
  public AdvancedListView mAlvFactories;

  @InjectView (R.id.tv_more)
  public TextView mTvMore;

  private int mRegionType;
  private FactoryRegionAdapter mAdapter;

  @OnClick (R.id.fl_parent)
  public void onFlParentClicked() {
    getFragmentManager().beginTransaction()
        .detach(this).commit();
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
    if (mAlvFactories != null) mAlvFactories.setAdapter(null);
    mRegionType = regionType;
    requestRegionFactories(mRegionType);
  }

  public void requestRegionFactories(int regionType) {
    U.getRESTRequester().request(new FactoryRegionRequest(regionType, 1), new OnResponse2<FactoryRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FactoryRegionResponse response) {
        mAdapter = new FactoryRegionAdapter(response.object.factoryList.lists);
        mAlvFactories.setAdapter(mAdapter);
      }
    }, FactoryRegionResponse.class);
  }
}
