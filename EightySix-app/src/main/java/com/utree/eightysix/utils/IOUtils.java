package com.utree.eightysix.utils;

import android.os.Environment;
import com.aliyun.android.util.MD5Util;
import com.utree.eightysix.U;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 */
public class IOUtils {

  public static void copyFile(InputStream in, OutputStream os) {
    final byte[] buffer = new byte[1024 * 1024];
    int n;

    try {
      while ((n = in.read(buffer)) != -1) {
        os.write(buffer, 0, n);
      }
    } catch (IOException e) {
      U.getAnalyser().reportException(U.getContext(), e);
    }
  }

  public static File createTmpFile(String filename) {
    String p;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      p = U.getContext().getExternalFilesDir(null).getAbsolutePath() + File.separator + "tmp";
    } else {
      p = U.getContext().getFilesDir().getAbsolutePath() + File.separator + "tmp";
    }
    File file = new File(p);
    if (file.isFile()) {
      if (!file.delete()) return null;
    } else if (!file.exists()) {
      if (!file.mkdirs()) return null;
    }
    return new File(file.getAbsolutePath() + File.separator + filename);
  }

  public static String fileHash(File file) {
    return MD5Util.getMD5String(
        (String.format("%s-%d", file.getAbsolutePath(), file.length())).getBytes()).toLowerCase();
  }
}
