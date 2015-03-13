package com.utree.eightysix.app.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import butterknife.InjectView;
import butterknife.OnClick;
import com.edmodo.cropper.CropImageView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;

import java.io.*;

/**
 * @author simon
 */
@Layout(R.layout.activity_image_crop)
@TopTitle(R.string.crop_image)
public class ImageCropActivity extends BaseActivity {

  private int mRotateDegree;

  @InjectView(R.id.civ_crop)
  public CropImageView mCivCrop;

  @OnClick(R.id.rb_rotate)
  public void onRbRotateClicked() {
    mRotateDegree += 90;
    mCivCrop.rotateImage(mRotateDegree % 360);
  }

  public static void startForResult(Activity context, int requestCode, Uri uri, boolean fixedRatio) {
    Intent intent = new Intent(context, ImageCropActivity.class);

    intent.putExtra("fixedRatio", fixedRatio);
    intent.setDataAndType(uri, "image/*");

    context.startActivityForResult(intent, requestCode);
  }

  public static void startForResult(Fragment framgnet, int requestCode, Uri uri, boolean fixedRatio) {
    Intent intent = new Intent(framgnet.getActivity(), ImageCropActivity.class);

    intent.putExtra("fixedRatio", fixedRatio);
    intent.setDataAndType(uri, "image/*");

    framgnet.startActivityForResult(intent, requestCode);
  }


  @OnClick(R.id.rb_okay)
  public void onRbOkayClicked() {
    File cropped = IOUtils.createTmpFile("crop_image");

    OutputStream stream = null;
    try {
      stream = new FileOutputStream(cropped);
      mCivCrop.getCroppedImage().compress(Bitmap.CompressFormat.JPEG, 100, stream);
    } catch (FileNotFoundException ignored) {
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException ignored) {
        }
      }
    }
    Intent data = new Intent();
    data.setDataAndType(Uri.fromFile(cropped), "image/jpeg");
    setResult(RESULT_OK, data);
    finish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    boolean fixedRatio = getIntent().getBooleanExtra("fixedRatio", true);

    mCivCrop.setAspectRatio(1, 1);
    mCivCrop.setFixedAspectRatio(fixedRatio);
    mCivCrop.setGuidelines(2);

    Uri uri = getIntent().getData();

    if (uri == null) {
      setResult(RESULT_CANCELED);
      finish();
      return;
    }

    File f = new File(uri.getPath());


    Bitmap bitmap = ImageUtils.safeDecodeBitmap(f);

    if (bitmap == null) {
      setResult(RESULT_CANCELED);
      finish();
      return;
    }

    mCivCrop.setImageBitmap(bitmap);

    try {
      ExifInterface exifInterface = new ExifInterface(f.getPath());
      int orientation = exifInterface
          .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

      if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
        mCivCrop.rotateImage(90);
      } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
        mCivCrop.rotateImage(180);
      } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
        mCivCrop.rotateImage(270);
      }
    } catch (IOException ignored) {
    }
  }

  @Override
  public void onBackPressed() {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}