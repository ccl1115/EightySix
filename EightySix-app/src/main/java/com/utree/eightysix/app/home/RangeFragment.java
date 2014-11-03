package com.utree.eightysix.app.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;

/**
 */
public class RangeFragment extends BaseFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_range, container, false);
  }
}
