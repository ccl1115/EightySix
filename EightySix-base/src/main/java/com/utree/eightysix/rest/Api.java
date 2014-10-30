package com.utree.eightysix.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target (ElementType.TYPE)
@Retention (RetentionPolicy.RUNTIME)
public @interface Api {
  String value();
  boolean token() default false;
  boolean cache() default false;
  boolean log() default false;
  CachePolicy cachePolicy() default CachePolicy.CACHE_IF_FAIL;
}
