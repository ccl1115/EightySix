package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;

@Api (C.API_ACCOUNT_REG)
public class RegisterRequest {

  @Param ("password")
  public String password;

  @Param ("phone")
  public String phone;

  @Param ("valiCode")
  public String valiCode;

  public RegisterRequest(String phone, String password) {
    this.phone = phone;
    this.password = password;
  }

  public RegisterRequest(String phone, String password, String valiCode) {
    this.phone = phone;
    this.password = password;
    this.valiCode = valiCode;
  }
}
