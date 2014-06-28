package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api (C.API_FACTORY_FIND)
@Token
public class SelectCirclesRequest extends Paginate {


  @Param ("keywords")
  public String keywords;

  public SelectCirclesRequest(String keywords, int currPage) {
    super(currPage);
    this.keywords = keywords;
  }
}
