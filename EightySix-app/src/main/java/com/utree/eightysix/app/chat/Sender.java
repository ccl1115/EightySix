package com.utree.eightysix.app.chat;


import com.utree.eightysix.dao.Message;

import java.io.File;
import java.io.InputStream;

/**
 * @author simon
 */
public interface Sender {

  void send(Message Message);

  Message txt(String chatId, String postId, String commentId, String txt);

  Message voice(String chatId, String postId, String commentId, File f);

  Message voice(String chatId, String postId, String commentId, InputStream is);

  Message photo(String chatId, String postId, String commentId, File f);

  Message photo(String chatId, String postId, String commentId, InputStream is);
}
