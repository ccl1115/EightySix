package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Post;

/**
 * @author simon
 */
public class PostDeleteEvent {
  private Post mPost;

  public PostDeleteEvent(Post post) {
    mPost = post;
  }

  public Post getPost() {
    return mPost;
  }
}
