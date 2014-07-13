package com.utree.eightysix.data;

import java.util.List;

/**
 * @author simon
 */
public class QuestionSet extends BaseItem {

  public List<Question> lists;

  public static class Question {
    public String content;
    public String bgColor;
    public String bgUrl;
  }
}
