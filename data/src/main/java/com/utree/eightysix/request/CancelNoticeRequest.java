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

  public static final int TYPE_DELETE = 2;

  @Param("postId")
  public String postId;

  @Param("type")
  public int type;

  public CancelNoticeRequest(String postId) {
    this.postId = postId;
  }

  public CancelNoticeRequest(String postId, int type) {
    this(postId);
    this.type = type;
  }
}
