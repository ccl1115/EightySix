package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */

@Api(C.API_FEED_OPTION_BACK)
@Token
public class OptionBackRequest {

  @Param("factoryId")
  public int circleId;

  @Param("content")
  public String content = "_";


  public OptionBackRequest(int circleId) {
    this.circleId = circleId;
  }
}
