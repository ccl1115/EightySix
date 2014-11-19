package com.utree.eightysix.app.explore;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.widget.ITopBar2;

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
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    onHiddenChanged(false);
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    if (!hidden) {
      ITopBar2 topBar = getTopBar();
      topBar.setTitle(getBaseActivity().getString(R.string.explore));
      topBar.setSubTitle("");
      topBar.hideLeft();
    }
  }
}
