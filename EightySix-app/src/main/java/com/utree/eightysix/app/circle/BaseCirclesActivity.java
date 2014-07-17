package com.utree.eightysix.app.circle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.request.CircleSetRequest;
import com.utree.eightysix.request.MyCirclesRequest;
import com.utree.eightysix.request.SelectCirclesRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.IRefreshable;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.RefresherView;
import com.utree.eightysix.widget.TopBar;

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

  @InjectView (R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  @InjectView (R.id.tv_search_hint)
  public EditText mRbSearchHint;

  protected CircleListAdapter mCircleListAdapter;

  private int mMode;

  private boolean mRefreshed;

  private Paginate.Page mPageInfo;
  private boolean mLocatingFinished;
  private boolean mRequestStarted;

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

  @OnClick ({R.id.fl_search, R.id.tv_search_hint})
  public void onFlSearchClicked() {
    if (mMode == MODE_MY) {
      CircleSearchActivity.start(this, false);
    } else if (mMode == MODE_SELECT) {
      CircleSearchActivity.start(this, true);
    }
  }

  @OnItemClick (R.id.alv_refresh)
  public void onLvCirclesItemClicked(int position) {
    final Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      if (mMode == MODE_MY) {
        circle.selected = true;
        FeedActivity.start(this, circle);
      } else if (mMode == MODE_SELECT) {
        showCircleSetDialog(circle);
      }
    }
  }

  @OnItemLongClick (R.id.alv_refresh)
  public boolean onLvCirclesItemLongClicked(int position) {
    final Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      if (mMode == MODE_MY && !circle.viewGroupType.equals("在职企业")) {
        showCircleSetDialog(circle);
        return true;
      }
    }
    return false;
  }

  protected void showCircleSetDialog(final Circle circle) {
    AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle(String.format("确认在%s上班么？", circle.name))
        .setMessage("15天之内不能修改在职工厂")
        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            requestCircleSet(circle);
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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_base_circles);

    mRbSearchHint.setHint(R.string.search_circles);
    mRbSearchHint.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(2), Color.WHITE));

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
      public FrameLayout.LayoutParams getLayoutParams(int position) {
        return new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
      }
    });

    if (U.useFixture()) {
      mCircleListAdapter = new CircleListAdapter(U.getFixture(Circle.class, 20, "valid"));
      mLvCircles.setAdapter(mCircleListAdapter);
      mLvCircles.setLoadMoreCallback(new LoadMoreCallback() {
        @Override
        public View getLoadMoreView(ViewGroup parent) {
          return LayoutInflater.from(BaseCirclesActivity.this).inflate(R.layout.footer_load_more, parent, false);
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
        public View getLoadMoreView(ViewGroup parent) {
          return LayoutInflater.from(BaseCirclesActivity.this).inflate(R.layout.footer_load_more, parent, false);
        }

        @Override
        public boolean hasMore() {
          return (mPageInfo != null) && (mPageInfo.currPage < mPageInfo.countPage);
        }

        @Override
        public boolean onLoadMoreStart() {
          if (mRefreshed) {
            requestCircles(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
          } else {
            cacheOutCircles(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
          }
          return true;
        }
      });

      mRefresherView.setOnRefreshListener(new IRefreshable.OnRefreshListener() {

        @Override
        public void onStateChanged(IRefreshable.State state) {
        }

        @Override
        public void onPreRefresh() {
          mRefreshed = true;
          requestCircles(1);
        }

        @Override
        public void onRefreshData() {
        }

        @Override
        public void onRefreshUI() {

        }
      });
    }

    U.getLocation().requestLocation(new Location.OnResult() {
      @Override
      public void onResult(Location.Result result) {
        mLocatingFinished = true;
        if (mRequestStarted) {
          requestCircles(1);
        }
      }
    });

    if (mMode == MODE_SELECT) {
      setActionLeftDrawable(null);
    }
  }

  @Override
  public void onActionLeftClicked() {
    if (mMode == MODE_MY) {
      finish();
    }
  }

  @Override
  public void onBackPressed() {
    if (mMode != MODE_SELECT) {
      super.onBackPressed();
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private AlertDialog getQuitConfirmDialog() {
    return new AlertDialog.Builder(this).setTitle("建议完成设置以便更好的和朋友互动")
        .setPositiveButton("停止", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            FeedActivity.start(BaseCirclesActivity.this);
            finish();
          }
        }).setNegativeButton("继续", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).create();
  }

  private void cacheOutCircles(final int page) {
    cacheOut(mMode == MODE_MY ? new MyCirclesRequest("", page) : new SelectCirclesRequest("", page), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (response != null && response.code == 0) {
          if (page == 1) {
            mCircleListAdapter = new CircleListAdapter(response.object.lists);
            mLvCircles.setAdapter(mCircleListAdapter);

            if (response.object.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }
          } else if (mCircleListAdapter != null) {
            mCircleListAdapter.add(response.object.lists);
          }
          mPageInfo = response.object.page;
          mLvCircles.stopLoadMore();
          hideProgressBar();
        } else {
          requestCircles(page);
        }
      }
    }, CirclesResponse.class);
  }

  private void requestCircles(final int page) {
    mRequestStarted = true;
    if (mLocatingFinished) {
      request(mMode == MODE_MY ? new MyCirclesRequest("", page) : new SelectCirclesRequest("", page), new OnResponse2<CirclesResponse>() {
        @Override
        public void onResponse(CirclesResponse response) {
          if (RESTRequester.responseOk(response)) {
            if (page == 1) {
              mCircleListAdapter = new CircleListAdapter(response.object.lists);
              mLvCircles.setAdapter(mCircleListAdapter);

              if (response.object.lists.size() == 0) {
                mRstvEmpty.setVisibility(View.VISIBLE);
              } else {
                mRstvEmpty.setVisibility(View.GONE);
              }
            } else if (mCircleListAdapter != null) {
              mCircleListAdapter.add(response.object.lists);
            }
            mPageInfo = response.object.page;
          } else {
            if (mCircleListAdapter == null || mCircleListAdapter.getCount() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            }
          }
          mLvCircles.stopLoadMore();
          hideProgressBar();
          mRefresherView.hideHeader();
        }

        @Override
        public void onResponseError(Throwable e) {
          mLvCircles.stopLoadMore();
          hideProgressBar();
          mRefresherView.hideHeader();
          mRstvEmpty.setVisibility(View.VISIBLE);
        }
      }, CirclesResponse.class);
    }
  }

  private void requestCircleSet(final Circle circle) {
    request(new CircleSetRequest(circle.id), new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          FeedActivity.start(BaseCirclesActivity.this, circle);
          finish();
        }
      }
    }, Response.class);
  }
}