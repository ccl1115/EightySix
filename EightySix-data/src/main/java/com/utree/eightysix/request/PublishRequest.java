package com.utree.eightysix.request;

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
  public int factoryId;

  @Param("sourceType")
  @Optional
  public int sourceType;

  public PublishRequest(int factoryId, String content, String bgColor, String bgUrl) {
    this.factoryId = factoryId;
    this.content = content;
    this.bgColor = bgColor;
    this.bgUrl = bgUrl;
  }

  public PublishRequest(String bgUrl, String bgColor, String content, int factoryId, int sourceType) {
    this.bgUrl = bgUrl;
    this.bgColor = bgColor;
    this.content = content;
    this.factoryId = factoryId;
    this.sourceType = sourceType;
  }
}