package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;

/**
 * @author simon
 */

@Api(C.API_FEED_OPTION_BACK)
public class OptionBackRequest {

  @Param("factoryId")
  public int circleId;


  public OptionBackRequest(int circleId) {
    this.circleId = circleId;
  }
}
