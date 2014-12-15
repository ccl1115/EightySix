/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

import java.io.File;

/**
 */
public class Generate {

  public Generate() {
    Schema schema = new Schema(8, "com.utree.eightysix.dao");

    generateConversation(schema);
    generateMessage(schema);

    try {
      String path = "EightySix-data/src/main/java";
      File file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
      } else if (file.isFile()) {
        file.delete();
        file.mkdirs();
      }
      new DaoGenerator().generateAll(schema, path);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void generateConversation(Schema schema) {
    Entity conversation = schema.addEntity("Conversation");
    conversation.addIdProperty();
    conversation.addStringProperty("chatId").notNull();
    conversation.addStringProperty("postId");
    conversation.addStringProperty("commentId");
    conversation.addStringProperty("lastMsg");
    conversation.addStringProperty("portrait");
    conversation.addStringProperty("bgUrl");
    conversation.addStringProperty("postContent");
    conversation.addStringProperty("commentContent");
    conversation.addStringProperty("chatSource");
    conversation.addStringProperty("relation");
    conversation.addLongProperty("timestamp").indexDesc(null, false);
    conversation.addLongProperty("unreadCount");
    conversation.addBooleanProperty("favorite");
  }

  private void generateMessage(Schema schema) {
    Entity message = schema.addEntity("Message");
    message.addIdProperty();
    message.addStringProperty("chatId").index().notNull();
    message.addStringProperty("postId");
    message.addStringProperty("commentId");
    message.addStringProperty("msgId");
    message.addLongProperty("timestamp").indexDesc(null, false);
    message.addStringProperty("from");
    message.addIntProperty("type");
    message.addStringProperty("content");
    message.addIntProperty("status");
    message.addBooleanProperty("read");
    message.addIntProperty("direction");
  }

  public static void main(String[] args) {
    new Generate();
  }
}
