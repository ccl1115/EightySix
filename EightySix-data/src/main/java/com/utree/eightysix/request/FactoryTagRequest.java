package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api(C.API_FEED_TAG_FACTORY)
@Token
@Cache
public class FactoryTagRequest extends Paginate {
  @Param("tagId")
  public int id;

  public FactoryTagRequest(int currPage, int id) {
    super(currPage);
    this.id = id;
  }
}
