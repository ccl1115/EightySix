package com.utree.eightysix.request;

/**
 */
@Request("/login/login.do")
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
