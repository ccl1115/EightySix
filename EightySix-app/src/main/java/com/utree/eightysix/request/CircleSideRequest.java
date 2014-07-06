package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_FACTORY_SIDE)
@Token
public class CircleSideRequest extends Paginate {

  @Param ("keywords")
  public String keywords;

  public CircleSideRequest(String keywords, int currPage) {
    super(currPage);
    this.keywords = keywords;
  }
}
