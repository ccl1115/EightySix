package com.utree.eightysix.app.circle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.EmotionOnRefreshListener;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.request.MyCirclesRequest;
import com.utree.eightysix.request.SelectCirclesRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RefresherView;
import com.utree.eightysix.widget.TopBar;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Layout (R.layout.activity_base_circles)
public class BaseCirclesActivity extends BaseActivity {

  private static final int MODE_SELECT = 1;
  private static final int MODE_MY = 2;

  @InjectView (R.id.fl_search)
  public FrameLayout mFlSearch;

  @InjectView (R.id.alv_refresh)
  public AdvancedListView mLvCircles;

  @InjectView (R.id.refresh_view)
  public RefresherView mRefresherView;

  @InjectView (R.id.tv_empty_text)
  public TextView mTvEmptyText;

  @InjectView (R.id.tv_head)
  public TextView mTvHead;

  protected CircleListAdapter mCircleListAdapter;
  protected List<Circle> mCircles;

  private int mMode;

  private boolean mRefreshed;

  private Paginate.Page mPageInfo;

  public static void startSelect(Context context) {
    Intent intent = new Intent(context, BaseCirclesActivity.class);
    intent.putExtra("mode", MODE_SELECT);
    context.startActivity(intent);
  }

  public static void startMyCircles(Context context) {
    Intent intent = new Intent(context, BaseCirclesActivity.class);
    intent.putExtra("mode", MODE_MY);
    context.startActivity(intent);
  }

  @OnClick (R.id.fl_search)
  public void onFlSearchClicked() {
    startActivity(new Intent(this, CircleSearchActivity.class));
  }

  @OnItemClick (R.id.alv_refresh)
  public void onLvCirclesItemClicked(int position) {
    final Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      if (mMode == MODE_MY) {
        circle.selected = true;
        FeedActivity.start(this, circle);
      } else if (mMode == MODE_SELECT) {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("确认在" + circle.name + "上班么？")
            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                circle.selected = true;
                FeedActivity.start(BaseCirclesActivity.this, circle,
                    (ArrayList<Circle>) mCircleListAdapter.getCircles());
              }
            })
            .setNegativeButton("重新选择", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            }).create();

        dialog.show();
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_base_circles);

    mTvEmptyText.setText("");

    mMode = getIntent().getIntExtra("mode", MODE_MY);
    setTopTitle(mMode == MODE_MY ? getString(R.string.my_circles) : getString(R.string.select_circle));

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
        startActivity(new Intent(BaseCirclesActivity.this, CircleCreateActivity.class));
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public ViewGroup.LayoutParams getLayoutParams(int position) {
        return new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
      }
    });

    mLvCircles.setEmptyView(mTvEmptyText);

    if (U.useFixture()) {
      mCircleListAdapter = new CircleListAdapter(U.getFixture(Circle.class, 20, "valid"));
      mLvCircles.setAdapter(mCircleListAdapter);
      mLvCircles.setLoadMoreCallback(new LoadMoreCallback() {
        @Override
        public View getLoadMoreView() {
          return View.inflate(BaseCirclesActivity.this, R.layout.footer_load_more, null);
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
              mCircleListAdapter.add(U.getFixture(Circle.class, 20, "valid"));
              mLvCircles.stopLoadMore();
            }
          }, 2000);
          return true;
        }
      });
    } else {
      cacheOutCircles(1);
      showProgressBar();

      mLvCircles.setLoadMoreCallback(new LoadMoreCallback() {
        @Override
        public View getLoadMoreView() {
          return View.inflate(BaseCirclesActivity.this, R.layout.footer_load_more, null);
        }

        @Override
        public boolean hasMore() {
          return mPageInfo.currPage < mPageInfo.countPage;
        }

        @Override
        public boolean onLoadMoreStart() {
          if (mRefreshed) {
            requestCircles(mPageInfo.currPage + 1);
          } else {
            cacheOutCircles(mPageInfo.currPage + 1);
          }
          return true;
        }
      });

      mRefresherView.setOnRefreshListener(new EmotionOnRefreshListener(mTvHead) {

        @Override
        public void onPreRefresh() {
          super.onPreRefresh();
          mRefreshed = true;
          requestCircles(1);
        }

        @Override
        public void onRefreshData() {
        }
      });
    }

    if (U.useFixture()) {
      mRefresherView.setOnRefreshListener(new EmotionOnRefreshListener(mTvHead) {
        @Override
        public void onRefreshData() {
          mCircleListAdapter = new CircleListAdapter(U.getFixture(Circle.class, 20, "valid"));
          mLvCircles.setAdapter(mCircleListAdapter);
        }
      });
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }

  private void cacheOutCircles(final int page) {
    cacheOut(mMode == MODE_MY ? new MyCirclesRequest("", page) : new SelectCirclesRequest("", page), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0) {
          if (page == 1) {
            mCircleListAdapter = new CircleListAdapter(response.object.lists);
            mLvCircles.setAdapter(mCircleListAdapter);
            mPageInfo = response.object.page;
          } else if (mCircleListAdapter != null) {
            mCircleListAdapter.add(response.object.lists);
            mPageInfo = response.object.page;
          }
          mLvCircles.stopLoadMore();
          hideProgressBar();
        } else {
          requestCircles(page);
        }
      }
    }, CirclesResponse.class);
  }

  private void requestCircles(final int page) {
    request(mMode == MODE_MY ? new MyCirclesRequest("", page) : new SelectCirclesRequest("", page), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0) {
          if (page == 1) {
            mCircleListAdapter = new CircleListAdapter(response.object.lists);
            mLvCircles.setAdapter(mCircleListAdapter);
            mPageInfo = response.object.page;
            if (mRefreshed) mRefresherView.hideHeader();
          } else if (mCircleListAdapter != null) {
            mCircleListAdapter.add(response.object.lists);
            mPageInfo = response.object.page;
          }
        } else {
          mTvEmptyText.setText(getString(R.string.no_circles));
        }
        mLvCircles.stopLoadMore();
        hideProgressBar();
      }
    }, CirclesResponse.class);
  }
}