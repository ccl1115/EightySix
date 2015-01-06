package com.utree.eightysix.rest;

/**
 */
public interface OnResponse3<RES> extends OnResponse2<RES> {

  void onCache(RES res);
}
