package com.utree.eightysix.app.publish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import butterknife.InjectView;
import butterknife.OnClick;
import com.edmodo.cropper.CropImageView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.RoundedButton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author simon
 */
@Layout(R.layout.activity_image_crop)
@TopTitle(R.string.crop_image)
public class ImageCropActivity extends BaseActivity {


  @InjectView(R.id.civ_crop)
  public CropImageView mCivCrop;

  @OnClick (R.id.rb_okay)
  public void onRbOkayClicked() {
    File cropped = IOUtils.createTmpFile("crop_image");

    OutputStream stream = null;
    try {
      stream = new FileOutputStream(cropped);
      mCivCrop.getCroppedImage().compress(Bitmap.CompressFormat.JPEG, 85, stream);
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

    mCivCrop.setAspectRatio(1, 1);
    mCivCrop.setFixedAspectRatio(true);
    mCivCrop.setGuidelines(2);

    Uri uri = getIntent().getData();

    File f = new File(uri.getPath());

    Bitmap bitmap = ImageUtils.safeDecodeBitmap(f);

    mCivCrop.setImageBitmap(bitmap);

  }

  @Override
  public void onBackPressed() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}