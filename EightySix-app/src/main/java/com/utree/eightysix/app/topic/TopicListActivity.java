package com.utree.eightysix.app.topic;

import android.app.Activity;
import android.os.Bundle;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.request.TopicListRequest;
import com.utree.eightysix.response.TopicListResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;

/**
 */
@Layout(R.layout.activity_topic_list)
public class TopicListActivity extends BaseActivity {

  @InjectView(R.id.alv_topic)
  public AdvancedListView mAlvTopic;

  public TopicListAdapter mTopicListAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  private void requestTopicList() {
    request(new TopicListRequest(), new OnResponse2<TopicListResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(TopicListResponse response) {
        if (RESTRequester.responseOk(response)) {
          mTopicListAdapter = new TopicListAdapter(response.object);
          mAlvTopic.setAdapter(mTopicListAdapter);
        }
      }
    }, TopicListResponse.class);
  }
}