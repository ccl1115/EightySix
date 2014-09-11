package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.QRCodeGenerator;

/**
 * @author simon
 */
@TopTitle(R.string.my_qr_code)
@Layout(R.layout.activity_my_qr_code)
public class MyQRCodeActivity extends BaseActivity {

  @InjectView(R.id.iv_qr_code)
  public ImageView mIvQRCode;

  @InjectView(R.id.iv_bg)
  public ImageView mIvBg;

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

    final String id = getIntent().getStringExtra("id");

    if (id == null) {
      finish();
      return;
    }

    new QRCodeGenerator().generate("eightysix://friend/add/" + id, new QRCodeGenerator.OnResult() {
      @Override
      public void onResult(Bitmap bitmap) {
        mIvQRCode.setImageBitmap(bitmap);
      }
    });
  }
}
