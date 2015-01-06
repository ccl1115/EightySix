package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 *
 */
@Api (C.API_FACTORY_MY)
@Token
@Cache
public class MyCirclesRequest extends Paginate {

  @Param ("keywords")
  public String keywords;

  public MyCirclesRequest(String keywords, int currPage) {
    super(currPage);
    this.keywords = keywords;
  }
}
