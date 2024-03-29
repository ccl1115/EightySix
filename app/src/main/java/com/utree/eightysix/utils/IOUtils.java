package com.utree.eightysix.utils;

import android.os.AsyncTask;
import android.os.Environment;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class IOUtils {

  /**
   * copy from input stream to output stream
   *
   * <b>note: this method doesn't close any of these streams, you should close them manually</b>
   * <b>note: when the copy is failed by any exception this method never through it.</b>
   * @param in the file copy from
   * @param os the file copy to
   */
  public static void copyFile(InputStream in, OutputStream os) {
    final byte[] buffer;
    try {
      buffer = new byte[1024 * 128];
    } catch (OutOfMemoryError error) {
      return;
    }
    int n;

    try {
      while ((n = in.read(buffer)) != -1) {
        os.write(buffer, 0, n);
      }
    } catch (IOException ignored) {
    } finally {
      try {
        os.flush();
      } catch (IOException ignored) {
      }
    }
  }

  public static File createTmpFile(String filename) {
    File file = new File(getAvailableAppDir().getAbsolutePath() + File.separator + "tmp");
    if (getOrCreateDir(file)) {
      return new File(file.getAbsolutePath() + File.separator + filename);
    } else {
      return null;
    }
  }

  public static String fileHash(File file) {
    return MD5Util.getMD5(file);
  }

  public static void asyncFileHash(final File file, final ParamsRunnable runnable) {
    (new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... voids) {
        return fileHash(file);
      }

      @Override
      protected void onPostExecute(String s) {
        runnable.run(s);
      }
    }).execute();
  }

  public static File getAvailableAppDir() {
    String path;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      try {
        path = U.getContext().getExternalFilesDir(null).getAbsolutePath() + File.separator;
        File file = new File(path);
        if (file.canRead() && file.canWrite() && getOrCreateDir(file)) {
          return file;
        } else throw new RuntimeException("Directory not available");
      } catch (Exception e) {
        path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "Android" + File.separator + "data" + File.separator + U.getContext().getPackageName() + File.separator;

        File file = new File(path);
        if (file.canRead() && file.canWrite() && getOrCreateDir(file)) {
          return file;
        }
      }
    }

    path = U.getContext().getFilesDir().getAbsolutePath() + File.separator;
    File file = new File(path);
    if (file.canRead() && file.canWrite() && getOrCreateDir(file)) {
      return file;
    }

    return null;
  }

  private static boolean getOrCreateDir(File file) {
    Log.d("CacheUtils", "cache path = " + file.getAbsolutePath());
    if (file.exists()) {
      if (file.isFile()) {
        if (file.delete()) {
          if (file.mkdirs()) {
            return true;
          } else if (file.mkdir()) {
            return true;
          }
        }
      } else if (file.isDirectory()) {
        return true;
      }
    } else {
      if (file.mkdirs()) {
        return true;
      } else if (file.mkdir()) {
        return true;
      }
    }
    return false;
  }

}
