package com.utree.eightysix.utils;

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
}
