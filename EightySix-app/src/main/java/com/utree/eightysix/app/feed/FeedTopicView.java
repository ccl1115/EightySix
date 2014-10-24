package com.utree.eightysix.app.feed;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.data.PostTopic;
import com.utree.eightysix.data.Tag;
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

  @InjectView(R.id.rb_more)
  public RoundedButton mRbMoreFeeds;
  private PostTopic mTopic;

  public FeedTopicView(Context context) {
    super(context);
    inflate(context, R.layout.item_feed_topic, this);
    ButterKnife.inject(this, this);
  }

  @OnClick (R.id.rb_more)
  public void onRbMoreClicked() {

  }

  @OnClick (R.id.rb_publish)
  public void onRbPublishClicked() {

  }


  public void setData(PostTopic topic) {
    if (topic == null) return;

    mTopic = topic;

    mTvHead.setText(topic.headTitle);
    mTvContent.setText(topic.postTopic.content);
    mRbMoreFeeds.setText(getContext().getString(R.string.display_more_feeds, topic.postTopic.postCount));

    List<Tag> tags = topic.postTopic.tags;
    for (int i = 0; i < tags.size(); i++) {
      Tag g = tags.get(i);
      switch (i) {
        case 0:
          mTvTag1.setText(g.content);
          break;
        case 1:
          mTvTag2.setText(g.content);
          break;
        case 2:
          mTvTag3.setText(g.content);
          break;
      }
    }
  }
}
