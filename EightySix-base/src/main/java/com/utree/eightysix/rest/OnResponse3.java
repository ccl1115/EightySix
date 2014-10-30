package com.utree.eightysix.rest;

/**
 */
public interface OnResponse3<RES> extends OnResponse<RES> {

  void onCache(RES res);
}
