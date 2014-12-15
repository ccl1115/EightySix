package com.utree.eightysix.app.tag;

import android.os.Bundle;
import android.view.View;
import com.utree.eightysix.request.HotTagRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.response.TagFeedsResponse;
import com.utree.eightysix.rest.OnResponse2;

/**
 */
public class HotTagFragment extends AbsTagFragment {

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRstvEmpty.setText("这个标签下还没有热门的帖子哟");
    mRstvEmpty.setSubText("快快顶帖，或去其他的标签看看吧");
  }

  @Override
  protected void requestFeeds(final int id, final int page) {
    if (getBaseActivity() == null) {
      return;
    }

    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
    }

    getBaseActivity().request(new HotTagRequest(page, id), new OnResponse2<TagFeedsResponse>() {
      @Override
      public void onResponse(TagFeedsResponse response) {
        responseForRequest(id, response, page, TagFeedAdapter.FEED_HOT);
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

    getBaseActivity().request(new HotTagRequest(page, id), new OnResponse2<TagFeedsResponse>() {
      @Override
      public void onResponse(TagFeedsResponse response) {
        responseForRequest(id, response, page, TagFeedAdapter.FEED_HOT);
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

  }

  @Override
  protected void onLoadMore(int page) {

  }
}
