package com.utree.eightysix.request;

import com.utree.eightysix.C;

/**
 */
@API(C.API_FIND_PWD_3)
public class FindPwd3Request {

    @Param("new_password")
    public String newPassword;
}
