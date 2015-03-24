/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.AsyncImageView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FeedsSearchAdapter extends BaseAdapter {

  private List<BaseItem[]> mFeeds;

  public FeedsSearchAdapter(List<BaseItem> feeds) {
    if (feeds.size() % 2 != 0) {
      feeds.add(null);
    }

    mFeeds = new ArrayList<BaseItem[]>(feeds.size() >> 1);

    for (int i = 0, size = feeds.size() >> 1; i < size; i += 2) {
      mFeeds.add(new BaseItem[]{feeds.get(i), feeds.get(i + 1)});
    }
  }

  public void add(List<BaseItem> feeds) {
    if (feeds.size() % 2 != 0) {
      feeds.add(null);
    }

    for (int i = 0, size = feeds.size() >> 1; i < size; i += 2) {
      mFeeds.add(new BaseItem[]{feeds.get(i), feeds.get(i + 1)});
    }

    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mFeeds.size();
  }

  @Override
  public BaseItem[] getItem(int position) {
    return mFeeds.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_post, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.setData(getItem(position));

    return convertView;
  }

  public static class ViewHolder {

    @InjectView(R.id.tv_left_tag_1)
    public TextView mTvLeftTag1;

    @InjectView(R.id.tv_left_tag_2)
    public TextView mTvLeftTag2;

    @InjectView(R.id.tv_content_left)
    public TextView mTvContentLeft;

    @InjectView(R.id.aiv_bg_left)
    public AsyncImageView mAivBgLeft;

    @InjectView(R.id.tv_left_source)
    public TextView mTvSourceLeft;

    @InjectView(R.id.tv_left_praise)
    public TextView mTvPraiseLeft;

    @InjectView(R.id.tv_left_reply)
    public TextView mTvReplyLeft;


    @InjectView(R.id.tv_right_tag_1)
    public TextView mTvRightTag1;

    @InjectView(R.id.tv_right_tag_2)
    public TextView mTvRightTag2;

    @InjectView(R.id.tv_content_right)
    public TextView mTvContentRight;

    @InjectView(R.id.aiv_bg_right)
    public AsyncImageView mAivBgRight;

    @InjectView(R.id.tv_right_source)
    public TextView mTvSourceRight;

    @InjectView(R.id.tv_right_praise)
    public TextView mTvPraiseRight;

    @InjectView(R.id.tv_right_reply)
    public TextView mTvReplyRight;

    @InjectView(R.id.fl_left)
    public FrameLayout mFlLeft;

    @InjectView(R.id.fl_right)
    public FrameLayout mFlRight;

    public void setData(BaseItem[] posts) {
      Post left = (Post) posts[0];

      if (left != null) {
        mFlLeft.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(left.bgUrl)) {
          mAivBgLeft.setVisibility(View.INVISIBLE);
          mFlLeft.setBackgroundColor(ColorUtil.strToColor(left.bgColor));
          mAivBgLeft.setUrl(null);
        } else {
          mFlLeft.setBackgroundColor(Color.WHITE);
          mAivBgLeft.setVisibility(View.VISIBLE);
          mAivBgLeft.setUrl(left.bgUrl);
        }

        mTvContentLeft.setText(left.content);
        mTvSourceLeft.setText(left.shortName);
        mTvReplyLeft.setText(String.valueOf(left.comments));
        mTvPraiseLeft.setText(String.valueOf(left.praise));
      } else {
        mFlLeft.setVisibility(View.INVISIBLE);
      }

      Post right = (Post) posts[1];

      if (right != null) {
        mFlRight.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(right.bgUrl)) {
          mAivBgRight.setVisibility(View.INVISIBLE);
          mFlRight.setBackgroundColor(ColorUtil.strToColor(right.bgColor));
          mAivBgRight.setUrl(null);
        } else {
          mFlRight.setBackgroundColor(Color.WHITE);
          mAivBgRight.setVisibility(View.VISIBLE);
          mAivBgRight.setUrl(right.bgUrl);
        }

        mTvContentRight.setText(right.content);
        mTvSourceRight.setText(right.shortName);
        mTvReplyRight.setText(String.valueOf(right.comments));
        mTvPraiseRight.setText(String.valueOf(right.praise));
      } else {
        mFlRight.setVisibility(View.INVISIBLE);
      }
    }

    public ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
