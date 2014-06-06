package com.utree.eightysix.request;


import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;

/**
 */
@Api(C.API_FIND_PWD_2)
public class FindPwd2Request {

    @Param("validateCode")
    public String validateCode;

    public FindPwd2Request(String validateCode) {
        this.validateCode = validateCode;
    }
}
