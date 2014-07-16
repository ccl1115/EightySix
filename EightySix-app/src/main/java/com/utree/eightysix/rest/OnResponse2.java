package com.utree.eightysix.rest;

/**
 * @author simon
 */
public interface OnResponse2<RES> extends OnResponse<RES> {

  void onResponseError(Exception e);
}
