package com.utree.eightysix.app.tag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;

/**
 */
public class MoreTagFragment extends BaseFragment {
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_more_tag, container, false);
  }
}