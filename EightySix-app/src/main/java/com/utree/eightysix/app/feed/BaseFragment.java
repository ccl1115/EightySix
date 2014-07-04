package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;

/**
 * @author simon
 */
public class BaseFragment extends Fragment {

  BaseActivity getBaseActivity() {
    return (BaseActivity) getActivity();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    U.getBus().register(this);
  }

  @Override
  public void onDetach() {
    super.onDetach();

    U.getBus().unregister(this);
  }
}
