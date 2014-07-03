package com.utree.eightysix.app.circle;

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
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.request.SearchCircleRequest;
import com.utree.eightysix.response.CirclesResponse;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RoundedButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private List<String> mSearchHistory;
  private View mFooterClearSearch;

  private CircleBaseListAdapter mResultAdapter;

  private List<Circle> mData;

  @OnClick (R.id.rb_create_circle)
  public void onRbCreateCircleClicked() {
    // TODO start create circle activity
    if (BuildConfig.DEBUG) showToast("TODO start create circle activity");
  }

  @OnItemClick (R.id.lv_history)
  public void onHistoryItemClicked(int position) {
    getTopBar().getSearchEditText().setText(mSearchHistory.get(position));
    requestSearch(1, mSearchHistory.get(position));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().enterSearch();

    mSearchHistory = Account.inst().getSearchHistory();

    mFooterClearSearch = View.inflate(this, R.layout.footer_clear_history, null);

    mLvHistory.addFooterView(mFooterClearSearch, null, false);
    U.viewBinding(mFooterClearSearch, mHistoryFooterViewHolder);

    updateHistoryData();

    showHistory();

    mLvResult.setEmptyView(mLlEmptyResult);
  }

  @Override
  protected void onActionLeftOnClicked() {
    finish();
  }

  @Override
  protected void onSearchActionGo(String keyword) {
    requestSearch(1, keyword);


    //region load history keyword
    for (String k : mSearchHistory) {
      if (k.equals(keyword)) {
        return;
      }
    }
    mSearchHistory.add(keyword);
    Account.inst().setSearchHistory(mSearchHistory);
    updateHistoryData();
    //endregion


    mData = new ArrayList<Circle>();
    mResultAdapter = new CircleBaseListAdapter(mData);

  }

  @Override
  protected void onSearchTextChanged(String newKeyword) {
    if (TextUtils.isEmpty(newKeyword)) {
      showHistory();
    }
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

  private void showSearchResult() {
    mLvResult.setVisibility(View.VISIBLE);
    mLvHistory.setVisibility(View.GONE);


    if (U.useFixture()) {
      mResultAdapter = new CircleBaseListAdapter(U.getFixture(Circle.class, 20, "valid"));
      mLvResult.setAdapter(mResultAdapter);
      mLvResult.setLoadMoreCallback(new LoadMoreCallback() {
        @Override
        public View getLoadMoreView(ViewGroup parent) {
          return LayoutInflater.from(CircleSearchActivity.this).inflate(R.layout.footer_load_more, parent, false);
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
              mResultAdapter.add(U.getFixture(Circle.class, 20, "valid"));
              mLvResult.stopLoadMore();
            }
          }, 2000);
          return true;
        }
      });
    } else {
      //TODO request more data
    }
  }

  private void requestSearch(int page, String keyword) {
    request(new SearchCircleRequest(page, keyword), new OnResponse<CirclesResponse>() {

      @Override
      public void onResponse(CirclesResponse response) {
        //if (response != null) {
          showSearchResult();
        //}
        hideProgressBar();
      }
    }, CirclesResponse.class);
    mTvEmptyText.setText(String.format(getString(R.string.no_search_result), keyword));
    showProgressBar();
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

  /**
   * When LogoutEvent fired, finish myself
   *
   * @param event the logout event
   */
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

}