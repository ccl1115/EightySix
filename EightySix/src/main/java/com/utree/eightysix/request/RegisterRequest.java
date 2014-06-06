package com.utree.eightysix.request;

import com.utree.eightysix.C;

@Api(C.API_REG)
@Method(Method.METHOD.POST)
public class RegisterRequest {

    @Param("password")
    public String password;

    @Param("phone")
    public String phone;

    public RegisterRequest(String password, String phone) {
        this.password = password;
        this.phone = phone;
    }
}
