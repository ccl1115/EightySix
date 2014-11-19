package com.utree.eightysix.app.explore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;

/**
 */
public class ExploreFragment extends BaseFragment {


  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_explore, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    onHiddenChanged(false);
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      getTopBar().setTitle(getBaseActivity().getString(R.string.explore));
      getTopBar().setSubTitle("");
    }
  }
}
