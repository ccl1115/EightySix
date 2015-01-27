package com.utree.eightysix.app.feed;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.utree.eightysix.data.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class BaseItemDeserializer implements JsonDeserializer<BaseItem> {

  @Override
  public BaseItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject jObj = (JsonObject) json;
      if (jObj.has("type")) {
        int type = jObj.get("type").getAsInt();
        switch (type) {
          case BaseItem.TYPE_POST:
            return getPost(jObj);
          case BaseItem.TYPE_PROMOTION:
            return getPromotion(jObj);
          case BaseItem.TYPE_QUESTION_SET:
            return getQuestionSet(jObj);
          case BaseItem.TYPE_OPTION_SET:
            return getOptionSet(jObj);
          case BaseItem.TYPE_TOPIC:
            return getPostTopicView(jObj);
          case BaseItem.TYPE_BAINIAN:
            return getBainianView(jObj);
        }
      }
      return getBaseItem(jObj);
    }
    return null;
  }

  private QuestionSet getQuestionSet(JsonObject jObj) {
    QuestionSet questionSet = new QuestionSet();
    serializeBaseItem(jObj, questionSet);

    questionSet.lists = new ArrayList<QuestionSet.Question>();
    JsonArray array = jObj.getAsJsonArray("lists");
    for (JsonElement jsonElement : array) {
      JsonObject obj = jsonElement.getAsJsonObject();
      QuestionSet.Question question = new QuestionSet.Question();
      question.bgColor = safeGetAsString(obj.get("bgColor"));
      question.bgUrl = safeGetAsString(obj.get("bgUrl"));
      question.content = safeGetAsString(obj.get("content"));
      question.buttonText = safeGetAsString(obj.get("buttonText"));
      questionSet.lists.add(question);
    }

    return questionSet;
  }

  protected BaseItem getBaseItem(JsonObject jObj) {
    BaseItem item = new BaseItem();
    serializeBaseItem(jObj, item);
    return item;
  }

  protected Promotion getPromotion(JsonObject jObj) {
    Promotion promotion = new Promotion();
    serializeBaseItem(jObj, promotion);
    promotion.activeName = safeGetAsString(jObj.get("activeName"));
    promotion.activeRemark = safeGetAsString(jObj.get("activeRemark"));
    promotion.activeStartTime = safeGetAsString(jObj.get("activeStartTime"));
    promotion.activeUrl = safeGetAsString(jObj.get("activeUrl"));
    promotion.title = safeGetAsString(jObj.get("title"));
    promotion.activeWebViewName = safeGetAsString(jObj.get("activeWebViewName"));

    return promotion;
  }

  protected Post getPost(JsonObject jObj) {
    Post post = new Post();
    serializeBaseItem(jObj, post);
    post.comment = safeGetAsString(jObj.get("commentMsg"));
    post.commentHead = safeGetAsString(jObj.get("commentHead"));
    post.comments = safeGetAsInt(jObj.get("countComment"));
    post.commentTail = safeGetAsString(jObj.get("commentTail"));
    post.id = safeGetAsString(jObj.get("id"));
    post.myPraiseCount = safeGetAsInt(jObj.get("myPraiseCount"));
    post.praise = safeGetAsInt(jObj.get("countPraise"));
    post.praised = safeGetAsInt(jObj.get("praised"));
    post.read = safeGetAsInt(jObj.get("read"));
    post.source = safeGetAsString(jObj.get("postSource"));
    post.whoAtMe = safeGetAsString(jObj.get("whoAtMe"));
    post.isRepost = safeGetAsInt(jObj.get("isRepost"));
    post.isHot = safeGetAsInt(jObj.get("isHot"));
    post.viewType = safeGetAsInt(jObj.get("viewType"));
    post.circle = safeGetAsString(jObj.get("factoryName"));
    post.shortName = safeGetAsString(jObj.get("factoryShortName"));
    post.factoryId = safeGetAsInt(jObj.get("factoryId"));
    post.owner = safeGetAsInt(jObj.get("owner"));
    post.sourceType = safeGetAsInt(jObj.get("sourceType"));
    post.userCurrFactoryId = safeGetAsInt(jObj.get("userCurrFactoryId"));
    post.hometownText = safeGetAsString(jObj.get("hometownText"));

    post.tags = new ArrayList<Tag>();

    JsonElement e = jObj.get("tags");
    if (e != null && e.isJsonArray()) {
      JsonArray array = jObj.getAsJsonArray("tags");

      for (JsonElement tag : array) {
        JsonObject t = tag.getAsJsonObject();
        Tag g = new Tag();
        g.id = safeGetAsInt(t.get("id"));
        g.content = safeGetAsString(t.get("content"));
        post.tags.add(g);
      }
    }

    return post;
  }

  protected OptionSet getOptionSet(JsonObject jObj) {
    OptionSet optionSet = new OptionSet();

    serializeBaseItem(jObj, optionSet);

    optionSet.step = safeGetAsInt(jObj.get("step"));
    JsonArray options = jObj.getAsJsonArray("namesListView");
    optionSet.options = new ArrayList<OptionSet.Option>(options.size());

    for (JsonElement element : options) {
      JsonObject optionObj = element.getAsJsonObject();
      OptionSet.Option option = new OptionSet.Option();
      option.bgColor = safeGetAsString(optionObj.get("bgColor"));
      option.bgUrl = safeGetAsString(optionObj.get("bgUrl"));
      option.title = safeGetAsString(optionObj.get("title"));
      option.subTitle = safeGetAsString(optionObj.get("subTitle"));
      option.nextTitle = safeGetAsString(optionObj.get("nextTitle"));
      option.quesId = safeGetAsInt(optionObj.get("quesId"));

      JsonArray choices = optionObj.getAsJsonArray("options");
      option.choices = new ArrayList<OptionSet.Choice>(choices.size());

      for (JsonElement e : choices) {
        JsonObject choiceObj = e.getAsJsonObject();
        OptionSet.Choice choice = new OptionSet.Choice();
        choice.value = safeGetAsString(choiceObj.get("value"));
        choice.text = safeGetAsString(choiceObj.get("text"));

        option.choices.add(choice);
      }

      optionSet.options.add(option);
    }

    optionSet.step2View = getStepView(safeGetJsonObject(jObj, "step2View"));
    optionSet.step3View = getStepView(safeGetJsonObject(jObj, "step3View"));

    return optionSet;
  }

  private OptionSet.StepView getStepView(JsonObject jObj) {
    if (jObj == null) return null;

    OptionSet.StepView stepView = new OptionSet.StepView();

    stepView.answerHelper = safeGetAsString(jObj.get("answerHelper"));
    stepView.bgColor = safeGetAsString(jObj.get("bgColor"));
    stepView.bgUrl = safeGetAsString(jObj.get("bgUrl"));
    stepView.buttonText = safeGetAsString(jObj.get("buttonText"));
    stepView.content = safeGetAsString(jObj.get("content"));
    stepView.nextTitle = safeGetAsString(jObj.get("nextTitle"));
    stepView.subTitle = safeGetAsString(jObj.get("subTitle"));
    stepView.step = safeGetAsInt(jObj.get("step"));
    stepView.title = safeGetAsString(jObj.get("title"));
    stepView.type = safeGetAsInt(jObj.get("type"));
    stepView.viewName = safeGetAsString(jObj.get("viewName"));
    stepView.quesId = safeGetAsInt(jObj.get("quesId"));

    return stepView;
  }

  private PostTopic getPostTopicView(JsonObject jObj) {
    if (jObj == null) return null;

    PostTopic postTopic = new PostTopic();

    serializeBaseItem(jObj, postTopic);

    postTopic.headTitle = safeGetAsString(jObj.get("headTitle"));

    postTopic.id = safeGetAsInt(jObj.get("id"));
    postTopic.postCount = safeGetAsInt(jObj.get("postCount"));

    List<Tag> tags = new ArrayList<Tag>();

    JsonArray array = jObj.getAsJsonArray("tags");

    for (JsonElement element : array) {
      JsonObject tagObj = element.getAsJsonObject();
      Tag tag = new Tag();
      tag.id = safeGetAsInt(tagObj.get("id"));
      tag.content = safeGetAsString(tagObj.get("content"));
      tags.add(tag);
    }

    postTopic.tags = tags;

    return postTopic;
  }

  private Bainian getBainianView(JsonObject jObj) {
    if (jObj == null) return null;

    Bainian bainian = new Bainian();

    serializeBaseItem(jObj, bainian);

    bainian.buttonText = safeGetAsString(jObj.get("buttonText"));
    bainian.contentText = safeGetAsString(jObj.get("contentText"));
    bainian.receiveText = safeGetAsString(jObj.get("receiveText"));
    bainian.subTitle = safeGetAsString(jObj.get("subTitle"));
    bainian.title = safeGetAsString(jObj.get("title"));

    bainian.newYearContents = new ArrayList<Bainian.NewYearContent>();

    JsonArray ja = jObj.getAsJsonArray("newYearContents");

    for (JsonElement element : ja) {
      JsonObject contentObj = element.getAsJsonObject();

      Bainian.NewYearContent content = new Bainian.NewYearContent();

      content.content = safeGetAsString(contentObj.get("content"));
      content.id = safeGetAsInt(contentObj.get("id"));

      bainian.newYearContents.add(content);
    }

    return bainian;
  }

  private void serializeBaseItem(JsonObject jObj, BaseItem item) {
    item.bgColor = safeGetAsString(jObj.get("bgColor"));
    item.bgUrl = safeGetAsString(jObj.get("bgUrl"));
    item.content = safeGetAsString(jObj.get("content"));
    item.type = safeGetAsInt(jObj.get("type"));
  }

  private String safeGetAsString(JsonElement element) {
    if (element == null || element.isJsonNull()) return "";
    else return element.getAsString();
  }

  private int safeGetAsInt(JsonElement element) {
    if (element == null || element.isJsonNull()) return 0;
    else return element.getAsInt();
  }

  private JsonObject safeGetJsonObject(JsonObject object, String key) {
    JsonElement element = object.get(key);
    if (element instanceof JsonObject) {
      return element.getAsJsonObject();
    } else {
      return null;
    }
  }
}
