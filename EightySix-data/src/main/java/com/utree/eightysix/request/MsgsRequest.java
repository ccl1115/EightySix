package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Cache;
import com.utree.eightysix.rest.Token;
import com.utree.eightysix.rest.Log;

/**
 * @author simon
 */
@Api(C.API_MSG_LIST)
@Token
@Cache
@Log
public class MsgsRequest extends Paginate {


  public MsgsRequest(int currPage) {
    super(currPage);
  }
}
