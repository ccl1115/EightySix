package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.QRCodeGenerator;

/**
 * @author simon
 */
@TopTitle(R.string.my_friends)
@Layout(R.layout.activity_account)
public class AccountActivity extends BaseActivity {

  @InjectView(R.id.iv_qr_code)
  public ImageView mIvQRCode;

  @OnClick(R.id.rl_id)
  public void onRlIdClicked() {
    startActivity(new Intent(this, MyQRCodeActivity.class));
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

    new QRCodeGenerator().generate("http://baidu.com", new QRCodeGenerator.OnResult() {
      @Override
      public void onResult(Bitmap bitmap) {
        mIvQRCode.setImageBitmap(bitmap);
      }
    });
  }
}
