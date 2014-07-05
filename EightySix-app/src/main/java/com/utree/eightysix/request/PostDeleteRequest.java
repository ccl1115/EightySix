package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_FEED_DELETE)
@Token
public class PostDeleteRequest {

  @Param ("postId")
  public String postId;

  public PostDeleteRequest(String postId) {
    this.postId = postId;
  }
}
