package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Token
@Api(C.API_FACTORY_ADD)
public class CreateCircleRequest {

  public static final int TYPE_FACTORY = 1;
  public static final int TYPE_BUSINESS = 2;

  @Param("name")
  public String name;

  @Param("factoryType")
  public int circleType;

  @Param("shortName")
  public String shortName;

  @Param("valiCode")
  public String valiCode;

  public CreateCircleRequest(String name, int circleType, String shortName, String valiCode) {
    this.name = name;
    this.circleType = circleType;
    this.shortName = shortName;
    this.valiCode = valiCode;
  }
}
