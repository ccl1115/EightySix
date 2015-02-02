/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.cg;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConfigurationGeneratorTest {

  private ConfigurationGenerator mGenerator;

  private final static String[] FIXED = {
      "#api.host = http://c.lanmeiquan.com",
      "#api.host = http://lanmei.gz.1251114078.clb.myqcloud.com",
      "#api.host = http://10.18.126.45:8080/wm-webapp",
      "#api.host = http://10.18.126.77:8088/wm-webapp",
      "#api.host = http://192.168.0.117:8080",
      "#api.host = http://192.168.0.101:8080",
      "api.host = http://192.168.0.118:8088",
      "#api.host = http://192.168.0.103:8080",
      "#api.host = http://192.168.0.105:8088",
      "#api.host = http://182.254.172.170"
  };

  @BeforeMethod
  public void setUp() throws Exception {
    mGenerator = new ConfigurationGenerator();
  }

  @Test
  public void testGenerate() throws Exception {
    mGenerator.generate(FIXED);
  }
}