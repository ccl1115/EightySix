package com.utree.eightysix.request;

import com.utree.eightysix.rest.Param;

/**
 * @author simon
 */
public class QuestionBackRequest {

  @Param("circleId")
  public int circleId;

  @Param("userId")
  public int userId;

  public QuestionBackRequest(int circleId, int userId) {
    this.circleId = circleId;
    this.userId = userId;
  }
}
