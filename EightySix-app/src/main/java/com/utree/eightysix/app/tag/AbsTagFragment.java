package com.utree.eightysix.app.tag;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.data.Tag;
import com.utree.eightysix.response.TagFeedsResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
public abstract class AbsTagFragment extends BaseFragment {
  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView(R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  protected TagFeedAdapter mFeedAdapter;
  protected Paginate.Page mPageInfo;

  private Tag mTag;

  protected boolean mPostPraiseRequesting;

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position, View view) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null || !(item instanceof Post)) return;
    PostActivity.start(getActivity(), (Post) item);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) requestFeeds(mTag.id, 1);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable("tag", mTag);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);

    if (savedInstanceState != null) {
      mTag = savedInstanceState.getParcelable("tag");
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);

    mLvFeed.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return (mPageInfo != null) && (mPageInfo.currPage < mPageInfo.countPage);
      }

      @Override
      public boolean onLoadMoreStart() {
        if (mPageInfo != null) {
          onLoadMore(mPageInfo.currPage + 1);
        }
        requestFeeds(mTag.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        return true;
      }
    });

    M.getRegisterHelper().register(mLvFeed);

    mRefresherView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        onPullRefresh();
        if (isAdded()) {
          if (mTag != null) {
            requestFeeds(mTag.id, 1);
          } else {
            requestFeeds(0, 1);
          }
        }
      }

      @Override
      public void onDrag() {
        getBaseActivity().showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        getBaseActivity().hideRefreshIndicator();
      }
    });

    mRefresherView.setColorSchemeResources(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    getBaseActivity().showRefreshIndicator(true);
    mRefresherView.setRefreshing(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (mLvFeed != null) {
      M.getRegisterHelper().unregister(mLvFeed);
    }

    if (mFeedAdapter != null) {
      M.getRegisterHelper().unregister(mFeedAdapter);
    }
  }

  public void setTag(int id) {
    if (mTag != null && mTag.id != id) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
      mTag.id = id;
      refresh();
    } else {
      mTag = new Tag();
      mTag.id = id;
    }
  }

  public void setTag(Tag tag) {
    if (tag != null && !tag.equals(mTag)) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
      mTag = tag;
      refresh();
    }

  }

  @Override
  protected void onActive() {
    if (mTag != null) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        requestFeeds(mTag.id, 1);
      }
    }
  }

  public int getTagId() {
    return mTag == null ? 0 : mTag.id;
  }

  public void refresh() {
    if (mTag != null) {
      if (isAdded()) {
        requestFeeds(mTag.id, 1);
      }
    }
  }


  protected abstract void requestFeeds(final int id, final int page);

  protected void responseForRequest(int circleId, TagFeedsResponse response, int page) {
    if (RESTRequester.responseOk(response)) {
      if (page == 1) {
        mFeedAdapter = new TagFeedAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
        }

        U.getBus().post(new CurrentCircleResponseEvent(response.object.circle));

        U.getBus().post(new TagResponseEvent(new Tag(response.object.tagId, response.object.tagName)));
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }
      mPageInfo = response.object.posts.page;

    } else {
      cacheOutFeeds(circleId, page);
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void cacheOutFeeds(final int id, final int page);

  protected void responseForCache(TagFeedsResponse response, int page, int id) {
    if (response != null && response.code == 0 && response.object != null) {
      if (page == 1) {
        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.INVISIBLE);
        }

        mFeedAdapter = new TagFeedAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }
      mPageInfo = response.object.posts.page;
    } else {
      if (mFeedAdapter != null && mFeedAdapter.getCount() == 0) {
        mRstvEmpty.setVisibility(View.VISIBLE);
      }

      mPageInfo = null;
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void onPullRefresh();

  protected abstract void onLoadMore(int page);

}
