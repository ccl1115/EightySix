package com.utree.eightysix.app.circle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.home.HomeTabActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.request.CircleSetRequest;
import com.utree.eightysix.request.SearchCircleRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.*;

import java.util.*;

/**
 */
@Layout (R.layout.activity_circle_search)
public class CircleSearchActivity extends BaseActivity {

  private final HistoryFooterViewHolder mHistoryFooterViewHolder = new HistoryFooterViewHolder();

  @InjectView (R.id.lv_history)
  public ListView mLvHistory;

  @InjectView (R.id.lv_result)
  public AdvancedListView mLvResult;

  @InjectView (R.id.ll_empty_result)
  public LinearLayout mLlEmptyResult;

  @InjectView (R.id.rb_create_circle)
  public RoundedButton mRbCreateCircle;

  @InjectView (R.id.tv_empty_text)
  public TextView mTvEmptyText;

  @InjectView (R.id.rstv_empty)
  public RandomSceneTextView mRstvHistoryEmpty;

  private List<String> mSearchHistory;
  private View mFooterClearSearch;

  private CircleBaseListAdapter mResultAdapter;

  private Paginate.Page mPageInfo;

  private String mLastKeyword;

  private boolean mSelectMode;

  private ThemedDialog mCircleSetDialog;

  public static void start(Context context, boolean select) {
    Intent intent = new Intent(context, CircleSearchActivity.class);
    intent.putExtra("select", select);
    context.startActivity(intent);
  }

  @OnClick (R.id.rb_create_circle)
  public void onRbCreateCircleClicked() {
    startActivity(new Intent(this, CircleCreateActivity.class));
    finish();
  }

  @OnItemClick (R.id.lv_history)
  public void onHistoryItemClicked(int position) {
    U.getAnalyser().trackEvent(this, "search_history", "search_history");
    String keyword = mSearchHistory.get(position);
    mTopBar.getSearchEditText().setText(keyword);
    mLastKeyword = keyword;
    requestSearch(1, keyword);

    //region load history keyword
    for (Iterator<String> iterator = mSearchHistory.iterator(); iterator.hasNext(); ) {
      String k = iterator.next();
      if (k.equals(keyword)) {
        iterator.remove();
      }
    }
    mSearchHistory.add(0, keyword);
    Account.inst().setSearchHistory(mSearchHistory);
    //updateHistoryData();
    //endregion
  }

  @OnItemClick (R.id.lv_result)
  public void onResultItemClicked(int position) {
    U.getAnalyser().trackEvent(this, "search_result", "search_result");
    final Circle circle = mResultAdapter.getItem(position);
    if (circle != null) {
      if (mSelectMode) {
        showCircleSetDialog(circle);
      } else {
        FeedActivity.start(this, circle);
        finish();
      }
    }
  }

  @OnItemLongClick (R.id.lv_result)
  public boolean onLvResultItemLongClicked(int position) {
    final Circle circle = mResultAdapter.getItem(position);
    if (circle != null) {
      showCircleSetDialog(circle);
      return true;
    }
    return false;
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onSearchTextChanged(CharSequence cs) {
    if (TextUtils.isEmpty(cs)) {
      showHistory();
    }
  }

  @Override
  public void onActionSearchClicked(CharSequence cs) {
    String keyword = cs.toString();
    mLastKeyword = keyword;
    requestSearch(1, keyword);

    //region load history keyword
    for (Iterator<String> iterator = mSearchHistory.iterator(); iterator.hasNext(); ) {
      String k = iterator.next();
      if (k.equals(keyword)) {
        iterator.remove();
      }
    }
    mSearchHistory.add(0, keyword);
    Account.inst().setSearchHistory(mSearchHistory);
    //updateHistoryData();
    //endregion
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mSelectMode = getIntent().getBooleanExtra("select", false);

    mTopBar.enterSearch();

    mSearchHistory = Account.inst().getSearchHistory();

    mFooterClearSearch = View.inflate(this, R.layout.footer_clear_history, null);

    mLvHistory.addFooterView(mFooterClearSearch, null, false);
    U.viewBinding(mFooterClearSearch, mHistoryFooterViewHolder);

    updateHistoryData();

    showHistory();

    mLvResult.setEmptyView(mLlEmptyResult);

    mLvResult.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(CircleSearchActivity.this).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && mPageInfo.currPage < mPageInfo.countPage;
      }

      @Override
      public boolean onLoadMoreStart() {
        if (mPageInfo != null) {
          U.getAnalyser().trackEvent(CircleSearchActivity.this, "search_load_more", String.valueOf(mPageInfo.currPage + 1));
          requestSearch(mPageInfo.currPage + 1, mLastKeyword);
          return true;
        }
        return false;
      }
    });

  }

  /**
   * When LogoutEvent fired, finish myself
   *
   * @param event the logout event
   */
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

  private void updateHistoryData() {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    final String[] from = new String[]{"search"};
    final int[] to = new int[]{R.id.tv_name};

    for (String search : mSearchHistory) {
      Map<String, String> item = new HashMap<String, String>();
      item.put(from[0], search);

      data.add(item);
    }

    if (data.size() == 0) {
      mRstvHistoryEmpty.setVisibility(View.VISIBLE);
    } else {
      mRstvHistoryEmpty.setVisibility(View.GONE);
    }

    mLvHistory.setAdapter(new SimpleAdapter(this, data, R.layout.item_circle_simple, from, to));

    if (mSearchHistory.size() > 0) {
      mFooterClearSearch.setVisibility(View.VISIBLE);
    } else {
      mFooterClearSearch.setVisibility(View.GONE);
    }
  }

  private void clearHistory() {
    mSearchHistory.clear();
    Account.inst().setSearchHistory(mSearchHistory);

    updateHistoryData();
  }

  private void showHistory() {
    mLvResult.setVisibility(View.GONE);
    mLvHistory.setVisibility(View.VISIBLE);
  }

  private void requestSearch(final int page, final String keyword) {
    request(new SearchCircleRequest(page, keyword), new OnResponse<CirclesResponse>() {
      @Override
      public void onResponse(CirclesResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mLvResult.setVisibility(View.VISIBLE);
            mLvHistory.setVisibility(View.GONE);

            mResultAdapter = new CircleBaseListAdapter(response.object.lists);
            mLvResult.setAdapter(mResultAdapter);
            mPageInfo = response.object.page;
          } else {
            mResultAdapter.add(response.object.lists);
            mPageInfo = response.object.page;
          }
        } else {
          mLvResult.setAdapter(null);
          mLvResult.setVisibility(View.VISIBLE);
          mLvHistory.setVisibility(View.GONE);
          mTvEmptyText.setText(String.format(getString(R.string.no_search_result), keyword));
        }
        hideProgressBar();
        hideSoftKeyboard(mTopBar.getSearchEditText());
        updateHistoryData();
      }
    }, CirclesResponse.class);
    showProgressBar();
  }

  private void requestCircleSet(final Circle circle) {
    request(new CircleSetRequest(circle.id), new OnResponse<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          HomeTabActivity.start(CircleSearchActivity.this);
          finish();
        }
      }
    }, Response.class);
  }

  @Keep
  public class HistoryFooterViewHolder {
    @InjectView (R.id.rb_clear_history)
    public RoundedButton mRbClearHistory;

    @OnClick (R.id.rb_clear_history)
    public void onRbClearHistory() {
      clearHistory();
    }
  }

}