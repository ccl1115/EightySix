package com.utree.eightysix.utils;

import android.graphics.Color;
import android.widget.Toast;
import com.utree.eightysix.U;
import java.util.Date;

/**
 * @author simon
 */
public class Utils {

  public static int strToColor(String color) {
    try {
      return (int) Long.parseLong(color, 16);
    } catch (NumberFormatException e) {
      return 0x00000000;
    }
  }

  public static int monochromizing(int color) {
    return (color & 0xff) > 0x88 && ((color >> 8) & 0xff) > 0x88 && ((color >> 16) & 0xff) > 0x88
        ? Color.BLACK : Color.WHITE;
  }
}
