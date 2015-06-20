package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.circle.event.CircleFollowsChangedEvent;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.app.snapshot.SnapshotActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Feeds;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.event.ListViewScrollStateIdledEvent;
import com.utree.eightysix.response.CircleIsFollowResponse;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 * @author simon
 */
public abstract class AbsFeedFragment extends BaseFragment {
  @InjectView(R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView(R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  protected FeedAdapter mFeedAdapter;
  protected Circle mCircle;
  protected Paginate.Page mPageInfo;

  protected boolean mPostPraiseRequesting;
  private int mCurrent;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) requestFeeds(mCircle.id, 1);
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
        if (mPageInfo != null) {
          onLoadMore(mPageInfo.currPage + 1);
        }
        requestFeeds(mCircle.id, mPageInfo == null ? 1 : mPageInfo.currPage + 1);
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
          if (mCircle != null) {
            requestFeeds(mCircle.id, 1);
          } else {
            requestFeeds(0, 1);
          }
        }
      }

      @Override
      public void onDrag(int value) {
        getBaseActivity().showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        getBaseActivity().hideRefreshIndicator();
      }
    });

    mRefresherView.setColorSchemeResources(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    mLvFeed.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
          U.getBus().post(new ListViewScrollStateIdledEvent());

          if (view.getChildCount() <= 2) return;

          final int firstItem = mLvFeed.getFirstVisiblePosition() + 1;

          if (mFeedAdapter.tipsShowing() || mFeedAdapter.getItemViewType(firstItem) != FeedAdapter.TYPE_POST) return;

          Post post = (Post) mFeedAdapter.getItem(firstItem);


          if ((post.viewType == 1 || post.viewType == 2 || post.viewType == 5) && Env.firstRun("overlay_tip_source")) {
            mFeedAdapter.showTipSource(firstItem);
          } else if (Env.firstRun("overlay_tip_praise")) {
            mFeedAdapter.showTipPraise(firstItem);
          }
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

      }
    });

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

  public Circle getCircle() {
    return mCircle;
  }

  public void setCircle(int id) {
    if (mCircle != null && mCircle.id != id) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
      mCircle.id = id;
    } else {
      mCircle = new Circle();
      mCircle.id = id;
    }
  }

  public int getFriendCount() {
    if (mCircle != null) {
      return mCircle.friendCount;
    } else {
      return 0;
    }
  }

  public void setCircle(Circle circle) {
    if (circle == null || !circle.equals(mCircle)) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);
    }

    mCircle = circle;
  }

  @Override
  protected void onActive() {
    if (mCircle != null) {
      if (mLvFeed != null) mLvFeed.setAdapter(null);

      if (isAdded()) {
        requestFeeds(mCircle.id, 1);
      }
    } else {
      if (isActive()) {
        requestFeeds(0, 1);
      }
    }
  }

  public int getCircleId() {
    return mCircle == null ? 0 : mCircle.id;
  }

  public void refresh() {
    getBaseActivity().showProgressBar();
    if (mCircle != null) {
      if (isAdded()) {
        requestFeeds(mCircle.id, 1);
      }
    } else {
      if (isAdded()) {
        requestFeeds(0, 1);
      }
    }
  }


  public int getWorkerCount() {
    if (mFeedAdapter != null && mFeedAdapter.getFeeds() != null) {
      return mFeedAdapter.getFeeds().workerCount;
    } else {
      return 0;
    }
  }

  protected abstract void requestFeeds(final int id, final int page);

  protected void responseForRequest(int circleId, FeedsResponse response, int page) {
    if (RESTRequester.responseOk(response)) {
      if (page == 1) {
        mCircle = response.object.circle;

        mCurrent = response.object.current;

        U.getBus().post(mCircle);

        mFeedAdapter = new FeedAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.GONE);
        }


        ((FeedActivity) getBaseActivity()).setTitle(mCircle);
        getBaseActivity().setTopSubTitle(String.format(getString(R.string.friends_info),
            mCircle.friendCount, response.object.workerCount));
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }
      mPageInfo = response.object.posts.page;

      ((FeedActivity) getBaseActivity()).mSend.setImageResource(response.object.lock != 1 || response.object.current == 1 ?
          R.drawable.ic_post_pen : R.drawable.ic_post_pen_disabled);

      updateTopBar();

      FetchNotificationService.setCircleId(mCircle.id);
    } else {
      cacheOutFeeds(circleId, page);
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  private void updateTopBar() {
    getBaseActivity().getTopBar().getAbRight()
        .setDrawable(getResources().getDrawable(R.drawable.ic_action_overflow));

    getBaseActivity().getTopBar().getAbRight()
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                showMenuDialog();
              }
            }
        );
  }

  protected abstract void cacheOutFeeds(final int id, final int page);

  protected void responseForCache(FeedsResponse response, int page, int id) {
    if (response != null && response.code == 0 && response.object != null) {
      if (page == 1) {
        mCircle = response.object.circle;

        mCurrent = response.object.current;

        U.getBus().post(mCircle);

        if (response.object.posts.lists.size() == 0) {
          mRstvEmpty.setVisibility(View.VISIBLE);
        } else {
          mRstvEmpty.setVisibility(View.INVISIBLE);
        }

        mFeedAdapter = new FeedAdapter(response.object);
        M.getRegisterHelper().register(mFeedAdapter);
        mLvFeed.setAdapter(mFeedAdapter);

        ((FeedActivity) getBaseActivity()).setTitle(mCircle);
      } else if (mFeedAdapter != null) {
        mFeedAdapter.add(response.object.posts.lists);
      }
      mPageInfo = response.object.posts.page;

      ((FeedActivity) getBaseActivity()).mSend.setImageResource(response.object.lock != 1 || response.object.current == 1 ?
          R.drawable.ic_post_pen : R.drawable.ic_post_pen_disabled);

      FetchNotificationService.setCircleId(mCircle.id);
    } else {
      if (mFeedAdapter != null && mFeedAdapter.getCount() == 0) {
        mRstvEmpty.setVisibility(View.VISIBLE);
      }
      if (mCircle != null) {
        getBaseActivity().setTopTitle(mCircle.shortName);
      }
      getBaseActivity().setTopSubTitle("");

      mPageInfo = null;
    }
    mRefresherView.setRefreshing(false);
    mLvFeed.stopLoadMore();
    getBaseActivity().hideProgressBar();
    getBaseActivity().hideRefreshIndicator();
  }

  protected abstract void onPullRefresh();

  protected abstract void onLoadMore(int page);

  public boolean canPublish() {
    if (mFeedAdapter != null) {
      Feeds feeds = mFeedAdapter.getFeeds();
      if (feeds != null) return feeds.current == 1 || feeds.lock == 0;
    }
    return false;
  }

  private void showMenuDialog() {
    getBaseActivity().showProgressBar(true);

    U.request("follow_circle_followed", new OnResponse2<CircleIsFollowResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideProgressBar();
      }

      @Override
      public void onResponse(final CircleIsFollowResponse response) {
        if (RESTRequester.responseOk(response)) {
          String follow = response.object.followed == 1 ? "取消关注" : "关注";

          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

          builder.setTitle("操作");

          if (mCircle.snapshot == 1) {
            if (mCurrent == 1) {
              builder.setItems(new String[]{"快照"},
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      switch (which) {
                        case 0:
                          SnapshotActivity.start(getBaseActivity(), mCircle);
                          break;
                      }
                    }
                  });
            } else {
              builder.setItems(new String[]{"快照", follow},
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      switch (which) {
                        case 0:
                          SnapshotActivity.start(getBaseActivity(), mCircle);
                          break;
                        case 1:
                          followCircleItem(response);
                          break;
                      }
                    }
                  });
            }
          } else {
            if (mCurrent == 1) {
              getTopBar().getAbRight().hide();
              getBaseActivity().hideProgressBar();
              return;
            } else {
              builder.setItems(new String[]{follow},
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      switch (which) {
                        case 0:
                          followCircleItem(response);
                          break;
                      }
                    }
                  });

            }
          }
          builder.show();
        }
        getBaseActivity().hideProgressBar();
      }
    }, CircleIsFollowResponse.class, mCircle.id);
  }

  public void followCircleItem(CircleIsFollowResponse response) {
    if (response.object.followed == 1) {
      U.request("follow_circle_del", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {
          U.getBus().post(new CircleFollowsChangedEvent());
        }
      }, Response.class, mCircle.id);
    } else if (response.object.followed == 0) {
      U.request("follow_circle_add", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {
          if (RESTRequester.responseOk(response)) {
            U.getBus().post(new CircleFollowsChangedEvent());
          }
        }
      }, Response.class, mCircle.id);
    }
  }

}
