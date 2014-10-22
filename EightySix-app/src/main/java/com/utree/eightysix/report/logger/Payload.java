package com.utree.eightysix.report.logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 */
public class Payload {

  private JsonObject mJsonObject;

  public Payload() {
    mJsonObject = new JsonObject();
  }

  public String normalize() {
    return mJsonObject.toString();
  }

  public void add(String key, Number number) {
    mJsonObject.add(key, new JsonPrimitive(number));
  }

  public void add(String key, String str) {
    mJsonObject.add(key, new JsonPrimitive(str));
  }

  public void add(String key, Boolean b) {
    mJsonObject.add(key, new JsonPrimitive(b));
  }

  public void remove(String key) {
    mJsonObject.remove(key);
  }
}
