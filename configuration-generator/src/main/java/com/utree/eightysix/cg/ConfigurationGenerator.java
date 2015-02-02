/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.cg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class ConfigurationGenerator {

  private static final String TARGET_FILE = "app/src/main/assets/configuration_local";

  public ConfigurationGenerator() {

  }

  public static void main(String[] args) {
    new ConfigurationGenerator().generate(args);
  }

  public void generate(String[] args) {
    File file = new File(TARGET_FILE);

    Map<String, String> map = new HashMap<String, String>();

    for (String arg : args) {
      if (arg.contains("=")) {
        String[] kv = arg.split("=");
        if (kv.length == 2) {
          map.put(kv[0], kv[1]);
        }
      }
    }

    FileWriter writer = null;
    try {
      writer = new FileWriter(file);

      for (Map.Entry<String, String> entry : map.entrySet()) {
        writer.write(entry.getKey());
        writer.write("=");
        writer.write(entry.getValue());
        writer.write("\n");
      }
    } catch (IOException ignored) {
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ignored) {
        }
      }
    }
  }
}
