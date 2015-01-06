package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_FETCH)
@Token
public class FetchNotificationRequest {

  @Param("factoryId")
  public int circleId;

  public FetchNotificationRequest(int circleId) {
    this.circleId = circleId;
  }
}
