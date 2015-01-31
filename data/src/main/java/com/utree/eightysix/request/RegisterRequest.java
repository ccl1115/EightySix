package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Optional;
import com.utree.eightysix.rest.Param;

@Api (C.API_ACCOUNT_REG)
public class RegisterRequest {

  @Param ("password")
  public String password;

  @Param ("phone")
  public String phone;

  @Param ("valiCode")
  public String valiCode;

  @Param("inviteCode")
  @Optional
  public String inviteCode;

  public RegisterRequest(String phone, String password, String valiCode) {
    this.phone = phone;
    this.password = password;
    this.valiCode = valiCode;
  }

  public RegisterRequest(String password, String phone, String valiCode, String inviteCode) {
    this.password = password;
    this.phone = phone;
    this.valiCode = valiCode;
    this.inviteCode = inviteCode;
  }
}
