package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_COMMENT_ADD)
@Token
public class PublishCommentRequest {

  @Param ("content")
  public String content;

  @Param ("factoryId")
  public int factoryId;

  @Param ("postId")
  public String postId;

  public PublishCommentRequest(String content, int factoryId, String postId) {
    this.content = content;
    this.factoryId = factoryId;
    this.postId = postId;
  }
}
