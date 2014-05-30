package com.utree.eightysix.request;

/**
 */

import com.utree.eightysix.C;

@Api(C.API_FIND_PWD_1)
public class FindPwd1Request {

    @Param("phone")
    public String phone;

    public FindPwd1Request(String phone) {
        this.phone = phone;
    }
}
