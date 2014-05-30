package com.utree.eightysix.request;

/**
 */

import com.utree.eightysix.C;

@API(C.API_FIND_PWD_1)
public class FindPwd1Request {

    @Param("phone")
    public String phone;
}
