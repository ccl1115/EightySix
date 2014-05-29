package com.utree.eightysix.request;

/**
 */
@Request("/login/register.do")
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
