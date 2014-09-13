package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 * @author simon
 */
@Api(C.API_SUBMIT_ANSWER)
@Token
public class SubmitAnswerRequest {

  @Param("factoryId")
  public int circleId;

  @Param("content")
  public String content;

  @Param("questionId")
  public int questionId;

  public SubmitAnswerRequest(int circleId, String content, int questionId) {
    this.circleId = circleId;
    this.content = content;
    this.questionId = questionId;
  }
}
