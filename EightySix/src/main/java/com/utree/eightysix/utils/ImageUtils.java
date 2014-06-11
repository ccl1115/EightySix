package com.utree.eightysix.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.aliyun.android.util.MD5Util;
import com.jakewharton.disklrucache.DiskLruCache;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.storage.Storage;
import de.akquinet.android.androlog.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.Header;

/**
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    private static LruSoftCache<String, Bitmap> sLruCache = new LruSoftCache<String, Bitmap>(1024 * 1024 * 10) {
        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            oldValue.recycle();
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

    public static Bitmap decodeSquareBitmap(File file, int width, int height) {
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


    public static void asyncLoad(final String url, final String hash) {
        Bitmap bitmap = sLruCache.get(hash);
        if (bitmap == null) {
            try {
                final DiskLruCache.Snapshot snapshot = U.getImageCache().get(hash);
                if (snapshot != null) {
                    new ImageDecodeWorker(hash, snapshot).execute();
                } else {
                    sClient.get(U.getContext(), url, new BinaryHttpResponseHandler(new String[]{".*"}) {
                        @Override
                        public void onSuccess(byte[] binaryData) {
                            Log.d(TAG, "onSuccess");
                            new ImageDecodeWorker(hash, binaryData).execute();
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

    public static void asyncUpload(File file) {
        final String hash = MD5Util.getMD5String(
                String.format("%s-%d", file.getAbsolutePath(), file.length()).getBytes()).toLowerCase();
        final String path = hash.substring(0, 1) + File.separator + hash.substring(2, 4) + File.separator;
        final String key = hash.substring(5);
        U.getCloudStorage().aPut(U.getConfig("storage.image.bucket.name"), path, key, file,
                new Storage.OnResult() {
                    @Override
                    public void onResult(Storage.Result result) {
                        if (result.error == 0 && TextUtils.isEmpty(result.msg)) {
                            U.getBus().post(new ImageUploadedEvent(U.getCloudStorage().getUrl(
                                    U.getConfig("storage.image.bucket.name"), path, key)));
                        } else {
                            Log.d(TAG, "upload error : " + result.msg);
                        }
                    }
                });
    }

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

    public static class ImageUploadedEvent {
        private String mUrl;

        public ImageUploadedEvent(String url) {
            mUrl = url;
        }

        public String getUrl() {
            return mUrl;
        }
    }

    private static class ImageDecodeWorker extends AsyncTask<Void, Void, Bitmap> {
        private byte[] mBytes;
        private DiskLruCache.Snapshot mSnapshot;
        private String mHash;

        private ImageDecodeWorker(String hash, DiskLruCache.Snapshot snapshot) {
            mHash = hash;
            mSnapshot = snapshot;
        }

        private ImageDecodeWorker(String hash, byte[] bytes) {
            mHash = hash;
            mBytes = bytes;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mBytes != null) {
                //Here we get bitmap from net, and cache it to disk
                OutputStream os = null;
                try {
                    os = U.getImageCache().edit(mHash).newOutputStream(0);
                    IOUtils.copyFile(new ByteArrayInputStream(mBytes), os);
                } catch (IOException ignored) {
                    Log.e(TAG, "Put disk cache IOException: " + ignored.getMessage());
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
                try {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(mBytes));
                } catch (OutOfMemoryError e) {
                    U.getAnalyser().reportException(U.getContext(), e);
                    return null;
                }
            } else if (mSnapshot != null) {
                // Here we get bitmap from disk cache
                try {
                    return BitmapFactory.decodeStream(mSnapshot.getInputStream(0));
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
            if (mSnapshot != null) {
                mSnapshot.close();
            }

            if (bitmap != null) {
                sLruCache.put(mHash, bitmap);
            }

            U.getBus().post(new ImageLoadedEvent(mHash, bitmap));
        }
    }
}
