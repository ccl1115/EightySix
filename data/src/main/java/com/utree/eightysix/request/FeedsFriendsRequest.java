package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;
import com.utree.eightysix.rest.Log;

/**
 * @author simon
 */
@Api (C.API_FEED_FRIENDS_LIST)
@Token
@Cache
@Log
public class FeedsFriendsRequest extends Paginate {

  @Param("factoryId")
  public int circleId;

  public FeedsFriendsRequest(int circleId, int currPage) {
    super(currPage);
    this.circleId = circleId;
  }
}
