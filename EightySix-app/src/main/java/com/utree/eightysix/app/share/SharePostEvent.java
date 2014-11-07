package com.utree.eightysix.app.share;

import com.utree.eightysix.data.Post;

/**
 */
public class SharePostEvent {
  private final Post mPost;
  private final boolean mSuccess;

  public SharePostEvent(Post post, boolean suc) {
    mPost = post;
    mSuccess = suc;
  }

  public Post getPost() {
    return mPost;
  }

  public boolean isSuccess() {
    return mSuccess;
  }
}
