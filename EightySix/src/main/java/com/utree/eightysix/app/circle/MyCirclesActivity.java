package com.utree.eightysix.app.circle;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
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
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.TopBar;

/**
 */
@Layout (R.layout.activity_my_circles)
@TopTitle (R.string.my_circles)
public class MyCirclesActivity extends BaseActivity {

  @InjectView (R.id.fl_search)
  public FrameLayout mFlSearch;

  @InjectView (R.id.lv_circles)
  public AdvancedListView mLvCircles;

  @InjectView (R.id.tv_empty_text)
  public TextView mTvEmptyText;

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

    mLvCircles.setAdapter(mCircleListAdapter);
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }
}