package com.utree.eightysix.utils;

/**
 * @author simon
 */
public interface Shortener {

  interface Callback {
    void onShorten(String shorten);
  }

  void shorten(String url, Callback callback);
}
