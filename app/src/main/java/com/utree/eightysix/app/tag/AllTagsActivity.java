/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.tag;

import android.os.Bundle;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.request.TagsRequest;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
@Layout(R.layout.activity_all_tags)
@TopTitle(R.string.all_tags)
public class AllTagsActivity extends BaseActivity {

  @InjectView(R.id.content)
  public SwipeRefreshLayout mRefreshLayout;

  @InjectView(R.id.alv_tags)
  public AdvancedListView mAlvTags;

  private AllTagsAdapter mAllTagsAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mRefreshLayout.setColorSchemeResources(R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed);

    mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        requestTags();
        showRefreshIndicator(true);
      }

      @Override
      public void onDrag(int value) {
        showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        hideRefreshIndicator();
      }
    });

    requestTags();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestTags() {
    request(new TagsRequest(), new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(TagsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mAllTagsAdapter = new AllTagsAdapter(response.object.tags);
          mAlvTags.setAdapter(mAllTagsAdapter);
        }

        mRefreshLayout.setRefreshing(false);
        hideRefreshIndicator();
      }
    }, TagsResponse.class);
  }
}