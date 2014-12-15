package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_PRAISE_LIST)
@Token
@Cache
public class PraisesRequest extends Paginate {


  public PraisesRequest(int currPage) {
    super(currPage);
  }
}
