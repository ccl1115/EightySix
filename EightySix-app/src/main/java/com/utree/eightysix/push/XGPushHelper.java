package com.utree.eightysix.push;

import android.text.TextUtils;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class XGPushHelper implements PushHelper {
  @Override
  public void startWork() {
    // 开启logcat输出，方便debug，发布时请关闭
    XGPushConfig.enableDebug(U.getContext(), true);
    // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
    // 如果需要绑定账号，请使用registerPush(getApplicationContext(),"account")版本
    // 具体可参考详细的开发指南
    // 传递的参数为ApplicationContext
    if (!TextUtils.isEmpty(Account.inst().getUserId())) {
      XGPushManager.registerPush(U.getContext(), Account.inst().getUserId());
    } else {
      XGPushManager.registerPush(U.getContext());
    }
  }

  @Override
  public void stopWork() {
    XGPushManager.unregisterPush(U.getContext());
  }
}
