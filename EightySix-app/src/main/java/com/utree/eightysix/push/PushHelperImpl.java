package com.utree.eightysix.push;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.U;

/**
 */
public class PushHelperImpl implements PushHelper {

  public PushHelperImpl() {
  }

  public void startWork() {
    PushManager.startWork(U.getContext(), PushConstants.LOGIN_TYPE_API_KEY, U.getConfig("bd.api_key"));
  }

  public void stopWork() {
    PushManager.stopWork(U.getContext());
  }

  @Subscribe
  public void onLogoutEvent(Account.LogoutEvent event) {
    stopWork();
    M.getRegisterHelper().unregister(this);
  }
}
