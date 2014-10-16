package com.utree.eightysix.app.chat;

import com.easemob.chat.EMMessage;

import java.io.File;
import java.io.InputStream;

/**
 * @author simon
 */
public interface Sender {

  void send(EMMessage emMessage);

  void txt(String username, String txt);

  void voice(String username, File f);

  void voice(String username, InputStream is);

  void photo(String username, File f);

  void photo(String username, InputStream is);
}
