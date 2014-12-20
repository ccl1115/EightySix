/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.rest;

import android.content.Context;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 */
public class RequestSchema {

  private static final String RESPONSE_CLZ_PREFIX = "com.utree.eightysix.response.";

  public HashMap<String, RequestWrapper> mRequestWrapper = new HashMap<String, RequestWrapper>();

  public RequestSchema() {
  }

  public void load(Context context, String host, int xml) {
    InputStream inputStream = context.getResources().openRawResource(xml);
    this.load(host, inputStream);
  }

  public void load(String host, InputStream is) {
    XmlPullParser parser;

    try {
      parser = XmlPullParserFactory.newInstance().newPullParser();
      parser.setInput(new InputStreamReader(is));

      int eventType;

      while ((eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          if ("request".equals(parser.getName())) {
            parseRequest(parser, host);
          }
        }
        parser.next();
      }
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void parseRequest(XmlPullParser parser, String host) throws IOException, XmlPullParserException {
    android.util.Log.d("RequestSchema", "start parsing request");
    int eventType;
    RequestWrapper wrapper = new RequestWrapper();
    RequestData data = new RequestData();
    String id = "";
    while ((eventType = parser.getEventType()) != XmlPullParser.END_TAG || !"request".equals(parser.getName())) {
      if (eventType == XmlPullParser.START_TAG) {
        if ("id".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "id");
          parser.next();
          id = parser.getText();
          parser.next();
        } else if ("path".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "path");
          parser.next();
          data.api = parser.getText();
          parser.next();
        } else if ("sign".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "sign");
          data.sign = true;
        } else if ("token".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "token");
          data.token = true;
        } else if ("cache".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "cache");
          data.cache = true;
        } else if ("method".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "method");
          parser.next();
          if ("post".equals(parser.getText().toLowerCase())) {
            data.method = Method.POST;
          } else if ("get".equals(parser.getText().toLowerCase())) {
            data.method = Method.GET;
          }
        } else if ("host".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "host");
          parser.next();
          data.host = parser.getText();
          parser.next();
        } else if ("log".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "log");
          data.log = true;
        } else if ("params".equals(parser.getName())) {
          android.util.Log.d("RequestSchema", "params");
          List<String> paramKeys = new ArrayList<String>();
          parser.next();
          while ((eventType = parser.getEventType()) != XmlPullParser.END_TAG || !"params".equals(parser.getName())) {
            android.util.Log.d("RequestSchema", "param");
            if (eventType == XmlPullParser.START_TAG && "param".equals(parser.getName())) {
              parser.next();
              paramKeys.add(parser.getText());
              parser.next();
            }
            parser.next();
          }
          wrapper.mParamKeys = paramKeys;
        }
      }

      parser.next();
    }
    if (data.host == null) {
      data.host = host;
    }
    wrapper.mRequestData = data;
    android.util.Log.d("RequestSchema", data.toString());
    mRequestWrapper.put(id, wrapper);
  }

  public void fill(RequestParams params, List<String> keys, Object... objects) {
    if (keys == null) {
      return;
    }
    if (keys.size() != objects.length)
      throw new RuntimeException("error fill request params, key size is not equal data");
    for (int i = 0; i < keys.size(); i++) {
      if (objects[i] == null) continue;
      String key = keys.get(i);
      params.put(key, objects[i]);
    }
  }

  public RequestData getRequest(String id, Object... params) {
    RequestWrapper requestWrapper = mRequestWrapper.get(id);
    if (requestWrapper == null) return null;
    if (requestWrapper.mRequestData.params == null) requestWrapper.mRequestData.params = new RequestParams();
    fill(requestWrapper.mRequestData.params, requestWrapper.mParamKeys, params);
    return requestWrapper.mRequestData;
  }

  private static class RequestWrapper {
    private RequestData mRequestData;

    private List<String> mParamKeys;
  }
}
