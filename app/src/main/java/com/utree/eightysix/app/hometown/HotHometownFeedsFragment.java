/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.hometown;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

import java.util.Iterator;

/**
 */
public class HotHometownFeedsFragment extends AbsHometownFeedsFragment {

  private boolean mPostPraiseRequesting;

  public HotHometownFeedsFragment() {
    mTabType = 1;
  }

  @Subscribe
  public void onPostPublishedEvent(PostPublishedEvent event) {
    if (!isActive()) return;
    if (mFeedAdapter != null) {
      mLvFeed.setAdapter(null);
    }

    requestFeeds(1);
  }

  @Subscribe
  public void onFeedPostPraiseEvent(final FeedPostPraiseEvent event) {
    if (mPostPraiseRequesting) {
      return;
    }
    mPostPraiseRequesting = true;

    U.request("post_praise", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        mPostPraiseRequesting = false;
      }

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
    }, Response.class, event.getPost().id);
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
}
