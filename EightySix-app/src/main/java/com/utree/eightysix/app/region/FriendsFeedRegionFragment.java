package com.utree.eightysix.app.region;

import android.view.View;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.RefreshFeedEvent;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.FeedByRegionRequest;
import com.utree.eightysix.request.PostPraiseRequest;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.TopBar;

import java.util.Iterator;

/**
 * @author simon
 */
public class FriendsFeedRegionFragment extends AbsRegionFragment {

  public FriendsFeedRegionFragment() {
  }

  @Override
  protected void requestFeeds(final int regionType, final int page) {
    if (getBaseActivity() == null) return;
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().showRefreshIndicator(true);
    }
    switch (regionType) {
      case 0:
        getBaseActivity().setTopTitle(mCircle == null ? "" : mCircle.shortName);
        getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_ONE);
        break;
      case 1:
        getBaseActivity().setTopTitle("1公里内");
        getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);
        break;
      case 2:
        getBaseActivity().setTopTitle("5公里内");
        getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);
        break;
      case 3:
        getBaseActivity().setTopTitle("同城");
        getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);
        break;
    }
    getBaseActivity().setTopSubTitle("");
    getBaseActivity().request(new FeedByRegionRequest(page, regionType, 2), new OnResponse<FeedsByRegionResponse>() {
      @Override
      public void onResponse(FeedsByRegionResponse response) {
        responseForRequest(response, regionType, page);
      }
    }, FeedsByRegionResponse.class);

  }

  @Override
  protected void cacheOutFeeds(final int regionType, final int page) {
    if (getBaseActivity() == null) return;
    getBaseActivity().cacheOut(new FeedByRegionRequest(page, regionType, 2), new OnResponse<FeedsByRegionResponse>() {
      @Override
      public void onResponse(FeedsByRegionResponse response) {
        responseForCache(response, regionType, page);
      }
    }, FeedsByRegionResponse.class);
  }

  @Override
  protected void onPullRefresh() {
    U.getAnalyser().trackEvent(getActivity(), "feed_pull_refresh", "feed_region_friends");
  }

  @Override
  protected void onLoadMore(int page) {
    U.getAnalyser().trackEvent(getActivity(), "feed_load_more", String.valueOf(page), "feed_region_friends");
  }


  @Subscribe
  public void onFeedPostPraiseEvent(final FeedPostPraiseEvent event) {
    if (mPostPraiseRequesting) {
      return;
    }
    mPostPraiseRequesting = true;
    getBaseActivity().request(new PostPraiseRequest(event.getPost().id), new OnResponse2<Response>() {
      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.getBus().post(event.getPost());
        } else if ((response.code & 0xffff) == 0x2286) {
          event.getPost().praised = 0;
          mFeedAdapter.notifyDataSetChanged();
        } else {
          event.getPost().praised = 0;
          event.getPost().praise = Math.max(0, event.getPost().praise - 1);
          mFeedAdapter.notifyDataSetChanged();
        }

        mPostPraiseRequesting = false;
      }

      @Override
      public void onResponseError(Throwable e) {
                                               mPostPraiseRequesting = false;
                                                                                                                  }
    }, Response.class);
  }

  @Subscribe
  public void onPostPublishedEvent(PostPublishedEvent event) {
    if (mFeedAdapter != null) {
      if (mCircle != null && mCircle.id == event.getCircleId()) {
        mFeedAdapter.add(event.getPost());
        mRstvEmpty.setVisibility(View.INVISIBLE);
        mLvFeed.setSelection(0);
      }
    }
    if (isAdded()) {
      requestFeeds(getRegionType(), 1);
    }
  }

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    if (mFeedAdapter == null || mFeedAdapter.getFeeds() == null) return;
    for (Iterator<BaseItem> iterator = mFeedAdapter.getFeeds().posts.lists.iterator(); iterator.hasNext(); ) {
      BaseItem item = iterator.next();
      if (item != null && item instanceof Post) {
        Post p = ((Post) item);
        if (p.equals(event.getPost())) {
          iterator.remove();
          mFeedAdapter.notifyDataSetChanged();
          break;
        }
      }
    }
  }

  @Subscribe
  public void onContactsSyncEvent(ContactsSyncEvent event) {
    if (mFeedAdapter != null && mFeedAdapter.getFeeds().upContact == 0) {
      if (event.isSucceed()) {
        U.showToast("上传通讯录成功");
      } else {
        U.showToast("上传通讯录失败");
      }
    }

    refresh();
    getBaseActivity().hideProgressBar();
  }

  @Subscribe
  public void onRefreshFeedEvent(RefreshFeedEvent event) {
    refresh();
  }
}
