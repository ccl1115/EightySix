/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;


import com.utree.eightysix.dao.FriendMessage;

import java.io.File;

/**
 * @author simon
 */
public interface FriendSender {

  void send(FriendMessage message);

  FriendMessage txt(String chatId, String txt);

  FriendMessage voice(String chatId, File f);


  FriendMessage photo(String chatId, File f);

}
