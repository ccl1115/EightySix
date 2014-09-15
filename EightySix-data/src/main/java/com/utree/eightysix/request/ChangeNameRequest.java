package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_FEED_CHANGE_NAME)
@Token
public class ChangeNameRequest {

  @Param("factoryId")
  public int circleId;

  @Param("content")
  public String content = "_";

  public ChangeNameRequest(int circleId) {
    this.circleId = circleId;
  }
}
