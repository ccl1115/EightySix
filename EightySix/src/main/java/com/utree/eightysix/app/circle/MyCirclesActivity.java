package com.utree.eightysix.app.circle;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.response.data.Circle;
import com.utree.eightysix.rest.FixtureUtil;
import com.utree.eightysix.widget.LoadMoreAdapterWrapper;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout (R.layout.activity_my_circles)
@TopTitle (R.string.my_circles)
public class MyCirclesActivity extends BaseActivity {

  @InjectView (R.id.fl_search)
  public FrameLayout mFlSearch;

  @InjectView (R.id.lv_circles)
  public ListView mLvCircles;

  @InjectView (R.id.tv_empty_text)
  public TextView mTvEmptyText;
  private LoadMoreAdapterWrapper mAdapterWrapper;
  private CircleListAdapter mCircleListAdapter;

  @OnClick (R.id.fl_search)
  public void onFlSearchClicked() {
    startActivity(new Intent(this, CircleSearchActivity.class));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return getString(R.string.create);
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        startActivity(new Intent(MyCirclesActivity.this, CircleCreateActivity.class));
      }

      @Override
      public int getCount() {
        return 1;
      }
    });

    mLvCircles.setEmptyView(mTvEmptyText);

    mCircleListAdapter =
        new CircleListAdapter(((CirclesResponse) FixtureUtil.get(C.API_FACTORY_MY)).object.factoryCircle.lists);

    mAdapterWrapper =
        new LoadMoreAdapterWrapper(mCircleListAdapter, new LoadMoreAdapterWrapper.LoadMoreCallback() {
          @Override
          public View getLoadMoreView() {
            return View.inflate(MyCirclesActivity.this, R.layout.footer_load_more, null);
          }

          @Override
          public boolean hasMore() {
            return true;
          }

          @Override
          public boolean onLoadMoreStart() {
            getHandler().postDelayed(new Runnable() {
              @Override
              public void run() {
                mCircleListAdapter.add(Fixture.from(Circle.class).<Circle>gimme(20, "valid", new Rule() {
                      {
                        add("viewGroupType", "智能推荐");
                        add("viewType", 4);
                      }
                    }
                ));
                mCircleListAdapter.notifyDataSetChanged();
                mAdapterWrapper.stopLoadMore();
              }
            }, 1000);
            return true;
          }
        });

    mLvCircles.setAdapter(mAdapterWrapper);
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }
}