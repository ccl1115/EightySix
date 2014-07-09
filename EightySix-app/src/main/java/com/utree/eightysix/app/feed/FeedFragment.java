package com.utree.eightysix.app.feed;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.event.FeedPostPraiseEvent;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.AdapterDataSetChangedEvent;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.request.FeedsRequest;
import com.utree.eightysix.request.PostPraiseCancelRequest;
import com.utree.eightysix.request.PostPraiseRequest;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.EmotionOnRefreshListener;
import com.utree.eightysix.widget.FontPortraitView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RefresherView;
import java.util.Iterator;

/**
 * @author simon
 */
class FeedFragment extends BaseFragment {

  @InjectView (R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView (R.id.refresh_view)
  public RefresherView mRefresherView;

  @InjectView (R.id.fpv_head)
  public FontPortraitView mFontPortraitView;

  private FeedAdapter mFeedAdapter;
  private Circle mCircle;
  private Paginate.Page mPageInfo;
  private boolean mRefreshed;

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position, View view) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null) return;
    Rect rect = new Rect();
    int[] xy = new int[2];
    View target= view.findViewById(R.id.tv_content);
    target.getLocationInWindow(xy);
    rect.left = xy[0];
    rect.top = xy[1];
    rect.right = rect.left + target.getMeasuredWidth();
    rect.bottom = rect.top + target.getMeasuredHeight();
    PostActivity.start(getActivity(), (Post) item, rect);
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
          requestFeeds(mCircle.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        } else {
          cacheOutFeeds(mCircle.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
        }
        return true;
      }
    });

    U.getBus().register(mLvFeed);

    mRefresherView.setOnRefreshListener(new EmotionOnRefreshListener(mFontPortraitView) {
      @Override
      public void onRefreshData() {
      }

      @Override
      public void onPreRefresh() {
        super.onPreRefresh();
        mRefreshed = true;
        requestFeeds(mCircle.id, 1);
      }
    });

    mLvFeed.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
          U.getBus().post(new ListViewScrollStateIdledEvent());
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (mLvFeed != null) {
      U.getBus().unregister(mLvFeed);
    }

    if (mFeedAdapter != null) {
      U.getBus().unregister(mFeedAdapter);
    }
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
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        cacheOutFeeds(mCircle.id, 1);
      }
    }
  }

  public void setCircle(int id) {
    if (mCircle.id != id) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    if (isAdded()) {
      cacheOutFeeds(id, 1);
    }
  }

  public int getCurrFriends() {
    return mFeedAdapter.getFeeds().currFactoryFriends;
  }

  public void refresh() {
    requestFeeds(mCircle.id, 1);
  }

  @Subscribe
  public void onFeedPostPraiseEvent(final FeedPostPraiseEvent event) {
    if (event.isCancel()) {
      getBaseActivity().request(new PostPraiseCancelRequest(event.getPost().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (response == null || response.code != 0) {
            event.getPost().praised = 1;
            event.getPost().praise++;
            mFeedAdapter.notifyDataSetChanged();
          }
        }
      }, Response.class);
    } else {
      getBaseActivity().request(new PostPraiseRequest(event.getPost().id), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {
          if (response == null || response.code != 0) {
            event.getPost().praised = 0;
            event.getPost().praise--;
            mFeedAdapter.notifyDataSetChanged();
          }
        }
      }, Response.class);
    }
  }

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    if (mFeedAdapter == null || mFeedAdapter.getFeeds() == null) return;
    for (Iterator<Post> iterator = mFeedAdapter.getFeeds().posts.lists.iterator(); iterator.hasNext(); ) {
      Post p = iterator.next();
      if (p == null) continue;
      if (p.equals(event.getPost())) {
        iterator.remove();
        mFeedAdapter.notifyDataSetChanged();
        break;
      }
    }
  }

  private void requestFeeds(int id, final int page) {
    if (page == 1) getBaseActivity().showProgressBar();

    getBaseActivity().request(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          if (page == 1) {
            mCircle = response.object.circle;
            U.getBus().post(mCircle);
            mFeedAdapter = new FeedAdapter(response.object);
            U.getBus().register(mFeedAdapter);
            mLvFeed.setAdapter(mFeedAdapter);
            mRefresherView.hideHeader();
          } else if (mFeedAdapter != null) {
            mFeedAdapter.add(response.object.posts.lists);
          }
          ((FeedActivity)getBaseActivity()).setMyPraiseCount(response.object.myPraiseCount);
          mPageInfo = response.object.posts.page;
        } else {
          mLvFeed.setAdapter(null);
        }
        mLvFeed.stopLoadMore();
        getBaseActivity().hideProgressBar();
      }
    }, FeedsResponse.class);
  }

  private void cacheOutFeeds(final int id, final int page) {
    if (page == 1) getBaseActivity().showProgressBar();

    getBaseActivity().cacheOut(new FeedsRequest(id, page), new OnResponse<FeedsResponse>() {
      @Override
      public void onResponse(FeedsResponse response) {
        if (response != null && response.code == 0 && response.object != null) {
          if (page == 1) {
            mCircle = response.object.circle;
            U.getBus().post(mCircle);
            mFeedAdapter = new FeedAdapter(response.object);
            U.getBus().register(mFeedAdapter);
            mLvFeed.setAdapter(mFeedAdapter);
          } else if (mFeedAdapter != null) {
            mFeedAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;
          mLvFeed.stopLoadMore();
          ((FeedActivity)getBaseActivity()).setMyPraiseCount(response.object.myPraiseCount);
          getBaseActivity().hideProgressBar();
        } else {
          requestFeeds(id, page);
        }
      }
    }, FeedsResponse.class);
  }

}
