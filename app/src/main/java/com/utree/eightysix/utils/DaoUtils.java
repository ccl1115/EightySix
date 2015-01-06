/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import com.utree.eightysix.U;
import com.utree.eightysix.dao.ConversationDao;
import com.utree.eightysix.dao.DaoMaster;
import com.utree.eightysix.dao.DaoSession;
import com.utree.eightysix.dao.MessageDao;

/**
 */
public class DaoUtils {

  private static DaoMaster sDaoMaster;
  private static DaoSession mDaoSession;

  private static void init() {
    sDaoMaster = new DaoMaster(new DaoMaster.DevOpenHelper(U.getContext(), "com.utree.eightysix.db", null).getWritableDatabase());
    mDaoSession = sDaoMaster.newSession();
  }

  public static ConversationDao getConversationDao() {
    if (sDaoMaster == null) {
      init();
    }

    return mDaoSession.getConversationDao();
  }

  public static MessageDao getMessageDao() {
    if (sDaoMaster == null) {
      init();
    }

    return mDaoSession.getMessageDao();
  }
}
