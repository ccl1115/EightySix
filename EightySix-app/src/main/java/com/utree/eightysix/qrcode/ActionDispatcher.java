package com.utree.eightysix.qrcode;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
public class ActionDispatcher {

  public List<Action> mActions = new ArrayList<Action>();

  public void register(Action action) {
    mActions.add(action);
  }

  public void unregister(Action action) {
    mActions.remove(action);
  }

  public void clear() {
    mActions.clear();
  }

  public boolean dispatch(String content) {
    if (content == null) return false;
    Uri uri = Uri.parse(content);

    for (Action action : mActions) {
      if (action.accept(uri)) {
        action.act(uri);
        return true;
      }
    }
    return false;
  }
}
