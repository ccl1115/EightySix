package com.utree.eightysix.report;

import com.utree.eightysix.rest.RequestData;

/**
 * @author simon
 */
public interface Reporter {
  void reportRequestError(RequestData requestData, Throwable t);

  void reportRequestStatusCode(RequestData requestData, int statusCode);

  void reportAppCrash(Throwable t);

  void init();
}
