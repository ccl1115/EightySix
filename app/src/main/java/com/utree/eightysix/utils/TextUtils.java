/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import android.graphics.Paint;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TextUtils {

  public static Paint sPaint;

  public static String[] page(String content, int width, int textSize) {
    if (sPaint == null) {
      sPaint = new Paint();
    }

    sPaint.setTextSize(U.dp2px(textSize));
    int index;
    int line = 0;
    int start = 0;
    List<Integer> pageIndex = new ArrayList<Integer>();
    while ((index = sPaint.breakText(content, start, content.length(), true, width, null)) < (content.length() - start)) {
      Log.d("TextUtils", "index: " + index);
      line ++;

      if (line >= 7) {
        pageIndex.add(index + start);
        line = 0;
      }
      start += index;
    }

    if (pageIndex.size() == 0) {
      pageIndex.add(content.length());
    }

    String[] ret = new String[pageIndex.size()];

    int j = 0;
    int pre = 0;
    for (int i : pageIndex) {
      ret[j] = content.substring(pre, i);
      pre = i;
      j++;
    }

    return ret;
  }
}
