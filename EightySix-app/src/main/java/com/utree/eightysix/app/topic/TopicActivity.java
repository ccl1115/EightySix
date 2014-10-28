package com.utree.eightysix.app.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.data.TopicFeed;
import com.utree.eightysix.request.FeatureTopicFeedRequest;
import com.utree.eightysix.request.NewTopicFeedRequest;
import com.utree.eightysix.response.TopicFeedResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;

/**
 */
@Layout (R.layout.activity_topic)
public class TopicActivity extends BaseActivity {

  static final int TAB_NEW = 0;
  static final int TAB_FEATURE = 1;

  @InjectView(R.id.content)
  public AdvancedListView mAlvTopic;

  public TopicFeedAdapter mTopicFeedAdapter;

  private Paginate.Page mNewPageInfo;

  private Paginate.Page mFeaturePageInfo;

  private Topic mTopic;

  public static void start(Context context, Topic topic) {
    Intent intent = new Intent(context, TopicActivity.class);
    intent.putExtra("topic", topic);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @OnItemClick(R.id.content)
  public void onAlvTopicItemClicked(int position) {
    if (position > 0) {
      PostActivity.start(this, (Post) mTopicFeedAdapter.getItem(position));
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTopTitle("话题");

    mTopic = getIntent().getParcelableExtra("topic");

    if (mTopic != null) {
      mTopicFeedAdapter = new TopicFeedAdapter(mTopic);
      mAlvTopic.setAdapter(mTopicFeedAdapter);
    }

    mAlvTopic.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        switch (mTopicFeedAdapter.getCurrentTab()) {
          case TAB_NEW:
            if (mNewPageInfo != null) {
              return mNewPageInfo.currPage < mNewPageInfo.countPage;
            }
            break;
          case TAB_FEATURE:
            if (mFeaturePageInfo != null) {
              return mFeaturePageInfo.currPage < mNewPageInfo.countPage;
            }
            break;
        }
        return false;
      }

      @Override
      public boolean onLoadMoreStart() {
        switch (mTopicFeedAdapter.getCurrentTab()) {
          case TAB_NEW:
            requestNewTopicFeed(mNewPageInfo.currPage + 1);
            break;
          case TAB_FEATURE:
            requestFeatureTopicFeed(mFeaturePageInfo.currPage + 1);
            break;
        }
        return true;
      }
    });

    mTopicFeedAdapter.setCallback(new TopicFeedAdapter.Callback() {
      @Override
      public void onTabLeftClicked() {
      }

      @Override
      public void onTabRightClicked() {
      }

      @Override
      public void onSendClicked() {
        PublishActivity.startWithTopicId(TopicActivity.this, mTopic.id);
      }
    });

    requestNewTopicFeed(1);
    requestFeatureTopicFeed(1);
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Subscribe
  public void onPostPublichedEvent(PostPublishedEvent event) {
    if (mTopicFeedAdapter.getCurrentTab() == TAB_NEW) {
      requestNewTopicFeed(1);
    } else if (mTopicFeedAdapter.getCurrentTab() == TAB_FEATURE) {
      requestFeatureTopicFeed(1);
    }
  }

  private void requestNewTopicFeed(final int page) {
    if (page == 1) {
      showProgressBar();
    }
    request(new NewTopicFeedRequest(mTopic.id, page), new OnResponse2<TopicFeedResponse>() {

      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(TopicFeedResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mTopicFeedAdapter.getNewPosts().clear();
            mTopicFeedAdapter.setTopic(response.object.topic);
          }
          mTopicFeedAdapter.add(TAB_NEW, response.object.posts.lists);
          mNewPageInfo = response.object.posts.page;
        }

        hideProgressBar();
      }
    }, TopicFeedResponse.class);
  }

  private void requestFeatureTopicFeed(final int page) {
    if (page == 1) {
      showProgressBar();
    }
    request(new FeatureTopicFeedRequest(mTopic.id, page), new OnResponse2<TopicFeedResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(TopicFeedResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mTopicFeedAdapter.getFeaturePosts().clear();
            mTopicFeedAdapter.setTopic(response.object.topic);
          }
          mTopicFeedAdapter.add(TAB_FEATURE, response.object.posts.lists);
          mFeaturePageInfo = response.object.posts.page;
        }

        hideProgressBar();
      }
    }, TopicFeedResponse.class);
  }
}