/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.cg;

/**
 */
public class LoggerConfigurationGenerator {

  public static final String TARGET_FILE = "app/src/main/assets/com.utree.eightysix.properties";

  public static void main(String[] args) {
    new ConfigurationGenerator(TARGET_FILE).generate(args);
  }

}
