package com.utree.eightysix.app.feed;

import com.google.gson.*;
import com.utree.eightysix.U;
import com.utree.eightysix.data.*;

import java.lang.reflect.Type;

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
          case BaseItem.TYPE_FEED_INTENT:
            return getFeedIntent(jObj);
        }
      }
      return getBaseItem(jObj);
    }
    return null;
  }

  private FeedIntent getFeedIntent(JsonObject jObj) {
    return U.getGson().fromJson(jObj, FeedIntent.class);
  }

  private QuestionSet getQuestionSet(JsonObject jObj) {
    return U.getGson().fromJson(jObj, QuestionSet.class);
  }

  protected BaseItem getBaseItem(JsonObject jObj) {
    BaseItem item = new BaseItem();
    serializeBaseItem(jObj, item);
    return item;
  }

  protected Promotion getPromotion(JsonObject jObj) {
    return U.getGson().fromJson(jObj, Promotion.class);
  }

  protected Post getPost(JsonObject jObj) {
    return U.getGson().fromJson(jObj, Post.class);
  }

  protected OptionSet getOptionSet(JsonObject jObj) {
    return U.getGson().fromJson(jObj, OptionSet.class);
  }

  private PostTopic getPostTopicView(JsonObject jObj) {
    return U.getGson().fromJson(jObj, PostTopic.class);
  }

  private Bainian getBainianView(JsonObject jObj) {
    return U.getGson().fromJson(jObj, Bainian.class);
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

}
