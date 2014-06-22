package com.utree.eightysix.utils;

import android.view.View;
import com.utree.eightysix.BuildConfig;
import java.lang.reflect.Field;

/**
 */
public class ViewBinding {

    public void bind(View view, Object target) {
        Field[] fields = target.getClass().getDeclaredFields();

        for (Field f : fields) {
            ViewId id = f.getAnnotation(ViewId.class);
            if (id != null) {
                View child = view.findViewById(id.value());
                if (child != null) {
                    bindId(f, child, target);

                    OnClick onClick = f.getAnnotation(OnClick.class);
                    if (onClick != null && target instanceof View.OnClickListener) {
                        bindOnClick(child, target);
                    }
                }
            }

        }

    }

    public <T> T bind(View view, Class<T> holderClass) {
        T holder;
        try {
            holder = holderClass.newInstance();
        } catch (InstantiationException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
            return null;
        }

        bind(view, holder);

        return holder;
    }

    private void bindId(Field f, View child, Object target) {
        if (child != null) {
            try {
                f.set(target, f.getType().cast(child));
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            } catch (ClassCastException e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
        }
    }

    private void bindOnClick(View child, Object target) {
        child.setOnClickListener((View.OnClickListener) target);
    }
}
