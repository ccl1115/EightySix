package com.utree.eightysix.utils;

import android.app.Activity;
import android.os.Bundle;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class ShareUtils {

  private static QQAuth sQqAuth;
  private static Tencent sTencent;

  static {
    sQqAuth = QQAuth.createInstance(U.getConfig("qq.app_id"), U.getContext());
    sTencent = Tencent.createInstance(U.getConfig("qq.app_id"), U.getContext());
  }


  public static void shareToQQ(Activity from, Bundle data, IUiListener listener) {
    QQShare share = new QQShare(U.getContext(), sQqAuth.getQQToken());
    data.putString(QQShare.SHARE_TO_QQ_APP_NAME, U.getContext().getString(R.string.app_name));
    share.shareToQQ(from, data, listener);
  }
}
