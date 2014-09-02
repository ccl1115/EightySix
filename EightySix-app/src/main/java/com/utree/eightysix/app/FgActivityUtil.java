package com.utree.eightysix.app;

import android.app.Activity;

/**
 * To detect which activity with identity key is running foreground
 * @author simon
 */
public class FgActivityUtil {

  private Identity mIdentity;

  public void put(String key, Activity activity) {
    mIdentity = new Identity(key, activity.getComponentName().getClassName());
  }

  public boolean check(String key, Activity activity) {
    return new Identity(key, activity.getComponentName().getClassName()).equals(mIdentity);
  }

  public static class Identity {
    private String key;
    private String componentName;

    Identity(String key, String componentName) {
      this.key = key;
      this.componentName = componentName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Identity identity = (Identity) o;

      if (componentName != null ? !componentName.equals(identity.componentName) : identity.componentName != null)
        return false;
      if (key != null ? !key.equals(identity.key) : identity.key != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = key != null ? key.hashCode() : 0;
      result = 31 * result + (componentName != null ? componentName.hashCode() : 0);
      return result;
    }
  }
}
