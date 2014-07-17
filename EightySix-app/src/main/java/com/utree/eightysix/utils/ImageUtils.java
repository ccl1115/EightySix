package com.utree.eightysix.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.aliyun.android.util.MD5Util;
import com.jakewharton.disklrucache.DiskLruCache;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.storage.Storage;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;

/**
 */
public class ImageUtils {
  private static final String TAG = "ImageUtils";

  private static LruSoftCache<String, Bitmap> sLruCache = new LruSoftCache<String, Bitmap>(
      (((ActivityManager) U.getContext().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() >> 2) * 1024 * 1024) {
    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
      Log.d(TAG, "evicted: " + key);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
      if (value != null) {
        int sizeOf = value.getRowBytes() * value.getHeight();
        Log.d(TAG, String.format("m: %d s: %d a: %d i: %f", maxSize() / 1024, size() / 1024, sizeOf / 1024, sizeOf / (float) maxSize()));
        return sizeOf;
      } else {
        return 0;
      }
    }
  };

  private static AsyncHttpClient sClient = new AsyncHttpClient();

  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      // Calculate ratios of height and width to requested height and width
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);

      // Choose the smallest ratio as inSampleSize value, this will guarantee
      // a final image with both dimensions larger than or equal to the
      // requested height and width.
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
  }

  public static Bitmap decodeBitmap(File file, int width, int height) {
    final String filename = file.getAbsolutePath();
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(filename, options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, width, height);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(filename, options);
  }

  public static Bitmap decodeBitmap(String hash, int width, int height) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    InputStream inputStream = null;
    DiskLruCache.Snapshot snapshot = null;
    try {
      snapshot = U.getImageCache().get(hash);
      if (snapshot != null) {
        inputStream = snapshot.getInputStream(0);
        BitmapFactory.decodeStream(inputStream, null, options);
      } else {
        return null;
      }
    } catch (IOException ignored) {
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (IOException ignored) {
      }

      if (snapshot != null) snapshot.close();
    }

    //Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, width, height);
    Log.d(TAG, "inSampleSize = " + options.inSampleSize);
    //options.inSampleSize = 2;
    options.inJustDecodeBounds = false;

    try {
      snapshot = U.getImageCache().get(hash);
      if (snapshot != null) {
        inputStream = snapshot.getInputStream(0);
      }
      return BitmapFactory.decodeStream(inputStream, null, options);
    } catch (IOException ignored) {
    } finally {

      try {
        if (inputStream != null) inputStream.close();
      } catch (IOException ignored) {
      }

      if (snapshot != null) snapshot.close();

    }
    return null;
  }

  /**
   * Decode bitmap file with catch clause and always decode bitmap into a size that smaller than screen width.
   *
   * @param file the bitmap file
   * @return bitmap or null if error occurred
   */
  @SuppressWarnings ("SuspiciousNameCombination")
  public static Bitmap safeDecodeBitmap(File file) {
    int widthPixels = (int) (U.getContext().getResources().getDisplayMetrics().widthPixels * 0.75);
    try {
      return decodeBitmap(file, widthPixels, widthPixels);
    } catch (OutOfMemoryError e) {
      sLruCache.evictAll();
      try {
        return decodeBitmap(file, widthPixels, widthPixels);
      } catch (OutOfMemoryError ne) {
        return null;
      }
    }
  }

  public static Bitmap safeDecodeBitmap(File file, int width, int height) {
    try {
      return decodeBitmap(file, width, height);
    } catch (OutOfMemoryError e) {
      sLruCache.evictAll();
      try {
        return decodeBitmap(file, width, height);
      } catch (OutOfMemoryError ne) {
        return null;
      }
    }
  }

  /**
   * Decode bitmap file with catch clause and always decode bitmap into a size that smaller than screen width.
   *
   * @param hash the bitmap snapshot
   * @return bitmap or null if error occurred
   */
  @SuppressWarnings ("SuspiciousNameCombination")
  public static Bitmap safeDecodeBitmap(String hash) {
    int widthPixels = (int) (U.getContext().getResources().getDisplayMetrics().widthPixels * 0.75);
    try {
      return decodeBitmap(hash, widthPixels, widthPixels);
    } catch (OutOfMemoryError e) {
      sLruCache.evictAll();
      try {
        return decodeBitmap(hash, widthPixels, widthPixels);
      } catch (OutOfMemoryError ne) {
        return null;
      }
    }
  }

  public static Bitmap safeDecodeBitmap(String hash, int width, int height) {
    try {
      return decodeBitmap(hash, width, height);
    } catch (OutOfMemoryError e) {
      sLruCache.evictAll();
      try {
        return decodeBitmap(hash, width, height);
      } catch (OutOfMemoryError ne) {
        return null;
      }
    }
  }

  public static void asyncLoadThumbnail(final String url, final String hash) {
    asyncLoad(url, hash, U.dp2px(48), U.dp2px(48));
  }

  public static void asyncLoad(final String url, final String hash, final int width, final int height) {
    Bitmap bitmap = sLruCache.get(String.format("%s_%d_%d", url, width, height));
    if (bitmap == null) {
      try {
        final DiskLruCache.Snapshot snapshot = U.getImageCache().get(hash);
        if (snapshot != null) {
          new ImageDiskDecodeWorker(url, hash, snapshot, width, height).execute();
        } else {
          sClient.get(U.getContext(), url, new FileAsyncHttpResponseHandler(IOUtils.createTmpFile(hash)) {
            @Override
            public void onSuccess(File file) {
              Log.d(TAG, "onSuccess");
              new ImageRemoteDecodeWorker(hash, file, width, height).execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
              Log.d(TAG, "onFailure");
              if (error != null) {
                Log.d(TAG, "Get remote error: " + error.getMessage());
                if (BuildConfig.DEBUG) {
                  error.printStackTrace();
                }
              }
            }
          });
        }
      } catch (IOException ignored) {
        Log.e(TAG, "Get snapshot IOException: " + ignored.getMessage());
      } catch (OutOfMemoryError e) {
        U.getAnalyser().reportException(U.getContext(), e);
      }
    } else {
      U.getBus().post(new ImageLoadedEvent(hash, bitmap));
    }
  }

  public static void asyncLoad(final String url, final String hash) {
    Log.d(TAG, "loading: " + url);
    Bitmap bitmap = sLruCache.get(hash);
    if (bitmap == null) {
      try {
        final DiskLruCache.Snapshot snapshot = U.getImageCache().get(hash);
        if (snapshot != null) {
          new ImageDiskDecodeWorker(url, hash, snapshot).execute();
        } else {
          sClient.get(U.getContext(), url, new FileAsyncHttpResponseHandler(IOUtils.createTmpFile(hash)) {
            @Override
            public void onSuccess(File file) {
              Log.d(TAG, "onSuccess");
              new ImageRemoteDecodeWorker(hash, file).execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
              Log.d(TAG, "onFailure");
              if (error != null) {
                Log.d(TAG, "Get remote error: " + error.getMessage());
                if (BuildConfig.DEBUG) {
                  error.printStackTrace();
                }
              }
            }
          });
        }
      } catch (IOException ignored) {
        Log.e(TAG, "Get snapshot IOException: " + ignored.getMessage());
      } catch (OutOfMemoryError e) {
        U.getAnalyser().reportException(U.getContext(), e);
      }
    } else {
      U.getBus().post(new ImageLoadedEvent(hash, bitmap));
    }
  }

  public static Bitmap getFromMem(String hash) {
    return sLruCache.get(hash);
  }

  public static Bitmap getFromMemByUrl(String url) {
    return sLruCache.get(getUrlHash(url));
  }

  public static String getUrlHash(String url) {
    return MD5Util.getMD5String(url.getBytes()).toLowerCase();
  }


  /**
   * Upload a image file asynchronously
   * <p/>
   * <b>Fire {@link com.utree.eightysix.utils.ImageUtils.ImageUploadedEvent} when finished uploading</b>
   *
   * @param file the file
   */
  public static void asyncUpload(File file) {
    new UploadWorker(file).execute();
  }

  /**
   * Cache the image to memory and disk
   *
   * @param file the file to be cache
   */
  public static void cacheImage(String hash, File file) {
    new ImageRemoteDecodeWorker(hash, file).execute();
  }

  private static class UploadWorker extends AsyncTask<Void, Void, Void> {

    private Bitmap mBitmap;
    private File mFile;
    private String mFileHash;
    private String mUrl;

    UploadWorker(File file) {
      mFile = file;
    }

    @Override
    protected Void doInBackground(Void... params) {
      if (mFile != null) {
        mBitmap = safeDecodeBitmap(mFile);
      }

      if (mBitmap == null) {
        return null;
      }

      File file = IOUtils.createTmpFile(String.valueOf(System.currentTimeMillis()));

      FileOutputStream fos = null;

      try {
        fos = new FileOutputStream(file);
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
      } catch (FileNotFoundException ignored) {
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException ignored) {
          }
        }
      }

      final String hash = IOUtils.fileHash(mFile);
      final String path = hash.substring(0, 1) + File.separator + hash.substring(2, 4) + File.separator;
      final String key = hash.substring(5);
      Storage.Result result = U.getCloudStorage().put(U.getConfig("storage.image.bucket.name"), path, key, file);
      if (result.error == 0 && TextUtils.isEmpty(result.msg)) {
        String url = U.getCloudStorage().getUrl(U.getConfig("storage.image.bucket.name"), path, key);
        cacheImage(getUrlHash(url), file);
        mFileHash = hash;
        mUrl = url;
      } else {
        Log.d(TAG, "upload error : " + result.msg);
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      if (mFileHash != null && mUrl != null) {
        U.getBus().post(new ImageUploadedEvent(mFileHash, mUrl));
      }
    }
  }

  /**
   * Fired when a image has been downloaded from net or loaded from disk
   */
  public static class ImageLoadedEvent {
    private Bitmap mBitmap;
    private String mHash;

    public ImageLoadedEvent(String hash, Bitmap bitmap) {
      mBitmap = bitmap;
      mHash = hash;
    }

    public Bitmap getBitmap() {
      return mBitmap;
    }

    public String getHash() {
      return mHash;
    }
  }

  /**
   * Fired when a image file uploaded to the cloud storage
   */
  public static class ImageUploadedEvent {
    private String mHash;
    private String mUrl;

    public ImageUploadedEvent(String hash, String url) {
      mHash = hash;
      mUrl = url;
    }

    public String getHash() {
      return mHash;
    }

    public String getUrl() {
      return mUrl;
    }
  }

  private static class ImageDiskDecodeWorker extends AsyncTask<Void, Void, Bitmap> {
    private DiskLruCache.Snapshot mSnapshot;
    private String mHash;
    private String mUrl;
    private int mWidth = -1;
    private int mHeight = -1;


    private ImageDiskDecodeWorker(String url, String hash, DiskLruCache.Snapshot snapshot) {
      mHash = hash;
      mSnapshot = snapshot;
      mUrl = url;
    }

    private ImageDiskDecodeWorker(String url, String hash, DiskLruCache.Snapshot snapshot, int width, int height) {
      this(url, hash, snapshot);
      mWidth = width;
      mHeight = height;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
      if (mSnapshot != null) {
        // Here we get bitmap from disk cache
        try {
          if (mWidth > 0 && mHeight > 0) {
            return safeDecodeBitmap(mHash, mWidth, mHeight);
          } else {
            return safeDecodeBitmap(mHash);
          }
        } catch (OutOfMemoryError e) {
          U.getAnalyser().reportException(U.getContext(), e);
          return null;
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (mSnapshot != null) {
        mSnapshot.close();
      }

      if (bitmap != null) {
        if (mWidth > 0 && mHeight > 0) {
          sLruCache.put(String.format("%s_%d_%d", mUrl, mWidth, mHeight), bitmap);
        } else {
          sLruCache.put(mHash, bitmap);
        }
        U.getBus().post(new ImageLoadedEvent(mHash, bitmap));
      } else {
        sClient.get(U.getContext(), mUrl, new FileAsyncHttpResponseHandler(IOUtils.createTmpFile(mHash)) {
          @Override
          public void onSuccess(File file) {
            Log.d(TAG, "onSuccess");
            new ImageRemoteDecodeWorker(mHash, file).execute();
          }

          @Override
          public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
            Log.d(TAG, "onFailure");
            if (error != null) {
              Log.d(TAG, "Get remote error: " + error.getMessage());
              if (BuildConfig.DEBUG) {
                error.printStackTrace();
              }
            }
          }
        });
      }

    }
  }


  private static class ImageRemoteDecodeWorker extends AsyncTask<Void, Void, Bitmap> {
    private String mHash;
    private File mFile;
    private int mWidth = -1;
    private int mHeight = -1;

    private ImageRemoteDecodeWorker(String hash, File file) {
      mHash = hash;
      mFile = file;
    }

    private ImageRemoteDecodeWorker(String hash, File file, int width, int height) {
      this(hash, file);
      mWidth = width;
      mHeight = height;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
      if (mFile != null) {

        OutputStream os = null;
        DiskLruCache.Editor edit = null;
        try {
          edit = U.getImageCache().edit(mHash);
          os = edit.newOutputStream(0);
          IOUtils.copyFile(new FileInputStream(mFile), os);
        } catch (IOException ignored) {
          Log.e(TAG, "Put disk cache IOException: " + ignored.getMessage());
        } finally {
          if (os != null) {
            try {
              os.close();
            } catch (IOException ignored) {
            }
          }

          if (edit != null) {
            try {
              edit.commit();
            } catch (IOException ignored) {
            }
          }
        }
        try {
          if (mWidth > 0 && mHeight > 0) {
            return safeDecodeBitmap(mFile, mWidth, mHeight);
          } else {
            return safeDecodeBitmap(mFile);
          }
        } catch (OutOfMemoryError e) {
          U.getAnalyser().reportException(U.getContext(), e);
          return null;
        }
      } else {
        return null;
      }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (bitmap != null) {
        if (mWidth > 0 && mHeight > 0) {
          sLruCache.put(String.format("%s_%d_%d", mHash, mWidth, mHeight), bitmap);
        } else {
          sLruCache.put(mHash, bitmap);
        }
      }

      mFile.delete();

      U.getBus().post(new ImageLoadedEvent(mHash, bitmap));
    }
  }
}
