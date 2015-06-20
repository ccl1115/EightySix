/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.qrcode.actions;

import android.net.Uri;
import com.utree.eightysix.U;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.qrcode.Action;

/**
 * Pattrn:  [http|https]://
 */
public class UrlAction implements Action {
  @Override
  public boolean accept(Uri uri) {
    return "http".equals(uri.getScheme()) || "https".equals(uri.getScheme());
  }

  @Override
  public void act(Uri uri) {
    BaseWebActivity.start(U.getContext(), uri.toString());
  }
}
