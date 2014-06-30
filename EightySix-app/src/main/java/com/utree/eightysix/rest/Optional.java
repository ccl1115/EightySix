package com.utree.eightysix.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author simon
 */
@Target (ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
