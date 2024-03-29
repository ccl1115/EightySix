package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.IOUtils;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.widget.TopBar;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

import java.io.File;

/**
 */
@Layout (R.layout.activity_image_viewer)
@TopTitle (R.string.image_viewer)
public class ImageViewerActivity extends BaseActivity {

  @InjectView (R.id.content)
  public ImageViewTouch mImageViewTouch;

  private String mHash;
  private boolean mLoadRemote;

  private Bitmap mBitmap;
  private boolean mLoaded;

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
      mBitmap = event.getBitmap();
      mLoaded = true;
    }
    hideProgressBar();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    final String local = getIntent().getStringExtra("local");
    final String remote = getIntent().getStringExtra("remote");


    if (!new File(local).exists()) {
      mLoadRemote = true;
      mHash = ImageUtils.getUrlHash(remote);
      ImageUtils.asyncLoad(remote, mHash);
      showProgressBar();
    } else {
      mImageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
      File file = new File(local);
      mHash = IOUtils.fileHash(file);
      ImageUtils.asyncLoad(file, mHash, 600, 600);
    }

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return "保存";
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return getResources().getDrawable(R.drawable.apptheme_primary_btn_dark);
      }

      @Override
      public void onClick(View view, int position) {
        if (mLoaded) {
          String url = MediaStore.Images.Media.insertImage(getContentResolver(),
              mBitmap, local.substring(local.lastIndexOf("/") + 1), null);

          if (url != null) {
            showToast(getString(R.string.saved));
          } else {
            showToast(getString(R.string.failed_to_save));
          }
        }
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return null;
      }
    });
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