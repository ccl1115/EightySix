package com.utree.eightysix.app.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

/**
 * @author simon
 */
@Layout (R.layout.activity_invite)
@TopTitle (R.string.invite_people_to_unlock)
public class InviteActivity extends BaseActivity {

  @InjectView (R.id.tv_invite_info)
  public TextView mTvInviteInfo;

  @InjectView (R.id.tv_friend_count)
  public TextView mTvFriendCount;

  public static void start(Context context, String circle) {
    Intent intent = new Intent(context, InviteActivity.class);
    intent.putExtra("circle", circle);
    context.startActivity(intent);
  }

  @OnClick (R.id.rb_invite)
  public void onRbInviteClicked() {
    startActivity(new Intent(this, ContactsActivity.class));
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String circle = getIntent().getStringExtra("circle");
    if (circle == null) circle = null;

    mTvInviteInfo.setText(U.gfs(R.string.invite_info, circle));
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }
}
