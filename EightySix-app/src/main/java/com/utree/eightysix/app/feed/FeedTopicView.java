package com.utree.eightysix.app.feed;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.tag.TagTabActivity;
import com.utree.eightysix.app.topic.TopicActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.data.PostTopic;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.RoundedButton;
import java.util.List;

/**
 */
public class FeedTopicView extends FrameLayout {

  @InjectView (R.id.tv_head)
  public TextView mTvHead;

  @InjectView (R.id.tv_more)
  public TextView mTvMore;

  @InjectView (R.id.tv_content)
  public TextView mTvContent;

  @InjectView (R.id.tv_tag_1)
  public TextView mTvTag1;

  @InjectView (R.id.tv_tag_2)
  public TextView mTvTag2;

  @InjectView (R.id.tv_tag_3)
  public TextView mTvTag3;

  @InjectView (R.id.rb_more)
  public RoundedButton mRbMoreFeeds;

  @InjectView (R.id.ll_parent)
  public LinearLayout mLlParent;

  private PostTopic mTopic;

  public FeedTopicView(Context context) {
    super(context);
    inflate(context, R.layout.item_feed_topic, this);
    ButterKnife.inject(this, this);

    setPadding(U.dp2px(8), U.dp2px(8), U.dp2px(8), 0);
  }

  @OnClick (R.id.rb_more)
  public void onRbMoreClicked() {
    U.getAnalyser().trackEndEvent(getContext(), "feed_topic_view", "feed_topic_view");

    TopicListActivity.start(getContext());
    TopicActivity.start(getContext(), getTopic());
  }

  @OnClick (R.id.rb_publish)
  public void onRbPublishClicked() {
    U.getAnalyser().trackEvent(getContext(), "feed_topic_publish", "feed_topic_publish");

    TopicActivity.start(getContext(), getTopic());
    PublishActivity.startWithTopicId(getContext(), mTopic.id, mTopic.tags);
  }

  @OnClick (R.id.tv_more)
  public void onTvMoreClicked() {
    U.getAnalyser().trackEvent(getContext(), "feed_topic_more", "feed_topic_more");
    TopicListActivity.start(getContext());
  }

  @OnClick (R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    TagTabActivity.start(getContext(), mTopic.tags.get(0));
  }

  @OnClick (R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    TagTabActivity.start(getContext(), mTopic.tags.get(1));
  }

  @OnClick (R.id.tv_tag_3)
  public void onTvTag3Clicked() {
    TagTabActivity.start(getContext(), mTopic.tags.get(2));
  }

  public void setData(PostTopic topic) {
    if (topic == null) return;

    mTopic = topic;

    mTvHead.setText(topic.headTitle);
    mTvContent.setText(topic.content);

    mLlParent.setBackgroundColor(ColorUtil.strToColor(topic.bgColor));

    if (topic.postCount > 999) {
      mRbMoreFeeds.setText(getContext().getString(R.string.display_more_feeds, "999+"));
    } else {
      mRbMoreFeeds.setText(getContext().getString(R.string.display_more_feeds,
          String.valueOf(topic.postCount)));
    }

    List<Tag> tags = topic.tags;
    for (int i = 0; i < tags.size(); i++) {
      Tag g = tags.get(i);
      switch (i) {
        case 0:
          mTvTag1.setText("#" + g.content);
          break;
        case 1:
          mTvTag2.setText("#" + g.content);
          break;
        case 2:
          mTvTag3.setText("#" + g.content);
          break;
      }
    }
  }

  protected Topic getTopic() {
    Topic topic = new Topic();
    topic.content = mTopic.content;
    topic.id = mTopic.id;
    topic.postCount = mTopic.postCount;
    topic.tags = mTopic.tags;
    topic.bgColor = mTopic.bgColor;
    topic.bgUrl = mTopic.bgUrl;
    return topic;
  }
}
