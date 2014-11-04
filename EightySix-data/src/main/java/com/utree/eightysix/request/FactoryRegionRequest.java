package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_FACTORY_REGION)
@Token
@Cache
public class FactoryRegionRequest extends Paginate {

  @Param("regionType")
  public int regionType;

  public FactoryRegionRequest(int regionType, int currPage) {
    super(currPage);
    this.regionType = regionType;
  }
}
