package com.utree.eightysix.request;

import com.utree.eightysix.C;

/**
 */
@Api(C.API_LOGIN)
@Method(Method.METHOD.POST)
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
