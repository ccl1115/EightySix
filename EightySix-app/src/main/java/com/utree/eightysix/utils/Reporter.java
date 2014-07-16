package com.utree.eightysix.utils;

import com.utree.eightysix.rest.RequestData;

/**
 * @author simon
 */
public interface Reporter {
  void reportRequestError(RequestData requestData, Throwable t);

  void reportRequestStatusCode(RequestData requestData, int statusCode);

  void reportAppCrash(Throwable t);
}
