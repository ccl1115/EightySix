package com.utree.eightysix.app.feed;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Promotion;
import com.utree.eightysix.data.QuestionSet;
import de.akquinet.android.androlog.Log;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author simon
 */
public class BaseItemDeserializer implements JsonDeserializer<BaseItem> {

  @Override
  public BaseItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    Log.d("GSON", "deserialize BaseItem");
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
    post.comment = safeGetAsString(jObj.get("comment"));
    post.commentHead = safeGetAsString(jObj.get("commentHead"));
    post.comments = safeGetAsInt(jObj.get("countComment"));
    post.commentTail = safeGetAsString(jObj.get("commentTail"));
    post.id = safeGetAsString(jObj.get("id"));
    post.myPraiseCount = safeGetAsInt(jObj.get("myPraiseCount"));
    post.praise = safeGetAsInt(jObj.get("countPraise"));
    post.praised = safeGetAsInt(jObj.get("praised"));
    post.read = safeGetAsInt(jObj.get("readed"));
    post.source = safeGetAsString(jObj.get("source"));
    post.whoAtMe = safeGetAsString(jObj.get("whoAtMe"));
    return post;
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
