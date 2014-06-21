package com.utree.eightysix;

import java.util.List;

/**
 * @author simon
 */
public interface Fixture {
  <T> List<T> get(Class<T> clz, int quantity, String template);

  <T> T get(Class<T> clz, String template);
}
