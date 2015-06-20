package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.*;

/**
 * @author simon
 */
@Api(C.API_MSG_LIST)
@Token
@Cache
@Log
public class MsgsRequest extends Paginate {

  @Param("createType")
  public int createType;

  public MsgsRequest(int createType, int currPage) {
    super(currPage);
    this.createType = createType;
  }
}
