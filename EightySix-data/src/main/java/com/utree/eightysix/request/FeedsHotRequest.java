package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api (C.API_FEED_HOT_LIST)
@Token
@Cache
public class FeedsHotRequest extends Paginate {

  @Param("factoryId")
  public int circleId;

  public FeedsHotRequest(int circleId, int currPage) {
    super(currPage);
    this.circleId = circleId;
  }
}
