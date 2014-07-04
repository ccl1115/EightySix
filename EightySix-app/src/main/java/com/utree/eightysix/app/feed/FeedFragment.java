package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.request.FeedsRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;

/**
 * @author simon
 */
public class FeedFragment extends BaseFragment {

  private FeedAdapter mFeedAdapter;
  private Circle mCircle;

  private Paginate.Page mPageInfo;

  private boolean mRefreshed;

  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null) return;
    PostActivity.start(getActivity(), (Post) item);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
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
        if (mRefreshed) {
          requestFeeds(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        } else {
          cacheOutFeeds(mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        }
        return true;
      }
    });

    cacheOutFeeds(1);
  }

  @Override
  public void onResume() {
    super.onResume();
    U.getBus().register(mLvFeed);
  }

  @Override
  public void onPause() {
    super.onPause();
    U.getBus().unregister(mLvFeed);
  }

  public Circle getCircle() {
    return mCircle;
  }

  public void setCircle(Circle circle) {

    if (circle == null || !circle.equals(mCircle)) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    mCircle = circle;

    if (mCircle == null) {
      if (U.useFixture()) {
        mCircle = U.getFixture(Circle.class, "valid");
      } else {
        mCircle = Env.getLastCircle();
      }
    }

    if (mCircle != null) {
      mCircle.selected = true;
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    if (isAdded()) {
      U.getBus().post(new AdapterDataSetChangedEvent());
      refresh();
    }
  }

  public void refresh() {
    requestFeeds(1);
  }

  private void requestFeeds(final int page) {
    if (mCircle == null) return;
    getBaseActivity().request(new FeedsRequest(mCircle.id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          if (page == 1) {
            mFeedAdapter = new FeedAdapter(response.object.posts.lists, response.object.showUnlock == 1);
            mLvFeed.setAdapter(mFeedAdapter);
          } else if (mFeedAdapter != null) {
            mFeedAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;
        } else {
          mLvFeed.setAdapter(null);
        }
        mLvFeed.stopLoadMore();
      }
    }, FeedsResponse.class);
  }

  private void cacheOutFeeds(final int page) {
    if (mCircle == null) return;
    getBaseActivity().showProgressBar();

    getBaseActivity().cacheOut(new FeedsRequest(mCircle.id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          if (page == 1) {
            mFeedAdapter = new FeedAdapter(response.object.posts.lists, response.object.showUnlock == 1);
            mLvFeed.setAdapter(mFeedAdapter);
          } else if (mFeedAdapter != null) {
            mFeedAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;

          mLvFeed.stopLoadMore();
          getBaseActivity().hideProgressBar();
        } else {
          requestFeeds(page);
        }
      }
    }, FeedsResponse.class);
  }
}
