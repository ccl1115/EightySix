package com.utree.eightysix.app.region;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.request.FactoryRegionRequest;
import com.utree.eightysix.response.FactoryRegionResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;

/**
 */
@Layout(R.layout.activity_factory_region)
public class FactoryRegionActivity extends BaseActivity {

  @InjectView(R.id.content)
  public AdvancedListView mAlvFactories;

  public Paginate.Page mPageInfo;

  private FactoryRegionAdapter2 mAdapter;
  private int mRegionType;

  @OnItemClick(R.id.content)
  public void onAlvFactoriesItemClicked(int position) {
    Circle item = mAdapter.getItem(position);
    if (item.currFactory == 1) {
      HomeActivity.start(this);
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

    mRegionType = getIntent().getIntExtra("regionType", 1);

    requestFactoryRegion(mRegionType, 1);

    switch (mRegionType) {
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

    mAlvFactories.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && mPageInfo.currPage < mPageInfo.countPage;
      }

      @Override
      public boolean onLoadMoreStart() {
        if (mPageInfo != null) {
          requestFactoryRegion(mRegionType, mPageInfo.currPage + 1);
          return true;
        } else {
          return false;
        }
      }
    });

  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestFactoryRegion(int regionType, final int page) {
    if (page == 1) {
      showProgressBar();
    }
    request(new FactoryRegionRequest(regionType, page), new OnResponse2<FactoryRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
        mAlvFactories.stopLoadMore();
      }

      @Override
      public void onResponse(FactoryRegionResponse response) {
        if (page == 1) {
          mAdapter = new FactoryRegionAdapter2(response.object.lists);
          mAlvFactories.setAdapter(mAdapter);
        } else {
          mAdapter.add(response.object.lists);
        }

        mPageInfo = response.object.page;

        hideProgressBar();
        mAlvFactories.stopLoadMore();
      }
    }, FactoryRegionResponse.class);
  }
}
