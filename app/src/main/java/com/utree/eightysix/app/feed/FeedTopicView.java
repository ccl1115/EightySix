package com.utree.eightysix.app.feed;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.publish.PublishActivity;
import com.utree.eightysix.app.topic.TopicActivity;
import com.utree.eightysix.app.topic.TopicListActivity;
import com.utree.eightysix.data.PostTopic;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.data.Topic;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;

/**
 */
public class FeedTopicView extends FrameLayout {

  @InjectView(R.id.tv_content)
  public TextView mTvContent;

  @InjectView(R.id.tv_tag_1)
  public TextView mTvTag1;

  @InjectView(R.id.tv_tag_2)
  public TextView mTvTag2;

  @InjectView(R.id.v_mask)
  public View mVMask;

  @InjectView(R.id.rb_more)
  public RoundedButton mRbMoreFeeds;

  @InjectView(R.id.ll_parent)
  public LinearLayout mLlParent;

  @InjectView(R.id.aiv_bg)
  public AsyncImageView mAivBg;

  private PostTopic mTopic;

  public FeedTopicView(Context context) {
    super(context);
    inflate(context, R.layout.item_feed_topic, this);
    setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, U.dp2px(190)));
    ButterKnife.inject(this, this);

    setPadding(0, U.dp2px(3), 0, 0);

    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        U.getAnalyser().trackEndEvent(getContext(), "feed_topic_view", "feed_topic_view");

        TopicListActivity.start(getContext());
        TopicActivity.start(getContext(), getTopic());
      }
    });
  }

  @OnClick(R.id.rb_more)
  public void onRbMoreClicked() {
    U.getAnalyser().trackEndEvent(getContext(), "feed_topic_view", "feed_topic_view");

    TopicListActivity.start(getContext());
    TopicActivity.start(getContext(), getTopic());
  }

  @OnClick(R.id.rb_publish)
  public void onRbPublishClicked() {
    U.getAnalyser().trackEvent(getContext(), "feed_topic_publish", "feed_topic_publish");

    if (Account.inst().getCurrentCircle() != null) {
      TopicListActivity.start(getContext());
      TopicActivity.start(getContext(), getTopic());
      PublishActivity.startWithTopicId(getContext(), mTopic.id, mTopic.tags, mTopic.hint);
    } else {
      U.showToast("还没有在职工厂，不能发话题帖哦");
    }
  }

  @OnClick(R.id.tv_tag_1)
  public void onTvTag1Clicked() {
    FeedsSearchActivity.start(getContext(), mTopic.tags.get(0).content);
  }

  @OnClick(R.id.tv_tag_2)
  public void onTvTag2Clicked() {
    FeedsSearchActivity.start(getContext(), mTopic.tags.get(1).content);
  }

  public void setData(PostTopic topic) {
    if (topic == null) return;

    mTopic = topic;

    mTvContent.setText(topic.content);

    if (TextUtils.isEmpty(topic.content)) {
      mTvContent.setVisibility(GONE);
      if (TextUtils.isEmpty(topic.title)) {
        mVMask.setVisibility(GONE);
      } else {
        mVMask.setVisibility(VISIBLE);
      }
    } else {
      mVMask.setVisibility(VISIBLE);
      mTvContent.setVisibility(VISIBLE);
    }

    if (TextUtils.isEmpty(mTopic.bgUrl)) {
      mAivBg.setUrl(null);
      mLlParent.setBackgroundColor(ColorUtil.strToColor(topic.bgColor));
    } else {
      mAivBg.setUrl(mTopic.bgUrl);
      mLlParent.setBackgroundColor(Color.TRANSPARENT);
    }

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
