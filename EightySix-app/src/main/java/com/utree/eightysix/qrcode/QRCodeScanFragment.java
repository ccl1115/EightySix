package com.utree.eightysix.qrcode;

import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.zxing.*;
import com.google.zxing.client.android.camera.CameraConfigurationUtils;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.encoder.QRCode;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.event.QRCodeScanEvent;
import com.utree.eightysix.drawable.RoundRectDrawable;

/**
 * @author simon
 */
public class QRCodeScanFragment extends Fragment implements Camera.PreviewCallback {

  private static final int MSG_SCAN_START = 1;

  private static final int MSG_SCAN_FIN = 2;

  private static final int INTERVAL = 1000;

  private boolean mShouldDecode;

  private Camera mCamera;

  private String mChannel;

  private class DecodeHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_SCAN_START:
          mShouldDecode = true;
          break;
        case MSG_SCAN_FIN:
          mShouldDecode = false;
          break;
      }
    }
  }

  private DecodeHandler mDecodeHandler = new DecodeHandler();

  @InjectView(R.id.ll_bg)
  public LinearLayout mLlBg;

  @InjectView(R.id.sv_scan)
  public SurfaceView mSvScan;
  private MultiFormatReader mMultiFormatReader = new MultiFormatReader();

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    FragmentManager manager = getFragmentManager();

    if (manager != null) {
      manager.beginTransaction()
          .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
          .remove(this)
          .commit();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_qrcode_scan, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mLlBg.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8), Color.WHITE));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle arguments = getArguments();
    if (arguments != null) {
      mChannel = arguments.getString("channel");
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    mSvScan.postDelayed(new Runnable() {
      @Override
      public void run() {
        mSvScan.setVisibility(View.VISIBLE);
        openCamera();
      }
    }, 1000);

    mDecodeHandler.sendEmptyMessageDelayed(MSG_SCAN_START, 2000);
  }

  @Override
  public void onPause() {
    super.onPause();

    closeCamera();
  }

  private void openCamera() {
    try {
      mCamera = Camera.open(getBackCameraId());

      setCameraDisplayOrientation(0, mCamera);
      Camera.Parameters parameters = mCamera.getParameters();

      CameraConfigurationUtils.setBarcodeSceneMode(parameters);
      Point point = CameraConfigurationUtils.findBestPreviewSizeValue(parameters,
          new Point(U.dp2px(240), U.dp2px(240)));
      parameters.setPreviewSize(point.x, point.y);

      CameraConfigurationUtils.setFocus(parameters, true, false, false);

      mCamera.setParameters(parameters);

      mCamera.setPreviewDisplay(mSvScan.getHolder());
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

  public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
    android.hardware.Camera.CameraInfo info =
        new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    int rotation = getActivity().getWindowManager().getDefaultDisplay()
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

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    if (mShouldDecode) {
      Camera.Size size = camera.getParameters().getPreviewSize();
      decode(data, size.width, size.height);
    }
  }

  private int getBackCameraId() {
    int number = Camera.getNumberOfCameras();

    for (int i = 0; i < number; i++) {
      Camera.CameraInfo info = new Camera.CameraInfo();
      Camera.getCameraInfo(i, info);
      if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
        return i;
      }
    }

    return 0;
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
      mDecodeHandler.sendEmptyMessage(MSG_SCAN_FIN);
      if (mChannel == null) {
        U.getBus().post(new QRCodeScanEvent(result.getText()));
      } else {
        U.getBus(mChannel).post(new QRCodeScanEvent(result.getText()));
      }
    } else {
      mDecodeHandler.sendEmptyMessageDelayed(MSG_SCAN_START, INTERVAL);
    }
    mShouldDecode = false;
  }
}