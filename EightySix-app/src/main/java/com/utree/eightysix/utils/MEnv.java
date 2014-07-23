package com.utree.eightysix.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Key-value to be store in memory, will be lost when application exit
 * @author simon
 */
public class MEnv {

  public static final int MAX_SIZE = 32;

  private ConcurrentHashMap<String, Object> mMEnv = new ConcurrentHashMap<String, Object>();

  public void put(String key, Object value) {
    if (mMEnv.contains(key)) {
      mMEnv.replace(key, value);
    } else {
      mMEnv.put(key, value);
    }
  }

  public <T> T get(String key) {
    if (mMEnv.contains(key)) {
      return (T) mMEnv.get(key);
    } else {
      return null;
    }
  }
}
