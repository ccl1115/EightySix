package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_CANCEL_NOTICE)
@Token
public class CancelNoticeRequest {

  @Param("postId")
  public String postId;

  public CancelNoticeRequest(String postId) {
    this.postId = postId;
  }
}
