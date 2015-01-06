package com.utree.eightysix.app.feed.event;

import com.utree.eightysix.data.Comment;

/**
 * @author simon
 */
public class PostCommentDeleteEvent {


  private String mPostId;
  private Comment mComment;

  public PostCommentDeleteEvent(String postId, Comment comment) {
    mPostId = postId;
    mComment = comment;
  }

  public String getPostId() {
    return mPostId;
  }

  public Comment getComment() {
    return mComment;
  }
}
