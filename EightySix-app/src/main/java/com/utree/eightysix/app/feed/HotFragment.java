package com.utree.eightysix.app.feed;

import com.utree.eightysix.request.FeedsFriendsRequest;
import com.utree.eightysix.request.FeedsHotRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;

/**
 * @author simon
 */
class HotFragment extends AbsFeedFragment {

  public HotFragment() {}

  @Override
  protected void requestFeeds(int id, final int page) {
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().setTopSubTitle("");
    }
    getBaseActivity().request(new FeedsHotRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForRequest(response, page);
      }
    }, FeedsResponse.class);

  }

  @Override
  protected void cacheOutFeeds(final int id, final int page) {
    getBaseActivity().cacheOut(new FeedsFriendsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForCache(response, page, id);
      }
    }, FeedsResponse.class);
  }

}