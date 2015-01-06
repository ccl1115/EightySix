package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author simon
 */
public class QuestionSet extends BaseItem {

  @SerializedName("lists")
  public List<Question> lists;

  public static class Question {

    @SerializedName("content")
    public String content;

    @SerializedName("bgColor")
    public String bgColor;

    @SerializedName("bgUrl")
    public String bgUrl;

    @SerializedName("buttonText")
    public String buttonText;
  }
}
