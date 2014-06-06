package com.utree.eightysix.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {

    enum METHOD {
        GET, POST
    }

    METHOD value() default METHOD.GET;
}
