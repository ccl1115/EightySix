package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class FeedPostPraiseEvent {
  private Post mPost;

  private boolean mCancel;

  public FeedPostPraiseEvent(Post post, boolean cancel) {
    mPost = post;
    mCancel = cancel;
  }

  public boolean isCancel() {
    return mCancel;
  }

  public Post getPost() {
    return mPost;
  }
}
