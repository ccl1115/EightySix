package com.utree.eightysix.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.jakewharton.disklrucache.DiskLruCache;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.utree.eightysix.U;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class ImageUtils {
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

    public static void asyncLoad(final String url) {
        Bitmap bitmap = sLruCache.get(url);
        if (bitmap == null) {
            try {
                final DiskLruCache.Snapshot snapshot = U.getImageCache().get(url);
                if (snapshot != null) {
                    new ImageDecodeWorker(url, snapshot.getInputStream(0)).execute();
                } else {
                    sClient.get(U.getContext(), url, new BinaryHttpResponseHandler() {
                        @Override
                        public void onSuccess(byte[] binaryData) {
                            new ImageDecodeWorker(url, new ByteArrayInputStream(binaryData)).execute();
                        }
                    });
                }
            } catch (IOException ignored) {

            } catch (OutOfMemoryError e) {
                U.getAnalyser().reportException(U.getContext(), e);
            }
        } else {
            U.getBus().post(new ImageLoadedEvent(url, bitmap));
        }
    }

    public static class ImageLoadedEvent {
        private Bitmap mBitmap;
        private String mKey;

        public ImageLoadedEvent(String key, Bitmap bitmap) {
            mBitmap = bitmap;
            mKey = key;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public String getKey() {
            return mKey;
        }
    }

    private static class ImageDecodeWorker extends AsyncTask<Void, Void, Bitmap> {
        private InputStream mStream;
        private String mKey;
        private boolean mToDiskCache;

        private ImageDecodeWorker(String key, InputStream stream) {
            mKey = key;
            mStream = stream;
        }

        private ImageDecodeWorker(String key, InputStream stream, boolean toDiskCache) {
            mKey = key;
            mStream = stream;
            mToDiskCache = toDiskCache;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mToDiskCache) {
                OutputStream os = null;
                try {
                    os = U.getImageCache().edit(mKey).newOutputStream(0);
                    IOUtils.copyFile(mStream, os);
                } catch (IOException ignored) {
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
            return BitmapFactory.decodeStream(mStream);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try {
                mStream.close();
            } catch (IOException ignored) {
            }
            if (bitmap != null) {
                U.getBus().post(new ImageLoadedEvent(mKey, bitmap));
            }
        }
    }
}
