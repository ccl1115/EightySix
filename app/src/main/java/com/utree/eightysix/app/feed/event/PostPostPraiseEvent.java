package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class PostPostPraiseEvent {
  private Post mPost;
  private boolean mCancel;

  public PostPostPraiseEvent(Post post, boolean cancel) {
    mPost = post;
    mCancel = cancel;
  }

  public Post getPost() {
    return mPost;
  }

  public boolean isCancel() {
    return mCancel;
  }
}
