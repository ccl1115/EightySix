package com.utree.eightysix.app.msg;

import android.content.Context;
import android.text.TextUtils;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import java.util.HashSet;
import java.util.Set;

/**
 * @author simon
 */
class ReadMsgStore {

  private Set<String> sStore;

  private static ReadMsgStore sReadMsgStore;

  static ReadMsgStore inst() {
    if (sReadMsgStore == null) {
      sReadMsgStore = new ReadMsgStore();
    }
    return sReadMsgStore;
  }

  ReadMsgStore() {
    M.getRegisterHelper().register(this);
  }

  void addRead(String postId) {
    sStore.add(postId);
    StringBuilder builder = new StringBuilder();
    for (String s : sStore) {
      builder.append(s).append(",");
    }
    U.getContext().getSharedPreferences("read_msg_store", Context.MODE_PRIVATE)
        .edit().putString(Account.inst().getUserId(), builder.toString()).apply();
  }

  void clearRead() {
    if (sStore != null) {
      sStore.clear();
    }
    U.getContext().getSharedPreferences("read_msg_store", Context.MODE_PRIVATE)
        .edit().remove(Account.inst().getUserId()).apply();
  }

  boolean isRead(String postId) {
    if (sStore == null) {
      String stores = U.getContext().getSharedPreferences("read_msg_store", Context.MODE_PRIVATE)
          .getString(Account.inst().getUserId(), "");

      String[] msgs = stores.split(",");

      sStore = new HashSet<String>();

      for (String m : msgs) {
        if (!TextUtils.isEmpty(m)) {
          sStore.add(m);
        }
      }
    }
    return sStore.contains(postId);
  }

  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    sStore = null;
  }
}
