package com.utree.eightysix.app.feed;

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
import com.utree.eightysix.request.FeedsRequest;
import com.utree.eightysix.request.PostPraiseCancelRequest;
import com.utree.eightysix.request.PostPraiseRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

import java.util.Iterator;

/**
 * @author simon
 */
public class FeedFragment extends AbsFeedFragment {

  public FeedFragment() {
  }

  @Override
  protected void requestFeeds(final int id, final int page) {
    if (getBaseActivity() == null) return;
    if (mRefresherView != null && page == 1) {
      mRefresherView.setRefreshing(true);
      getBaseActivity().setTopSubTitle("");
    }
    getBaseActivity().request(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForRequest(id, response, page);
      }
    }, FeedsResponse.class);

  }

  @Override
  protected void cacheOutFeeds(final int id, final int page) {
    if (getBaseActivity() == null) return;
    getBaseActivity().cacheOut(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        responseForCache(response, page, id);
      }
    }, FeedsResponse.class);
  }

  @Override
  protected void onPullRefresh() {
    U.getAnalyser().trackEvent(getActivity(), "feed_pull_refresh", "feed_all");
  }

  @Override
  protected void onLoadMore(int page) {
    U.getAnalyser().trackEvent(getActivity(), "feed_load_more", String.valueOf(page), "feed_all");
  }


  @Subscribe
  public void onFeedPostPraiseEvent(final FeedPostPraiseEvent event) {
    if (mPostPraiseRequesting) {
      return;
    }
    mPostPraiseRequesting = true;
    if (event.isCancel()) {
      getBaseActivity().request(new PostPraiseCancelRequest(event.getPost().id), new OnResponse2<Response>() {
        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(event.getPost());
          } else if ((response.code & 0xffff) == 0x2286) {
            event.getPost().praised = 0;
          } else {
            event.getPost().praised = 0;
            event.getPost().praise++;
          }
          mFeedAdapter.notifyDataSetChanged();

          mPostPraiseRequesting = false;
        }

        @Override
        public void onResponseError(Throwable e) {
          mPostPraiseRequesting = false;
        }
      }, Response.class);
    } else {
      getBaseActivity().request(new PostPraiseRequest(event.getPost().id), new OnResponse2<Response>() {
        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(event.getPost());
          } else if ((response.code & 0xffff) == 0x2286) {
            event.getPost().praised = 1;
          } else {
            event.getPost().praised = 1;
            event.getPost().praise = Math.max(0, event.getPost().praise - 1);
          }
          mFeedAdapter.notifyDataSetChanged();

          mPostPraiseRequesting = false;
        }

        @Override
        public void onResponseError(Throwable e) {
          mPostPraiseRequesting = false;
        }
      }, Response.class);
    }
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
      if (mCircle != null) {
        requestFeeds(mCircle.id, 1);
      } else {
        requestFeeds(0, 1);
      }
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
