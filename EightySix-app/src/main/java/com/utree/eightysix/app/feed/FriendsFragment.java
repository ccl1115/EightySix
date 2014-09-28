package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.request.FeedsFriendsRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;

/**
 * @author simon
 */
class FriendsFragment extends AbsFeedFragment {

  public FriendsFragment() {}

  @Override
  protected void requestFeeds(int id, final int page) {
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().setTopSubTitle("");
    }
    getBaseActivity().request(new FeedsFriendsRequest(id, page), new OnResponse<FeedsResponse>() {
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