package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_MSG_LIST)
@Token
@Cache
public class MsgsRequest extends Paginate {


  public MsgsRequest(int currPage) {
    super(currPage);
  }
}
