package com.utree.eightysix.app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;

/**
 * @author simon
 */
public class BaseFragment extends Fragment {

  public BaseActivity getBaseActivity() {
    return (BaseActivity) getActivity();
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
}