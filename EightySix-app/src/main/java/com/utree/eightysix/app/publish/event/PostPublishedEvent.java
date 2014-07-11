package com.utree.eightysix.app.publish.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class PostPublishedEvent {
  private Post mPost;

  public PostPublishedEvent(Post post) {
    mPost = post;
  }

  public Post getPost() {
    return mPost;
  }
}
