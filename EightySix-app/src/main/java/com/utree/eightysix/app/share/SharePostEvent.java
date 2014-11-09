package com.utree.eightysix.app.share;

import com.utree.eightysix.data.Post;

/**
 */
public class SharePostEvent {
  private final Post mPost;
  private final boolean mSuccess;

  public boolean isFromBs() {
    return mFromBs;
  }

  private final boolean mFromBs;

  public SharePostEvent(Post post, boolean suc, boolean fromBs) {
    mPost = post;
    mSuccess = suc;
    mFromBs = fromBs;
  }

  public Post getPost() {
    return mPost;
  }

  public boolean isSuccess() {
    return mSuccess;
  }
}
