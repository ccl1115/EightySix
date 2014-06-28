package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Method;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 *
 */
@Api (C.API_FACTORY_MY)
@Token
@Method (Method.METHOD.POST)
public class MyCirclesRequest extends PaginateRequest {

  @Param ("keywords")
  public String keywords;

  @Param ("currPage")
  public int currPage;

  public MyCirclesRequest(String keywords, int currPage) {
    super(currPage);
    this.keywords = keywords;
  }
}
