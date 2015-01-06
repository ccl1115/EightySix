package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.InjectView;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import de.akquinet.android.androlog.Log;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Layout (R.layout.activity_image_viewer)
@TopTitle (R.string.image_viewer)
public class ImageViewerActivity extends BaseActivity {

  @InjectView (R.id.content)
  public ImageViewTouch mImageViewTouch;

  private String mHash;
  private boolean mLoadRemote;

  public static void start(Context context, String local, String remote, String secret) {
    Intent intent = new Intent(context, ImageViewerActivity.class);

    intent.putExtra("local", local);
    intent.putExtra("remote", remote);
    intent.putExtra("secret", secret);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (event.getHash().equals(mHash) && (mLoadRemote || event.getWidth() == 600 && event.getHeight() == 600)) {
      mImageViewTouch.setImageBitmap(event.getBitmap());
      hideProgressBar();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final String local = getIntent().getStringExtra("local");
    final String remote = getIntent().getStringExtra("remote");
    final String secret = getIntent().getStringExtra("secret");

    Log.d(C.TAG.CH, "ImageViewer local: " + local);
    Log.d(C.TAG.CH, "ImageViewer remote: " + remote);
    Log.d(C.TAG.CH, "ImageViewer secret: " + secret);


    if (!new File(local).exists()) {
      mLoadRemote = true;
      mHash = ImageUtils.getUrlHash(remote);
      ImageUtils.asyncLoad(remote, mHash);
    } else {
      mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
      File file = new File(local);
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