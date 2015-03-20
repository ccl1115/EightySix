package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Log;
import com.utree.eightysix.rest.Optional;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_FEED_BY_REGION)
@Token
@Cache
@Log
public class FeedByRegionRequest extends Paginate {

  @Param("regionType")
  @Optional
  public int regionType;

  @Param("tabType")
  @Optional
  public int tabType;

  @Param("regionDistance")
  @Optional
  public int regionDistance;

  public FeedByRegionRequest(int currPage) {
    super(currPage);
  }

  public FeedByRegionRequest(int currPage, int regionType, int tabType) {
    super(currPage);
    this.regionType = regionType;
    this.tabType = tabType;
  }

  public FeedByRegionRequest(int currPage, int regionType, int tabType, int regionDistance) {
    super(currPage);
    this.regionType = regionType;
    this.tabType = tabType;
    this.regionDistance = regionDistance;
  }
}
