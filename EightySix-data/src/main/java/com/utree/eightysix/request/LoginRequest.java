package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Optional;
import com.utree.eightysix.rest.Param;

/**
 */
@Api (C.API_ACCOUNT_LOGIN)
public class LoginRequest {

  @Param ("phone")
  public String phone;

  @Param ("password")
  public String password;


  @Param ("valiCode")
  @Optional
  public String captcha;

  public LoginRequest(String phone, String password) {
    this.phone = phone;
    this.password = password;
  }

  public LoginRequest(String phone, String password, String captcha) {
    this.phone = phone;
    this.password = password;
    this.captcha = captcha;
  }
}
