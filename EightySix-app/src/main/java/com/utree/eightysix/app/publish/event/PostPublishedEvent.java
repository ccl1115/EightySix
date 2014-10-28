package com.utree.eightysix.app.publish.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class PostPublishedEvent {

  public static final int SOURCE_FEED = 1;
  public static final int SOURCE_TOPIC = 2;


  private Post mPost;
  private int mCircleId;
  private int mSource;

  public PostPublishedEvent(Post post, int circleId) {
    mPost = post;
    mCircleId = circleId;
  }

  public PostPublishedEvent(Post post, int circleId, int source) {
    mPost = post;
    mCircleId = circleId;
    mSource = source;
  }

  public int getSource() {
    return mSource;
  }

  public Post getPost() {
    return mPost;
  }

  public int getCircleId() {
    return mCircleId;
  }
}
