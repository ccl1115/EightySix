package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_COMMENT_DELETE)
@Token
public class PostCommentDeleteRequest {

  @Param("postId")
  public String postId;

  @Param ("commentId")
  public String commentId;

  public PostCommentDeleteRequest(String postId, String commentId) {
    this.postId = postId;
    this.commentId = commentId;
  }
}
