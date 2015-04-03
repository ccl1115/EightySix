/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.request;

import com.utree.eightysix.C;
import com.utree.eightysix.rest.*;

import java.io.File;

/**
 */
@Api(C.COMMIT_UPLOADIMAGE)
@Token
@Sign
@Host("http://192.168.0.118:8000")
public class UploadImageRequest {

  @Param("image")
  public File image;

  public UploadImageRequest(File image) {
    this.image = image;
  }
}
