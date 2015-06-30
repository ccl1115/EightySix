package com.utree.eightysix.app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.utree.eightysix.M;
import com.utree.eightysix.widget.TopBar;

/**
 * @author simon
 */
public class BaseFragment extends Fragment {

  public BaseActivity getBaseActivity() {
    return (BaseActivity) getActivity();
  }

  private boolean mActive;

  public void setActive(boolean active) {
    if (mActive != active) {
      mActive = active;
      if (mActive) {
        onActive();
      }
    }
  }

  public boolean isActive() {
    return mActive;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    M.getRegisterHelper().register(this);
  }

  @Override
  public void onDetach() {
    super.onDetach();

    M.getRegisterHelper().unregister(this);
  }

  protected void onActive() {

  }

  public boolean detachSelf() {
    if (!isDetached()) {
      final FragmentManager fragmentManager = getFragmentManager();
      if (fragmentManager != null) {
        fragmentManager.beginTransaction()
            .detach(this).commit();
        return true;
      }

      FragmentManager childFragmentManager = getChildFragmentManager();
      if (childFragmentManager != null) {
        childFragmentManager.beginTransaction()
            .detach(this).commit();
        return true;
      }
    }
    return false;
  }

  public boolean hideSelf() {
    if (!isHidden()) {
      final FragmentManager m = getFragmentManager();
      if (m != null) {
        m.beginTransaction().hide(this).commit();
        return true;
      }

      final FragmentManager cm = getChildFragmentManager();
      if (cm != null) {
        cm.beginTransaction().hide(this).commit();
        return true;
      }
    }
    return false;
  }

  public boolean onBackPressed() {
    return false;
  }

  public TopBar getTopBar() {
    return getBaseActivity().getTopBar();
  }
}
