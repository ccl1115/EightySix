package com.utree.eightysix.event;

import com.utree.eightysix.Account;

/**
 * @author simon
 */
public interface LogoutListener {
  void onLogout(Account.LogoutEvent event);
}
