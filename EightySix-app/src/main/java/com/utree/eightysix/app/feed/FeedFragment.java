package com.utree.eightysix.app.feed;

import android.view.View;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.UpdatePraiseCountEvent;
import com.utree.eightysix.request.FeedsRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.Env;

import java.util.Iterator;

/**
 * @author simon
 */
class FeedFragment extends AbsFeedFragment {

  public FeedFragment() {
  }

  @Override
  protected void requestFeeds(int id, final int page) {
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().setTopSubTitle("");
    }
    getBaseActivity().request(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForRequest(response, page);
      }
    }, FeedsResponse.class);

  }

  @Override
  protected void cacheOutFeeds(final int id, final int page) {
    getBaseActivity().cacheOut(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForCache(response, page, id);
      }
    }, FeedsResponse.class);
  }

}
