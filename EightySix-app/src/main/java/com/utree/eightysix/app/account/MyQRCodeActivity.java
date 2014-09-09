package com.utree.eightysix.app.account;

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

    new QRCodeGenerator().generate("http://baidu.com", new QRCodeGenerator.OnResult() {
      @Override
      public void onResult(Bitmap bitmap) {
        mIvQRCode.setImageBitmap(bitmap);
      }
    });
  }
}
