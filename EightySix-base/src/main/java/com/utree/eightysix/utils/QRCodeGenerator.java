package com.utree.eightysix.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.WeakHashMap;

/**
 * @author simon
 */
public class QRCodeGenerator {

  private WeakHashMap<String, Bitmap> mCachedQRCode = new WeakHashMap<String, Bitmap>();

  public interface OnResult {
    void onResult(Bitmap bitmap);
  }

  public QRCodeGenerator() {
  }

  public Bitmap generate(String source) {
    Bitmap bitmap = null;

    if ((bitmap = mCachedQRCode.get(source)) != null) {
      return bitmap;
    }

    int scale = 8;
    try {
      QRCode code = Encoder.encode(source, ErrorCorrectionLevel.M);
      ByteMatrix matrix = code.getMatrix();
      bitmap = Bitmap.createBitmap(matrix.getWidth() * scale,  matrix.getHeight() * scale,
          Bitmap.Config.ARGB_8888);

      int color = Color.TRANSPARENT;
      for (int i = 0; i < matrix.getHeight(); i++) {
        for (int j = 0; j < matrix.getWidth(); j++) {
          switch (matrix.get(i, j)) {
            case 0:
              color = Color.TRANSPARENT;
              break;
            case 1:
              color = Color.BLACK;
              break;
          }
          for (int k = 0; k < scale; k++) {
            for (int l = 0; l < scale; l++) {
              bitmap.setPixel(i * scale + k, j * scale + l, color);
            }
          }
        }
      }

      mCachedQRCode.put(source, bitmap);
    } catch (WriterException ignored) {
    }

    return bitmap;
  }

  public void generate(String source, OnResult onResult) {
    new GenerateWorker(source, onResult).execute();
  }

  private class GenerateWorker extends AsyncTask<Void, Void, Bitmap> {
    private String mSource;
    private OnResult mOnResult;

    GenerateWorker(String source, OnResult onResult) {
      mSource = source;
      mOnResult = onResult;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
      return generate(mSource);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      mOnResult.onResult(bitmap);
    }
  }
}
