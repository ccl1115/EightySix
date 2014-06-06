package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;

/**
 */
@Api(C.API_FIND_PWD_3)
public class FindPwd3Request {

    @Param("new_password")
    public String newPassword;

    public FindPwd3Request(String newPassword) {
        this.newPassword = newPassword;
    }
}
