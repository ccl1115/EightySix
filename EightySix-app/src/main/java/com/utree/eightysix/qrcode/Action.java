package com.utree.eightysix.qrcode;

import android.net.Uri;

/**
 * @author simon
 */
public interface Action {

  /**
   *
   * @return true to consume this uri, false to pass to next.
   */
  boolean accept(Uri uri);

  /**
   *
   * @param uri the uri to deal with
   */
  void act(Uri uri);
}
