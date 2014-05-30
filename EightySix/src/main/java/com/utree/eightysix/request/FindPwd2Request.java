package com.utree.eightysix.request;


import com.utree.eightysix.C;

/**
 */
@Api(C.API_FIND_PWD_2)
public class FindPwd2Request {

    @Param("validateCode")
    public String validateCode;
}
