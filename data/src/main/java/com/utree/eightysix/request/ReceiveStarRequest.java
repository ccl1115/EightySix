package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_COMMENT_RECEIVE_STAR)
@Token
public class ReceiveStarRequest {
  @Param("starToken")
  public String starToken;

  public ReceiveStarRequest(String starToken) {
    this.starToken = starToken;
  }
}
