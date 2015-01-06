/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.hometown;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;

/**
 */
public class HotHometownFeedsFragment extends AbsHometownFeedsFragment {

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
}
