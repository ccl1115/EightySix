package com.utree.eightysix.rest;

/**
 */
public enum CachePolicy {

  /**
   * Load cache first then request from remote.
   */
  CACHE_FIRST,

  /**
   * Never load cache data even if request failed.
   */
  REMOTE_ONLY,

  /**
   * Request first, load cache if it failed.
   */
  CACHE_IF_FAIL,
}
