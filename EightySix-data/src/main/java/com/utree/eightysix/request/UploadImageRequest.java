/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.request;

import com.utree.eightysix.rest.*;

import java.io.File;

/**
 */
@Api("/commit/uploadimage")
@Host("http://203.195.213.36:8200")
@Token
@Sign
public class UploadImageRequest {

  @Param("image")
  public File image;

  public UploadImageRequest(File image) {
    this.image = image;
  }
}
