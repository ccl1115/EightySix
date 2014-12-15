/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest;

import com.utree.eightysix.utils.MD5Util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Overriding toString for generate sign
 */
public class RequestParams extends com.loopj.android.http.RequestParams {

  public RequestParams(String key, String value) {
    super(key, value);
  }

  public RequestParams() {
    super();
  }

  public void sign() {
    StringBuilder result = new StringBuilder();
    for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
      result.append(entry.getKey());
      result.append("=");
      result.append(entry.getValue());
    }

    result.append("lanmei!!!");
    add("sign", MD5Util.getMD5String(result.toString().getBytes()));
  }
}
