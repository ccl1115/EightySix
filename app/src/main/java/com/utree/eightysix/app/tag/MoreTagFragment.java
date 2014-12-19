package com.utree.eightysix.app.tag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.request.TagsByTypeRequest;
import com.utree.eightysix.response.TagsByTypeResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 */
public class MoreTagFragment extends BaseFragment {

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
}