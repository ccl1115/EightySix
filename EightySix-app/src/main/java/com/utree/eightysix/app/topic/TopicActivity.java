package com.utree.eightysix.app.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.request.FeatureTopicFeedRequest;
import com.utree.eightysix.request.NewTopicFeedRequest;
import com.utree.eightysix.response.TopicFeedResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import java.util.List;

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
      Post item = (Post) mTopicFeedAdapter.getItem(position);
      if (item != null) {
        PostActivity.start(this, item);
      }
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
              return mFeaturePageInfo.currPage < mFeaturePageInfo.countPage;
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
        if (Account.inst().getCurrentCircle() != null) {
          PublishActivity.startWithTopicId(TopicActivity.this, mTopic.id, mTopic.tags);
        } else {
          U.showToast("还没有在职工厂，不能发话题帖哦");
        }
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
  public void onPostEvent(Post post) {
    List<Post> posts;
    if (mTopicFeedAdapter.getCurrentTab() == TAB_NEW) {
      posts = mTopicFeedAdapter.getNewPosts();
    } else {
      posts = mTopicFeedAdapter.getFeaturePosts();
    }
    for (Post p : posts) {
      if (p.equals(post)) {
        p.praise = post.praise;
        p.praised = post.praised;
        p.comments = post.comments;
        mTopicFeedAdapter.notifyDataSetChanged();
        break;
      }
    }
  }

  @Subscribe
  public void onPostPublishedEvent(PostPublishedEvent event) {
    requestNewTopicFeed(1);
    mTopicFeedAdapter.switchTab(TAB_NEW);
  }

  private void requestNewTopicFeed(final int page) {
    if (page == 1) {
      showProgressBar();
    }

    NewTopicFeedRequest request;
    if (page == 1) {
      request = new NewTopicFeedRequest(mTopic.id, page);
    } else {
      request = new NewTopicFeedRequest(mTopic.id, page,
          mTopicFeedAdapter.getNewPosts().get(mTopicFeedAdapter.getNewPosts().size() - 1).id);
    }
    request(request, new OnResponse2<TopicFeedResponse>() {

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
            mTopicFeedAdapter.showNewEmptyView(response.object.posts.lists.size() == 0);
          }
          mTopicFeedAdapter.add(TAB_NEW, response.object.posts.lists);
          mNewPageInfo = response.object.posts.page;
        }

        mAlvTopic.stopLoadMore();
        hideProgressBar();
      }
    }, TopicFeedResponse.class);
  }

  private void requestFeatureTopicFeed(final int page) {
    if (page == 1) {
      showProgressBar();
    }
    FeatureTopicFeedRequest request;
    if (page == 1) {
      request = new FeatureTopicFeedRequest(mTopic.id, page);
    } else {
      request = new FeatureTopicFeedRequest(mTopic.id, page,
          mTopicFeedAdapter.getFeaturePosts().get(mTopicFeedAdapter.getFeaturePosts().size() - 1).id);
    }
    request(request, new OnResponse2<TopicFeedResponse>() {
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
            mTopicFeedAdapter.showFeatureEmptyView(response.object.posts.lists.size() == 0);
          }
          mTopicFeedAdapter.add(TAB_FEATURE, response.object.posts.lists);
          mFeaturePageInfo = response.object.posts.page;
        }

        hideProgressBar();
        mAlvTopic.stopLoadMore();
      }
    }, TopicFeedResponse.class);
  }
}