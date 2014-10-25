package com.utree.eightysix.app.topic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.request.TopicListRequest;
import com.utree.eightysix.response.TopicListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;

/**
 */
@Layout(R.layout.activity_topic_list)
@TopTitle(R.string.all_topic)
public class TopicListActivity extends BaseActivity {

  @InjectView(R.id.alv_topic)
  public AdvancedListView mAlvTopic;

  private TopicListAdapter mTopicListAdapter;

  private Paginate.Page mPageInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestTopicList(1);

    mAlvTopic.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(TopicListActivity.this).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && mPageInfo.currPage < mPageInfo.countPage;
      }

      @Override
      public boolean onLoadMoreStart() {
        requestTopicList(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        return true;
      }
    });
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestTopicList(final int page) {
    request(new TopicListRequest(page), new OnResponse2<TopicListResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(TopicListResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mTopicListAdapter = new TopicListAdapter(response.object);
            mAlvTopic.setAdapter(mTopicListAdapter);
          } else {
            mTopicListAdapter.add(response.object.hotTopic.postTopics.lists);
          }
          mPageInfo = response.object.hotTopic.postTopics.page;
        }
      }
    }, TopicListResponse.class);
  }
}