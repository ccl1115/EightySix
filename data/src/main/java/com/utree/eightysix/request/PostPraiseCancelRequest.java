package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_FEED_PRAISE_CANCEL)
@Token
public class PostPraiseCancelRequest {

  @Param("postId")
  public String postId;

  public PostPraiseCancelRequest(String postId) {
    this.postId = postId;
  }
}
