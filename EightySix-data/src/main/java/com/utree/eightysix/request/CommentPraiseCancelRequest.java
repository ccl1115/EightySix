package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * 评论取消赞
 *
 * @author simon
 */
@Api (C.API_COMMENT_PRAISE_CANCEL)
@Token
public class CommentPraiseCancelRequest {

  @Param ("postId")
  public String postId;

  @Param ("commentId")
  public String commentId;

  public CommentPraiseCancelRequest(String postId, String commentId) {
    this.postId = postId;
    this.commentId = commentId;
  }
}
