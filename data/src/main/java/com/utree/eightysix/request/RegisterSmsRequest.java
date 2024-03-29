package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;

/**
 * @author simon
 */
@Api(C.API_ACCOUNT_REG_SMS)
public class RegisterSmsRequest {

  @Param("phone")
  public String phone;

  public RegisterSmsRequest(String phone) {
    this.phone = phone;
  }
}
