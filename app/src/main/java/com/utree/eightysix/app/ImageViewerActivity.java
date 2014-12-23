package com.utree.eightysix.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import java.io.File;

/**
 */
@Layout(R.layout.activity_image_viewer)
@TopTitle(R.string.image_viewer)
public class ImageViewerActivity extends BaseActivity {

  @InjectView(R.id.content)
  public ImageViewTouch mImageViewTouch;

  private String mHash;

  public static void start(Context context, String path) {
    Intent intent = new Intent(context, ImageViewerActivity.class);

    intent.putExtra("path", path);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (event.getHash().equals(mHash) && event.getWidth() == 600 && event.getHeight() == 600) {
      mImageViewTouch.setImageBitmap(event.getBitmap());
      hideProgressBar();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String path = getIntent().getStringExtra("path");

    if (path == null) {
      return;
    }

    mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

    if (path.startsWith("http")) {
      mHash = ImageUtils.getUrlHash(path);
      ImageUtils.asyncLoad(path, mHash, 600, 600);
    } else if (path.startsWith("/")) {
      File file = new File(path);
      mHash = IOUtils.fileHash(file);
      ImageUtils.asyncLoad(file, mHash, 600, 600);
    }

    showProgressBar();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }
}