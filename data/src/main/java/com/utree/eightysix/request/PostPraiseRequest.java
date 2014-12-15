package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_FEED_PRAISE)
@Token
public class PostPraiseRequest {

  @Param ("postId")
  public String postId;

  public PostPraiseRequest(String postId) {
    this.postId = postId;
  }
}
