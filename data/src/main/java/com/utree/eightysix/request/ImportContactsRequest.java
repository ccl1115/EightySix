package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Host;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Token
@Api (C.API_UPLOAD_CONTACTS)
// #FIXME
@Host("http://192.168.0.118:8000")
public class ImportContactsRequest {

}
