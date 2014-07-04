package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class FeedPostCancelPraiseEvent {
  private Post mPost;

  public FeedPostCancelPraiseEvent(Post post) {
    mPost = post;
  }

  public Post getPost() {
    return mPost;
  }
}
