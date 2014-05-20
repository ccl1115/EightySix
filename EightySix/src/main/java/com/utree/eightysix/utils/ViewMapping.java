package com.utree.eightysix.utils;

import android.view.View;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 */
public class ViewMapping {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ViewId {
        int value();
    }

    public static <T> T map(View view, Class<T> holderClass) {
        Field[] fields = holderClass.getFields();

        T holder;
        try {
            holder = holderClass.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }

        for (Field f : fields) {
            ViewId id = f.getAnnotation(ViewId.class);
            if (id == null) {
                continue;
            }

            View child = view.findViewById(id.value());

            if (child == null) {
                return null;
            }

            try {
                f.set(holder, f.getClass().cast(child));
            } catch (IllegalAccessException e) {
                return null;
            } catch (ClassCastException e) {
                return null;
            }
        }

        return holder;
    }
}
