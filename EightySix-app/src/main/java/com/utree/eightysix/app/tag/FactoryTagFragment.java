package com.utree.eightysix.app.tag;

import com.utree.eightysix.U;
import com.utree.eightysix.request.FactoryTagRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.response.TagFeedsResponse;
import com.utree.eightysix.rest.OnResponse2;

/**
 */
public class FactoryTagFragment extends AbsTagFragment {

  @Override
  protected void requestFeeds(final int id, final int page) {
    if (getBaseActivity() == null) {
      return;
    }

    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
    }

    getBaseActivity().request(new FactoryTagRequest(page, id), new OnResponse2<TagFeedsResponse>() {
      @Override
      public void onResponse(TagFeedsResponse response) {
        responseForRequest(id, response, page);
      }

      @Override
      public void onResponseError(Throwable e) {
        mRefresherView.setRefreshing(false);
        mLvFeed.stopLoadMore();
        getBaseActivity().hideProgressBar();
        getBaseActivity().hideRefreshIndicator();
      }
    }, TagFeedsResponse.class);
  }

  @Override
  protected void cacheOutFeeds(final int id, final int page) {
    if (getBaseActivity() == null) {
      return;
    }

    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
    }

    getBaseActivity().request(new FactoryTagRequest(page, id), new OnResponse2<TagFeedsResponse>() {
      @Override
      public void onResponse(TagFeedsResponse response) {
        responseForRequest(id, response, page);
      }

      @Override
      public void onResponseError(Throwable e) {

        mRefresherView.setRefreshing(false);
        mLvFeed.stopLoadMore();
        getBaseActivity().hideProgressBar();
        getBaseActivity().hideRefreshIndicator();
      }
    }, TagFeedsResponse.class);
  }

  @Override
  protected void onPullRefresh() {
    U.getAnalyser().trackEvent(U.getContext(), "tag_pull_refresh", "tag_factory");
  }

  @Override
  protected void onLoadMore(int page) {
    U.getAnalyser().trackEvent(U.getContext(), "tag_load_more", "tag_factory");
  }
}
