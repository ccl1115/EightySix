package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.qrcode.QRCodeScanEvent;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.utils.QRCodeGenerator;
import com.utree.eightysix.widget.TopBar;

/**
 * @author simon
 */
@TopTitle(R.string.my_qr_code)
@Layout(R.layout.activity_my_qr_code)
public class MyQRCodeActivity extends BaseActivity {

  @InjectView(R.id.iv_qr_code)
  public ImageView mIvQRCode;

  @InjectView(R.id.parent)
  public LinearLayout mParent;
  private QRCodeScanFragment mQRCodeScanFragment;

  public static void start(Context context, String id) {
    Intent intent = new Intent(context, MyQRCodeActivity.class);

    intent.putExtra("id", id);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    final String id = getIntent().getStringExtra("id");

    if (id == null) {
      finish();
      return;
    }

    mParent.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8), Color.WHITE));

    new QRCodeGenerator().generate("eightysix://friend/add/" + id, new QRCodeGenerator.OnResult() {
      @Override
      public void onResult(Bitmap bitmap) {
        mIvQRCode.setImageBitmap(bitmap);
      }
    });

    mTopBar.setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position){
        return "扫码";
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
        if (position == 0) {
          if (mQRCodeScanFragment == null) {
            mQRCodeScanFragment = new QRCodeScanFragment();
            getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, mQRCodeScanFragment)
                .commit();
          } else if (mQRCodeScanFragment.isDetached()) {
            getSupportFragmentManager().beginTransaction()
                .attach(mQRCodeScanFragment)
                .commit();
          }
        }
      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public TopBar.LayoutParams getLayoutParams(int position) {
        return new TopBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (mQRCodeScanFragment != null) {
      if (!mQRCodeScanFragment.isDetached()) {
        getSupportFragmentManager().beginTransaction().detach(mQRCodeScanFragment).commit();
        return;
      }
    }
    super.onBackPressed();
  }

  @Subscribe
  public void onQRCodeScanEvent(QRCodeScanEvent event) {
    if (mQRCodeScanFragment != null) {
      if (mQRCodeScanFragment.isAdded()) {
        getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom)
            .detach(mQRCodeScanFragment)
            .commit();
      }
    }

    if (BuildConfig.DEBUG) {
      showToast("scanned: " + event.getText());
    }

    if (U.getQRCodeActionDispatcher().dispatch(event.getText())) {
      startActivity(new Intent(this, ScanFriendsActivity.class));
    }
  }
}
