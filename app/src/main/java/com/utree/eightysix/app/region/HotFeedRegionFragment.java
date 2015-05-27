package com.utree.eightysix.app.region;

import android.view.View;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.RefreshFeedEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.contact.ContactsSyncEvent;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.FeedByRegionRequest;
import com.utree.eightysix.request.FeedsHotRequest;
import com.utree.eightysix.request.PostPraiseRequest;
import com.utree.eightysix.response.FeedsByRegionResponse;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

import java.util.Iterator;

/**
 * @author simon
 */
public class HotFeedRegionFragment extends AbsRegionFragment {

  public HotFeedRegionFragment() {
  }

  @Override
  protected void requestRegionFeeds(final int regionType, int distance, int areaType, int areaId, final int page) {
    if (getBaseActivity() == null) return;
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().showRefreshIndicator(true);
    }
    getBaseActivity().setTopSubTitle("");

    U.request("feeds_by_region", new OnResponse2<FeedsByRegionResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FeedsByRegionResponse response) {
        responseForFeedsByRegionRequest(response, page);
      }
    }, FeedsByRegionResponse.class, page, regionType, 1, distance, null, null);
  }

  @Override
  protected void requestFeeds(int circleId, final int page) {
    if (getBaseActivity() == null) return;
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().showRefreshIndicator(true);
    }
    getBaseActivity().setTopSubTitle("");

    U.request("feed_list_host", new OnResponse2<FeedsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(FeedsResponse response) {
        responseForFeedsRequest(response, page);
      }
    }, FeedsResponse.class, circleId, page);
  }

  @Override
  protected void responseForFeedsByRegionRequest(FeedsByRegionResponse response, int page) {
    super.responseForFeedsByRegionRequest(response, page);
    U.getBus().post(new NewHotPostCountEvent(mCircle.id, 0));
  }

  @Override
  protected void responseForFeedsRequest(FeedsResponse response, int page) {
    super.responseForFeedsRequest(response, page);
    U.getBus().post(new NewHotPostCountEvent(mCircle.id, 0));
  }

  @Override
  protected void cacheOutFeedsByRegion(final int regionType, int distance, int areaType, int areaId, final int page) {
    if (getBaseActivity() == null) return;
    getBaseActivity().cacheOut(new FeedByRegionRequest(page, regionType, 1), new OnResponse<FeedsByRegionResponse>() {
      @Override
      public void onResponse(FeedsByRegionResponse response) {
        responseForFeedsByRegionCache(response, page);
      }
    }, FeedsByRegionResponse.class);
  }

  @Override
  protected void cacheOutFeeds(int circle, final int page) {
    if (getBaseActivity() == null) return;
    getBaseActivity().cacheOut(new FeedsHotRequest(circle, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForFeedsCache(response, page);
      }
    }, FeedsResponse.class);
  }

  @Override
  protected void onPullRefresh() {
    U.getAnalyser().trackEvent(getActivity(), "feed_pull_refresh", "feed_region_hot");
  }

  @Override
  protected void onLoadMore(int page) {
    U.getAnalyser().trackEvent(getActivity(), "feed_load_more", String.valueOf(page), "feed_region_hot");
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
          event.getPost().praised = 1;
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
    if (!isActive()) return;
    if (mFeedAdapter != null) {
      if (mCircle != null && mCircle.id == event.getCircleId()) {
        mFeedAdapter.add(event.getPost());
        mRstvEmpty.setVisibility(View.INVISIBLE);
        mLvFeed.setSelection(0);
      }
    }
    if (isAdded()) {
      refresh();
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
    if (isResumed()) {
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
  }

  @Subscribe
  public void onRefreshFeedEvent(RefreshFeedEvent event) {
    refresh();
  }
}
