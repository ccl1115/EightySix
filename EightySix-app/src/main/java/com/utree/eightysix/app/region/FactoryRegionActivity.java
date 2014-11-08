package com.utree.eightysix.app.region;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.request.FactoryRegionRequest;
import com.utree.eightysix.response.FactoryRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
@Layout(R.layout.activity_factory_region)
public class FactoryRegionActivity extends BaseActivity {

  @InjectView(R.id.content)
  public AdvancedListView mAlvFactories;

  private FactoryRegionAdapter2 mAdapter;

  @OnItemClick(R.id.content)
  public void onAlvFactoriesItemClicked(int position) {
    Circle item = mAdapter.getItem(position);
    if (item.currFactory == 1) {
      HomeActivity.start(this, 0);
    } else {
      FeedActivity.start(this, item);
    }
  }

  public static void start(Context context, int regionType) {
    Intent intent = new Intent(context, FactoryRegionActivity.class);

    intent.putExtra("regionType", regionType);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final int regionType = getIntent().getIntExtra("regionType", 1);

    requestFactoryRegion(regionType);

    switch (regionType) {
      case 1:
        setTopTitle("1公里内的工厂");
        break;
      case 2:
        setTopTitle("5公里内的工厂");
        break;
      case 3:
        setTopTitle("同城的工厂");
        break;
    }

  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestFactoryRegion(int regionType) {
    showProgressBar();
    request(new FactoryRegionRequest(regionType, 1), new OnResponse2<FactoryRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FactoryRegionResponse response) {
        mAdapter = new FactoryRegionAdapter2(response.object.lists);
        mAlvFactories.setAdapter(mAdapter);

        hideProgressBar();
      }
    }, FactoryRegionResponse.class);
  }
}
