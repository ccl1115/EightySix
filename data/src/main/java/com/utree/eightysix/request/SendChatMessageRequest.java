/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.request;

import com.utree.eightysix.rest.Param;

/**
 */
public class SendChatMessageRequest {

  @Param("chatId")
  public String chatId;

  @Param("msgType")
  public String msgType;

  @Param("postId")
  public String postId;

  @Param("commentId")
  public String commentId;

  public SendChatMessageRequest(String chatId, String msgType, String postId, String commentId) {
    this.chatId = chatId;
    this.msgType = msgType;
    this.postId = postId;
    this.commentId = commentId;
  }
}
