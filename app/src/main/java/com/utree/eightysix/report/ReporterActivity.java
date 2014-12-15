package com.utree.eightysix.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
@Layout(R.layout.activity_reporter)
@TopTitle(R.string.app_crash)
public class ReporterActivity extends BaseActivity {

  @InjectView(R.id.rb_report)
  public RoundedButton mRbReport;

  @InjectView(R.id.tv_stacktrace)
  public TextView mTvStacktrace;

  public static void start(Context context, String stacktrace) {
    Intent intent = new Intent(context, ReporterActivity.class);
    intent.putExtra("stacktrace", stacktrace);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String stacktrace = getIntent().getStringExtra("stacktrace");
    mTvStacktrace.setText(stacktrace);
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }
}