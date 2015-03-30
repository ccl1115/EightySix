/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.tag.AllTagsActivity;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.response.TagsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.view.RandomFloatingLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.RoundedButton;

import java.util.List;
import java.util.Random;

/**
 */
@Layout(R.layout.activity_feeds_search)
public class FeedsSearchActivity extends BaseActivity {

  public static void start(Context context, String tag) {
    Intent intent = new Intent(context, FeedsSearchActivity.class);

    intent.putExtra("tag", tag);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.alv_feeds)
  public AdvancedListView mAlvFeeds;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  @InjectView(R.id.rfl_tags)
  public RandomFloatingLayout mRflTags;

  @InjectView(R.id.ll_tags)
  public LinearLayout mLlTags;

  private FeedsSearchAdapter mFeedsSearchAdapter;

  private int mCurrent;

  private Paginate.Page mPageInfo;

  private String mSearchContent;

  @OnClick(R.id.rb_all_tags)
  public void onRbAllTagsClicked() {
    startActivity(new Intent(this, AllTagsActivity.class));
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mRstvEmpty.setDrawable(R.drawable.scene_5);
    mRstvEmpty.setText("没有搜索结果");

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));
    getTopBar().enterSearch();

    getTopBar().getSpinner().setVisibility(View.VISIBLE);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item,
        new String[]{"全部", "在职"});

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    getTopBar().getSpinner().setAdapter(adapter);

    getTopBar().getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
          mCurrent = 0;
        } else if (position == 1) {
          mCurrent = 1;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mAlvFeeds.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && mPageInfo.currPage < mPageInfo.countPage;
      }

      @Override
      public boolean onLoadMoreStart() {
        requestFeedsSearch(mPageInfo.currPage + 1);
        return true;
      }
    });

    requestTags();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    mSearchContent = "#" + intent.getStringExtra("tag");
    getTopBar().mEtSearch.setText(mSearchContent);
    requestFeedsSearch(1);
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

  @Override
  public void onActionSearchClicked(CharSequence cs) {
    hideSoftKeyboard(mAlvFeeds);
    mSearchContent = cs.toString();
    requestFeedsSearch(1);
  }

  @Override
  public void onSearchTextChanged(CharSequence cs) {
    if (cs.length() > 0) {
      mLlTags.setVisibility(View.GONE);
    } else {
      mLlTags.setVisibility(View.VISIBLE);
      mRstvEmpty.setVisibility(View.GONE);
      mAlvFeeds.setAdapter(null);
    }
  }

  private void requestFeedsSearch(final int page) {
    showProgressBar(true);
    U.request("feeds_search", new OnResponse2<FeedsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(FeedsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
              mAlvFeeds.setAdapter(null);
            } else {
              mFeedsSearchAdapter = new FeedsSearchAdapter(response.object.posts.lists);
              mAlvFeeds.setAdapter(mFeedsSearchAdapter);
              mRstvEmpty.setVisibility(View.GONE);
            }
          } else {
            mFeedsSearchAdapter.add(response.object.posts.lists);
          }
        }
        mPageInfo = response.object.posts.page;

        mLlTags.setVisibility(View.GONE);
        hideProgressBar();
        mAlvFeeds.stopLoadMore();
      }
    }, FeedsResponse.class, mSearchContent, null, mCurrent, page);
  }

  private void requestTags() {
    showProgressBar(true);
    U.request("tags_and_keywords", new OnResponse2<TagsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
      }

      @Override
      public void onResponse(TagsResponse response) {
        if (RESTRequester.responseOk(response)) {
          mRflTags.setAdapter(new TagsAdapter(response.object.tags));
        }
        hideProgressBar();
      }
    }, TagsResponse.class);
  }


  public static class TagsAdapter extends BaseAdapter {

    private List<Tag> mTags;
    private Random mRandom = new Random();

    public TagsAdapter(List<Tag> tags) {
      mTags = tags;
    }

    @Override
    public int getCount() {
      return mTags.size();
    }

    @Override
    public Tag getItem(int position) {
      return mTags.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      RoundedButton btn = new RoundedButton(parent.getContext());

      final String content = mTags.get(position).content;
      int padding = U.dp2px(4);
      btn.setText(content);
      btn.setTextSize(10 + mRandom.nextInt(10));
      btn.setBackgroundColor(ColorUtil.getRandomColor());
      btn.setPadding(padding << 2, padding, padding << 2, padding);

      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          FeedsSearchActivity.start(v.getContext(), content);
        }
      });

      return btn;
    }
  }
}