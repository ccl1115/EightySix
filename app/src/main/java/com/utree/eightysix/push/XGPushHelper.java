package com.utree.eightysix.push;

import android.content.Intent;
import android.text.TextUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class XGPushHelper implements PushHelper {
  @Override
  public void startWork() {
    Intent service = new Intent(U.getContext(), XGPushService.class);
    U.getContext().startService(service);
    // 开启logcat输出，方便debug，发布时请关闭
    XGPushConfig.enableDebug(U.getContext(), BuildConfig.DEBUG);
    // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
    // 如果需要绑定账号，请使用registerPush(getApplicationContext(),"account")版本
    // 具体可参考详细的开发指南
    // 传递的参数为ApplicationContext
    if (!TextUtils.isEmpty(Account.inst().getUserId())) {
      XGPushManager.registerPush(U.getContext(), Account.inst().getUserId(), new XGIOperateCallback() {
        @Override
        public void onSuccess(Object o, int i) {

        }

        @Override
        public void onFail(Object o, int i, String s) {

        }
      });
    } else {
      XGPushManager.registerPush(U.getContext());
    }
  }

  @Override
  public void stopWork() {
    XGPushManager.unregisterPush(U.getContext());
  }
}
