package com.utree.eightysix.app.nearby;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;

/**
 */
public class NearbyFragment extends BaseFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_nearby, container, false);
  }
}