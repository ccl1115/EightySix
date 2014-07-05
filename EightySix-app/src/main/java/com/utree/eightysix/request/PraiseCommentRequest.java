package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_COMMENT_PRAISE)
@Token
public class PraiseCommentRequest {

  @Param ("postId")
  public String postId;

  @Param ("commentId")
  public String commentId;

  public PraiseCommentRequest(String postId, String commentId) {
    this.postId = postId;
    this.commentId = commentId;
  }
}
