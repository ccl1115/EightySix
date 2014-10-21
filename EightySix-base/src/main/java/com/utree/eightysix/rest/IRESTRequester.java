package com.utree.eightysix.rest;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import org.apache.http.Header;

/**
 * @author simon
 */
public interface IRESTRequester {
  String getHost();

  AsyncHttpClient getClient();

  RequestHandle request(Object request, ResponseHandlerInterface handler);

  RequestHandle request(RequestData data, ResponseHandlerInterface handler);

  RequestData convert(Object request);

  RequestHandle get(String api, org.apache.http.Header[] headers, RequestParams params, ResponseHandlerInterface handler);

  RequestHandle post(String api, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler);

  RequestParams addAuthParams(RequestParams params);

  void setHost(String host);
}
