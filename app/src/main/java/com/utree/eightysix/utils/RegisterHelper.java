package com.utree.eightysix.utils;

import com.squareup.otto.Bus;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;

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
    register(U.getBus(), object);
  }

  public void unregister(Object object) {
    unregister(U.getBus(), object);
  }

  public void register(Bus bus, Object object) {
    if (object == null) return;
    for (WeakReference obj : mWeakReferences) {
      Object target = obj.get();
      if (target != null && target == object) {
        return;
      }
    }
    bus.register(object);
    mWeakReferences.add(new WeakReference<Object>(object));
    Log.d("RegisterHelper", "register: " + object.toString());
  }

  public void unregister(Bus bus, Object object) {
    if (object == null) return;
    for (Iterator<WeakReference<Object>> iterator = mWeakReferences.iterator(); iterator.hasNext(); ) {
      WeakReference obj = iterator.next();
      Object target = obj.get();
      if (target != null && target == object) {
        try {
          bus.unregister(object);
          obj.clear();
          iterator.remove();
          Log.d("RegisterHelper", "unregister: " + object.toString());
        } catch (IllegalArgumentException ignored) {
        }
      }
    }
  }
}
