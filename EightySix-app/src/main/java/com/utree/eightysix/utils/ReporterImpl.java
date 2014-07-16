package com.utree.eightysix.utils;

import com.utree.eightysix.U;
import com.utree.eightysix.rest.RequestData;
import java.util.Properties;

/**
 * @author simon
 */
public class ReporterImpl implements Reporter {
  @Override
  public void reportRequestError(RequestData requestData, Throwable t) {
    Properties properties = fromRequestData(requestData);
    properties.setProperty("throwable", t.getMessage());
    U.getAnalyser().trackKVEvent(U.getContext(), "server_response_error", properties);
  }

  @Override
  public void reportRequestStatusCode(RequestData requestData, int statusCode) {
    Properties properties = fromRequestData(requestData);
    properties.setProperty("statusCode", String.valueOf(statusCode));
    U.getAnalyser().trackKVEvent(U.getContext(), "server_response_status", properties);
  }

  @Override
  public void reportAppCrash(Throwable t) {

  }

  private Properties fromRequestData(RequestData data) {
    Properties properties = new Properties();
    properties.setProperty("api", data.getApi());
    properties.setProperty("params", data.getParams().toString());
    return properties;
  }
}
