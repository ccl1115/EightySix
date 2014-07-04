package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class FeedPostPraiseEvent {
  private Post mPost;

  public FeedPostPraiseEvent(Post post) {
    mPost = post;
  }

  public Post getPost() {
    return mPost;
  }
}
