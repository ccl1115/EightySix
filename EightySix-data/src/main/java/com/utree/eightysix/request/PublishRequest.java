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
  public Integer factoryId;

  @Param("sourceType")
  @Optional
  public Integer sourceType;

  @Param("topicId")
  @Optional
  public Integer topicId;

  @Param("tags")
  @Optional
  public String tags;

  @Param("tempName")
  @Optional
  public String tempName;

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

    public Builder factoryId(Integer id) {
      mPublishRequest.factoryId = id;
      return this;
    }

    public Builder sourceType(Integer type) {
      mPublishRequest.sourceType = type;
      return this;
    }

    public Builder topicId(Integer id) {
      mPublishRequest.topicId = id;
      return this;
    }

    public Builder tags(String tags) {
      mPublishRequest.tags = tags;
      return this;
    }

    public Builder tempName(String tempName) {
      mPublishRequest.tempName = tempName;
      return this;
    }

    public PublishRequest build() {
      return mPublishRequest;
    }

  }
}