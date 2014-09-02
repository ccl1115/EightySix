package com.utree.eightysix.app.publish.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class PostPublishedEvent {
  private Post mPost;
  private int mCircleId;

  public PostPublishedEvent(Post post, int circleId) {
    mPost = post;
    mCircleId = circleId;
  }

  public Post getPost() {
    return mPost;
  }

  public int getCircleId() {
    return mCircleId;
  }
}
