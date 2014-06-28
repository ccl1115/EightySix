package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Method;
import com.utree.eightysix.rest.Param;

/**
 */
@Api(C.API_LOGIN)
public class LoginRequest {

    @Param("phone")
    public String phone;

    @Param("password")
    public String password;

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
