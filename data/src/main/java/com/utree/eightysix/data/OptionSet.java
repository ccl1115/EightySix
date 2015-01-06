package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author simon
 */
public class OptionSet extends BaseItem {

  @SerializedName("step")
  public int step;

  @SerializedName("namesListView")
  public List<Option> options;

  @SerializedName("step2View")
  public StepView step2View;

  @SerializedName("step3View")
  public StepView step3View;

  public static class StepView {

    @SerializedName("content")
    public String content;

    @SerializedName("bgUrl")
    public String bgUrl;

    @SerializedName("bgColor")
    public String bgColor;

    @SerializedName("type")
    public int type;

    @SerializedName("buttonText")
    public String buttonText;

    @SerializedName("nextTitle")
    public String nextTitle;

    @SerializedName("title")
    public String title;

    @SerializedName("subTitle")
    public String subTitle;

    @SerializedName("step")
    public int step;

    @SerializedName("viewName")
    public String viewName;

    @SerializedName("answerHelper")
    public String answerHelper;

    @SerializedName("quesId")
    public int quesId;
  }

  public static class Option {

    @SerializedName("quesId")
    public int quesId;

    @SerializedName("bgUrl")
    public String bgUrl;

    @SerializedName("bgColor")
    public String bgColor;

    @SerializedName("title")
    public String title;

    @SerializedName("subTitle")
    public String subTitle;

    @SerializedName("step")
    public int step;

    @SerializedName("nextTitle")
    public String nextTitle;

    @SerializedName("options")
    public List<Choice> choices;
  }

  public static class Choice {
    @SerializedName("value")
    public String value;

    @SerializedName("text")
    public String text;
  }
}
