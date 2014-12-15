package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */

@Api (C.API_FACTORY_SET)
@Token
public class CircleSetRequest {

  @Param ("factoryId")
  public int factoryId;

  public CircleSetRequest(int factoryId) {
    this.factoryId = factoryId;
  }
}
