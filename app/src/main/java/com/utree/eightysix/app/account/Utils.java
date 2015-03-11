/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import com.utree.eightysix.U;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;

/**
 */
class Utils {

  static void updateProfile(String avatar,
                            String name,
                            String sex,
                            Long birthday,
                            String constellation,
                            String background,
                            String signature,
                            OnResponse<Response> response) {
    U.request("profile_fill", response, Response.class,
        avatar, sex, name, birthday, constellation, background, signature);
  }
}
