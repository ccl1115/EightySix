package com.utree.eightysix.app.tag;

import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.AbsFeedFragment;
import com.utree.eightysix.request.FeedsRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse2;

/**
 */
public class FactoryTagFragment extends AbsFeedFragment {
  @Override
  protected void requestFeeds(final int id, final int page) {
    getBaseActivity().request(new FeedsRequest(id, page), new OnResponse2<FeedsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FeedsResponse response) {
        responseForRequest(id, response, page);
      }
    }, FeedsResponse.class);
  }

  @Override
  protected void cacheOutFeeds(final int id, final int page) {
    getBaseActivity().cacheOut(new FeedsRequest(id, page), new OnResponse2<FeedsResponse>() {

      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FeedsResponse response) {
        responseForCache(response, page, id);
      }
    }, FeedsResponse.class);
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
