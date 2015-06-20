package com.utree.eightysix.app.tag;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.HolderFragment;
import com.utree.eightysix.request.TagsByTypeRequest;
import com.utree.eightysix.response.TagsByTypeResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 */
public class MoreTagsFragment extends HolderFragment {

  @InjectView (R.id.lv_tags)
  public ListView mLvTags;

  private MoreTagAdapter mMoreTagAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_more_tag, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) {
      requireTagsByType();
    }
  }

  @Override
  protected void onActive() {
    if (getBaseActivity() != null) {
      requireTagsByType();
    }
  }

  private void requireTagsByType() {
    getBaseActivity().showProgressBar();
    getBaseActivity().request(new TagsByTypeRequest(), new OnResponse2<TagsByTypeResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideProgressBar();
      }

      @Override
      public void onResponse(TagsByTypeResponse response) {
        if (RESTRequester.responseOk(response)) {
          mMoreTagAdapter = new MoreTagAdapter(response.object);
          mLvTags.setAdapter(mMoreTagAdapter);
        }
        getBaseActivity().hideProgressBar();
      }
    }, TagsByTypeResponse.class);
  }

  @Override
  protected void onTitleClicked() {

  }

  @Override
  protected void onActionLeftClicked() {
    getActivity().finish();
  }

  @Override
  protected void onActionOverflowClicked() {

  }

  @Override
  protected String getTitle() {
    return "全部标签";
  }
}