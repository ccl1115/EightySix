package com.utree.eightysix.app.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;

import java.awt.*;

/**
 * @author simon
 */
@Layout(R.layout.activity_start_chat)
public class StartChatActivity extends BaseActivity {

  @InjectView(R.id.et_username)
  public EditText mEtUsername;

  public static void start(Context context) {
    Intent intent = new Intent(context, StartChatActivity.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick(R.id.btn_start_chat)
  public void onBtnStartChatClicked() {
    ChatActivity.start(this, mEtUsername.getText().toString());
    finish();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }
}