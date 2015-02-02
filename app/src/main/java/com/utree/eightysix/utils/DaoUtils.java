/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import android.database.sqlite.SQLiteDatabase;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
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

  public static void init() {
    sDaoMaster = BuildConfig.DEBUG ?
        new DaoMaster(new DaoMaster.DevOpenHelper(U.getContext(), "com.utree.eightysix.db." + Account.inst().getUserId(), null).getWritableDatabase()) :
        new DaoMaster(new DaoMaster.OpenHelper(U.getContext(), "com.utree.eightysix.db." + Account.inst().getUserId(), null) {
          @Override
          public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 26 && newVersion == 27) {
              // Alter conversation table
              db.execSQL("ALTER TABLE CONVERSATION ADD COLUMN 'USER_ID' TEXT NOT NULL;" +
                  "CREATE INDEX IF NOT EXISTS 'USER_ID' ON CONVERSATION ('USER_ID' ASC);");

              // Alter message table
              db.execSQL("ALTER TABLE MESSAGE ADD COLUMN 'USER_ID' TEXT NOT NULL;" +
                  "CREATE INDEX IF NOT EXISTS 'USER_ID' ON MESSAGE ('USER_ID' ASC);");
            }
          }
        }.getWritableDatabase());
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
