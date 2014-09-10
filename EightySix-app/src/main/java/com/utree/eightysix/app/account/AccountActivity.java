package com.utree.eightysix.app.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.utils.QRCodeGenerator;
import com.utree.eightysix.widget.TopBar;

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

    getTopBar().setActionAdapter(new TopBar.ActionAdapter() {
      @Override
      public String getTitle(int position) {
        return "添加";
      }

      @Override
      public Drawable getIcon(int position) {
        return null;
      }

      @Override
      public Drawable getBackgroundDrawable(int position) {
        return new RoundRectDrawable(dp2px(4), getResources().getColorStateList(R.color.apptheme_primary_btn_light));
      }

      @Override
      public void onClick(View view, int position) {
        startActivity(new Intent(AccountActivity.this, AddFriendActivity.class));
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
}
