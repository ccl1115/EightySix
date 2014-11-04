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

  void setHost(String host);

  AsyncHttpClient getClient();

  RequestHandle request(Object request, ResponseHandlerInterface handler);

  RequestHandle request(RequestData data, ResponseHandlerInterface handler);

  <T extends Response> RequestHandle request(Object request, OnResponse<T> onResponse, Class<T> clz);

  RequestData convert(Object request);

  RequestHandle get(String api, org.apache.http.Header[] headers, RequestParams params, ResponseHandlerInterface handler);

  RequestHandle post(String api, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler);

  RequestParams addAuthParams(RequestParams params);
}
