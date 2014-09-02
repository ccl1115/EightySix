package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Comment;

/**
 * @author simon
 */
public class PostCommentPraiseEvent {
  private Comment mComment;
  private boolean mCancel;

  public PostCommentPraiseEvent(Comment comment, boolean cancel) {
    mComment = comment;
    mCancel = cancel;
  }

  public Comment getComment() {
    return mComment;
  }

  public boolean isCancel() {
    return mCancel;
  }
}
