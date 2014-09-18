package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.OnClick;
import com.baidu.android.common.util.CommonParam;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Circle;

/**
 * @author simon
 */
@Layout(R.layout.activity_reward_accepted)
public class RewardAcceptedActivity extends BaseActivity{

  public static void start(Context context, Circle circle) {
    Intent intent = new Intent(context, RewardAcceptedActivity.class);
    intent.putExtra("circle", circle);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnClick(R.id.rb_accept)
  public void onRbAcceptClicked() {
    BaseWebActivity.start(this, "活动详情",
        String.format("%s%s?userid=%s&factoryid=%d&virtualImei=%s", U.getConfig("api.host"),
            C.API_PROMOTION, Account.inst().getUserId(), ((Circle) getIntent().getParcelableExtra("circle")).id,
            CommonParam.getCUID(U.getContext())));
  }

  @OnClick(R.id.rb_share)
  public void onRbShare() {
    U.getShareManager().shareAppDialog(this, ((Circle) getIntent().getParcelableExtra("circle")));
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
    setTopTitle("领奖结果");
  }
}
