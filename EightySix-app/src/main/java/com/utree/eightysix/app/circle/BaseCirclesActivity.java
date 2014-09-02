package com.utree.eightysix.app.circle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
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
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 */
@Layout (R.layout.activity_base_circles)
public class BaseCirclesActivity extends BaseActivity {

  private static final int MODE_SELECT = 1;
  private static final int MODE_MY = 2;

  @InjectView (R.id.alv_refresh)
  public AdvancedListView mLvCircles;

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

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
  private Location.OnResult mOnResult;

  private ThemedDialog mCircleSetDialog;

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
      U.getAnalyser().trackEvent(this, "circle_search", "my");
      CircleSearchActivity.start(this, false);
    } else if (mMode == MODE_SELECT) {
      U.getAnalyser().trackEvent(this, "circle_search", "select");
      CircleSearchActivity.start(this, true);
    }
  }

  @OnItemClick (R.id.alv_refresh)
  public void onLvCirclesItemClicked(int position) {
    final Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      if (mMode == MODE_MY) {
        circle.selected = true;
        FeedActivity.start(this, circle, true);
        U.getAnalyser().trackEvent(this, "circle_select", "my");
      } else if (mMode == MODE_SELECT) {
        showCircleSetDialog(circle);
        U.getAnalyser().trackEvent(this, "circle_select", "select");
      }
    }
  }

  @OnItemLongClick (R.id.alv_refresh)
  public boolean onLvCirclesItemLongClicked(int position) {
    final Circle circle = mCircleListAdapter.getItem(position);
    if (circle != null) {
      if (mMode == MODE_MY && !circle.viewGroupType.equals("我所在的圈子")) {
        showCircleSetDialog(circle);
        return true;
      }
    }
    return false;
  }

  @Override
  public void onActionLeftClicked() {
    if (mMode == MODE_MY) {
      U.getAnalyser().trackEvent(this, "circle_title", "my");
      finish();
    } else {
      U.getAnalyser().trackEvent(this, "circle_title", "select");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_base_circles);

    mRbSearchHint.setHint(R.string.search_circles);
    mRbSearchHint.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(2), Color.WHITE));

    mRefresherView.setColorScheme(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    mMode = getIntent().getIntExtra("mode", MODE_MY);
    setTopTitle(mMode == MODE_MY ? getString(R.string.my_circles) : getString(R.string.select_circle));

    if (mMode == MODE_MY) {
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
        public TopBar.LayoutParams getLayoutParams(int position) {
          return new TopBar.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        }
      });
    }

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
          if (mPageInfo != null) {
            U.getAnalyser().trackEvent(BaseCirclesActivity.this,
                "circle_load_more", String.valueOf(mPageInfo.currPage + 1));
          }
          if (mRefreshed) {
            requestCircles(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
          } else {
            cacheOutCircles(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
          }
          return true;
        }
      });

      mRefresherView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          showRefreshIndicator(true);
          U.getAnalyser().trackEvent(BaseCirclesActivity.this, "circle_pull_refresh");
          mRefreshed = true;
          requestCircles(1);
          requestLocation();
        }

        @Override
        public void onDrag() {
          showRefreshIndicator(false);
        }

        @Override
        public void onCancel() {
          hideRefreshIndicator();
        }
      });
    }

    if (mMode == MODE_SELECT) {
      setActionLeftDrawable(null);
    }
  }

  @Override
  public void onBackPressed() {
    if (mMode != MODE_SELECT) {
      super.onBackPressed();
    }
  }

  private void requestLocation() {
    long last = Env.getTimestamp("last_location");
    long now = System.currentTimeMillis();
    if (now - last > 7200000) { // 两个小时之后请求新的定位
      M.getLocation().requestLocation();
      Env.setTimestamp("last_location");
    }
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  protected void showCircleSetDialog(final Circle circle) {
    mCircleSetDialog = new ThemedDialog(this);
    mCircleSetDialog.setTitle(String.format("确认在[%s]上班么？", circle.shortName));
    TextView textView = new TextView(this);
    textView.setText("\n请注意：" + (U.getSyncClient().getSync() != null ? U.getSyncClient().getSync().selectFactoryDays : 15) + "天之内不能修改哦\n");
    textView.setPadding(16, 16, 16, 16);
    mCircleSetDialog.setContent(textView);

    mCircleSetDialog.setPositive("设置在职", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        requestCircleSet(circle);
      }
    });
    mCircleSetDialog.setRbNegative("重新选择", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mCircleSetDialog.dismiss();
      }
    });

    mCircleSetDialog.show();
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
        }
        requestCircles(page);
      }
    }, CirclesResponse.class);
  }

  private void requestCircles(final int page) {
    mRequestStarted = true;
    request(mMode == MODE_MY ? new MyCirclesRequest("", page) : new SelectCirclesRequest("", page), new OnResponse2<CirclesResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mLvCircles.stopLoadMore();
        hideProgressBar();
        hideRefreshIndicator();
        mRefresherView.setRefreshing(false);
        mRstvEmpty.setVisibility(View.VISIBLE);
      }

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
        hideRefreshIndicator();
        mRefresherView.setRefreshing(false);
      }


    }, CirclesResponse.class);
  }

  private void requestCircleSet(final Circle circle) {
    request(new CircleSetRequest(circle.id), new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          FeedActivity.start(BaseCirclesActivity.this, circle, true);
          finish();
        }
      }
    }, Response.class);
  }
}