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
    Schema schema = new Schema(29, "com.utree.eightysix.dao");

    generateConversation(schema);
    generateMessage(schema);

    generateFriendConversation(schema);
    generateFriendMessage(schema);

    try {
      String path = "data/src/main/java";
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
    conversation.addStringProperty("userId").index();
    conversation.addStringProperty("chatId").notNull().unique();
    conversation.addStringProperty("postId");
    conversation.addStringProperty("postSource");
    conversation.addStringProperty("postContent");
    conversation.addStringProperty("portrait");
    conversation.addStringProperty("portraitColor");
    conversation.addStringProperty("myPortrait");
    conversation.addStringProperty("myPortraitColor");
    conversation.addStringProperty("bgUrl");
    conversation.addStringProperty("bgColor");
    conversation.addStringProperty("commentId");
    conversation.addStringProperty("commentContent");
    conversation.addStringProperty("relation");
    conversation.addStringProperty("lastMsg");
    conversation.addLongProperty("timestamp").indexDesc(null, false);
    conversation.addLongProperty("unreadCount");
    conversation.addBooleanProperty("favorite");
    conversation.addLongProperty("offlineDuration");
    conversation.addBooleanProperty("online");
    conversation.addBooleanProperty("banned");
  }

  private void generateMessage(Schema schema) {
    Entity message = schema.addEntity("Message");
    message.addIdProperty();
    message.addStringProperty("userId").index();
    message.addStringProperty("chatId").notNull();
    message.addStringProperty("postId").index();
    message.addStringProperty("commentId");
    message.addStringProperty("commentContent");
    message.addStringProperty("msgId");
    message.addLongProperty("timestamp").indexDesc(null, false);
    message.addStringProperty("from");
    message.addIntProperty("type");
    message.addStringProperty("content");
    message.addIntProperty("status");
    message.addBooleanProperty("read");
    message.addIntProperty("direction");
  }

  private void generateFriendConversation(Schema schema) {
    Entity friendConversation = schema.addEntity("FriendConversation");
    friendConversation.addIdProperty();
    friendConversation.addStringProperty("userId").index();
    friendConversation.addStringProperty("chatId").notNull().unique();
    friendConversation.addIntProperty("viewId");
    friendConversation.addStringProperty("targetName");
    friendConversation.addStringProperty("targetAvatar");
    friendConversation.addStringProperty("myName");
    friendConversation.addStringProperty("myAvatar");
    friendConversation.addStringProperty("source");
    friendConversation.addStringProperty("lastMsg");
    friendConversation.addLongProperty("timestamp").indexDesc(null, false);
    friendConversation.addLongProperty("unreadCount");
    friendConversation.addBooleanProperty("banned");
  }

  private void generateFriendMessage(Schema schema) {
    Entity friendMessage = schema.addEntity("FriendMessage");
    friendMessage.addIdProperty();
    friendMessage.addStringProperty("userId").index();
    friendMessage.addStringProperty("chatId").notNull();
    friendMessage.addStringProperty("msgId");
    friendMessage.addLongProperty("timestamp").indexDesc(null, false);
    friendMessage.addStringProperty("from");
    friendMessage.addStringProperty("content");
    friendMessage.addIntProperty("type");
    friendMessage.addIntProperty("status");
    friendMessage.addBooleanProperty("read");
    friendMessage.addIntProperty("direction");
  }

  public static void main(String[] args) {
    new Generate();
  }
}
