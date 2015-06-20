/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest;

import android.util.Log;
import com.utree.eightysix.utils.MD5Util;

import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;
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
    SortedMap<String, Object> sorted = new TreeMap<String, Object>();
    sorted.putAll(urlParamsWithObjects);
    sorted.putAll(urlParams);
    for (ConcurrentHashMap.Entry<String, Object> entry : sorted.entrySet()) {
      result.append(entry.getKey());
      result.append("=");
      if (entry.getValue() instanceof HashSet) {
        result.append(((HashSet) entry.getValue()).iterator().next().toString());
      } else {
        result.append((String) entry.getValue());
      }
    }

    result.append("lanmei!!!");
    Log.d("[EIG]sign", result.toString());
    String md5String = MD5Util.getMD5String(result.toString().getBytes());
    Log.d("[EIG]sign", md5String);
    add("sign", md5String);
  }
}
