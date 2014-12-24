package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import java.io.File;

/**
 */
@Layout (R.layout.activity_image_viewer)
@TopTitle (R.string.image_viewer)
public class ImageViewerActivity extends BaseActivity {

  @InjectView(R.id.content)
  public ImageViewTouch mImageViewTouch;

  private String mHash;

  public static void start(Context context, String local, String remote) {
    Intent intent = new Intent(context, ImageViewerActivity.class);

    intent.putExtra("local", local);

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

    String local = getIntent().getStringExtra("local");
    String remote = getIntent().getStringExtra("remote");

    if (local == null || remote == null) {
      return;
    }

    mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

    File file = new File(local);
    if (file.exists()) {
      mHash = IOUtils.fileHash(file);
      ImageUtils.asyncLoad(file, mHash, 600, 600);
    } else {
      mHash = ImageUtils.getUrlHash(remote);
      ImageUtils.asyncLoad(remote, mHash, 600, 600);
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