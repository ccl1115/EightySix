package com.utree.eightysix.utils;

import com.utree.eightysix.U;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author simon
 */
public final class RegisterHelper {

  private List<WeakReference<Object>> mWeakReferences = new ArrayList<WeakReference<Object>>(24);

  public void register(Object object) {
    for (WeakReference obj : mWeakReferences) {
      Object target = obj.get();
      if (target != null && target == object) {
        return;
      }
    }
    U.getBus().register(object);
    mWeakReferences.add(new WeakReference<Object>(object));
  }

  public void unregister(Object object) {
    for (Iterator<WeakReference<Object>> iterator = mWeakReferences.iterator(); iterator.hasNext(); ) {
      WeakReference obj = iterator.next();
      Object target = obj.get();
      if (target != null && target == object) {
        try {
          U.getBus().unregister(object);
          obj.clear();
        } catch (IllegalArgumentException ignored) {
        }
      }
      iterator.remove();
    }
  }
}
