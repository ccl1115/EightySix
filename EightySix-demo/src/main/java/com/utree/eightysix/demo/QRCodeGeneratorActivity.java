package com.utree.eightysix.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.zxing.*;
import com.google.zxing.client.android.camera.CameraConfigurationUtils;
import com.google.zxing.common.HybridBinarizer;
import com.utree.eightysix.utils.QRCodeGenerator;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class QRCodeGeneratorActivity extends Activity implements Camera.PreviewCallback {

  private static final int MSG_DECODE = 1;
  private static final int MSG_DECODE_FIN = 2;
  private static final int MSG_DECODE_INTERVAL = 3;

  private static final int INTERVAL = 1000;
  private static final String TAG = "QRCode";

  @InjectView(R.id.iv_qrcode)
  ImageView mIvQRCode;

  @InjectView(R.id.sv_camera)
  SurfaceView mSvCamera;

  @InjectView(R.id.et_content)
  EditText mEtContent;

  private Camera mCamera;

  private DecodeHandler mDecodeHandler;

  private boolean mShouldDecode;
  private MultiFormatReader mMultiFormatReader = new MultiFormatReader();

  @OnClick(R.id.btn_generate)
  public void onBtnGenerateClicked() {
    mSvCamera.setVisibility(View.GONE);
    mIvQRCode.setVisibility(View.VISIBLE);

    closeCamera();

    String source = mEtContent.getText().toString();
    if (!TextUtils.isEmpty(source)) {
      Bitmap bitmap = mQRCodeGenerator.generate(source);
      mIvQRCode.setImageBitmap(bitmap);
    }
  }

  @OnClick(R.id.btn_scan)
  public void onBtnScanClicked() {
    mSvCamera.setVisibility(View.VISIBLE);
    mIvQRCode.setVisibility(View.GONE);

    openCamera();
    mDecodeHandler.sendEmptyMessageDelayed(MSG_DECODE_INTERVAL, INTERVAL);
  }

  @InjectView(R.id.btn_scan)
  Button mBtnScan;

  private QRCodeGenerator mQRCodeGenerator;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrcode_generator);

    ButterKnife.inject(this);

    mQRCodeGenerator = new QRCodeGenerator();

    mDecodeHandler = new DecodeHandler();
  }

  private void openCamera() {
    try {
      mCamera = Camera.open();

      setCameraDisplayOrientation(this, 0, mCamera);
      Camera.Parameters parameters = mCamera.getParameters();

      CameraConfigurationUtils.setBarcodeSceneMode(parameters);
      Point point = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, new Point(400, 400));
      parameters.setPreviewSize(point.x, point.y);

      CameraConfigurationUtils.setFocus(parameters, true, false, false);

      mCamera.setParameters(parameters);

      mCamera.setPreviewDisplay(mSvCamera.getHolder());
      mCamera.setPreviewCallback(this);

      mCamera.startPreview();
    } catch (Exception ignored) {
    }
  }

  private void closeCamera() {
    if (mCamera != null) {
      try {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
      } catch (Exception ignored) {
      }
    }
  }

  @Override
  protected void onPause() {
    super.onDestroy();
    closeCamera();
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    if (mShouldDecode) {
      Camera.Size size = camera.getParameters().getPreviewSize();
      Message m = Message.obtain(mDecodeHandler, MSG_DECODE, size.width, size.height, data);
      m.sendToTarget();
    }
  }

  public static void setCameraDisplayOrientation(Activity activity,
                                                 int cameraId, android.hardware.Camera camera) {
    android.hardware.Camera.CameraInfo info =
        new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    int rotation = activity.getWindowManager().getDefaultDisplay()
        .getRotation();
    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break;
      case Surface.ROTATION_90:
        degrees = 90;
        break;
      case Surface.ROTATION_180:
        degrees = 180;
        break;
      case Surface.ROTATION_270:
        degrees = 270;
        break;
    }

    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360;  // compensate the mirror
    } else {  // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
  }


  private void decode(byte[] data, int width, int height) {

    Result result = null;

    PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);

    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

    try {
      result = mMultiFormatReader.decode(binaryBitmap);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    if (result != null) {
      mEtContent.setText(result.getText());
      mDecodeHandler.sendEmptyMessage(MSG_DECODE_FIN);
      Log.d(TAG, "Get QRCode: " + result.getText());
    } else {
      mDecodeHandler.sendEmptyMessageDelayed(MSG_DECODE_INTERVAL, INTERVAL);
      Log.d(TAG, "Get QRCode: null");
    }
    mShouldDecode = false;
  }

  private class DecodeHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_DECODE:
          decode((byte[])msg.obj, msg.arg1, msg.arg2);
          break;
        case MSG_DECODE_INTERVAL:
          mShouldDecode = true;
          break;
        case MSG_DECODE_FIN:
          mShouldDecode = false;
          break;
      }
    }
  }
}