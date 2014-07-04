package com.utree.eightysix.app.feed;

import android.support.v4.app.Fragment;
import com.utree.eightysix.app.BaseActivity;

/**
 * @author simon
 */
public class BaseFragment extends Fragment {

  BaseActivity getBaseActivity() {
    return (BaseActivity) getActivity();
  }
}
