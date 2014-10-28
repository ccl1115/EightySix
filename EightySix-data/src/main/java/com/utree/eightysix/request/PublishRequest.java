package com.utree.eightysix.request;

import android.os.Build;
import com.utree.eightysix.C;
import com.utree.eightysix.rest.Api;
import com.utree.eightysix.rest.Optional;
import com.utree.eightysix.rest.Param;
import com.utree.eightysix.rest.Token;

/**
 */
@Api (C.API_FEED_ADD)
@Token
public class PublishRequest {

  @Optional
  @Param ("bgUrl")
  public String bgUrl;

  @Param ("bgColor")
  public String bgColor;

  @Param ("content")
  public String content;

  @Param ("factoryId")
  @Optional
  public int factoryId;

  @Param("sourceType")
  @Optional
  public int sourceType;

  @Param("topicId")
  @Optional
  public int topicId;

  private PublishRequest() {

  }

  public static class Builder {

    private PublishRequest mPublishRequest = new PublishRequest();

    public Builder bgUrl(String url) {
      mPublishRequest.bgUrl = url;
      return this;
    }

    public Builder bgColor(String color) {
      mPublishRequest.bgColor = color;
      return this;
    }

    public Builder content(String content) {
      mPublishRequest.content = content;
      return this;
    }

    public Builder factoryId(int id) {
      mPublishRequest.factoryId = id;
      return this;
    }

    public Builder sourceType(int type) {
      mPublishRequest.sourceType = type;
      return this;
    }

    public Builder topicId(int id) {
      mPublishRequest.topicId = id;
      return this;
    }

    public PublishRequest build() {
      return mPublishRequest;
    }

  }
}