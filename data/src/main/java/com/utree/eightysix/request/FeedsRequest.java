package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Log;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_FEED_LIST)
@Token
@Cache
@Log
public class FeedsRequest extends Paginate {

  @Param("factoryId")
  public int circleId;

  public FeedsRequest(int circleId, int currPage) {
    super(currPage);
    this.circleId = circleId;
  }
}
