package com.utree.eightysix.app.hometown;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.utree.eightysix.app.feed.FeedAdapter;
import com.utree.eightysix.app.hometown.event.HometownNotSetEvent;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.response.FeedsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.TopBar;

/**
 */
public class AbsHometownFeedsFragment extends BaseFragment {

  @InjectView(R.id.lv_feed)
  public AdvancedListView mLvFeed;

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefresherView;

  @InjectView (R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  private Paginate.Page mPageInfo;

  protected FeedAdapter mFeedAdapter;

  protected int mTabType;

  private Integer mHometownId;

  private Integer mHometownType = -1;

  @OnItemClick (R.id.lv_feed)
  public void onLvFeedItemClicked(int position, View view) {
    Object item = mLvFeed.getAdapter().getItem(position);
    if (item == null || !(item instanceof Post)) return;
    PostActivity.start(getActivity(), (Post) item);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) requestFeeds(1);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    if (mFeedAdapter != null) {
      M.getRegisterHelper().unregister(mFeedAdapter);
    }
  }

  @Override
  protected void onActive() {
    super.onActive();

    if (mFeedAdapter != null) {
      mLvFeed.setAdapter(null);
    }

    requestFeeds(1);
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mRefresherView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        if (isAdded()) {
          requestFeeds(1);
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

    mLvFeed.setLoadMoreCallback(new LoadMoreCallback() {
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
        requestFeeds(mPageInfo.currPage + 1);
        return true;
      }
    });
  }

  public void setHometown(int hometownId, int hometownType) {
    mHometownId = hometownId;
    mHometownType = hometownType;
    requestFeeds(1);
  }

  protected void requestFeeds(final int page) {
    if (getBaseActivity() == null) return;
    mRefresherView.setRefreshing(true);
    getBaseActivity().showRefreshIndicator(true);
    U.request("get_hometown_feeds", new OnResponse2<FeedsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        mRefresherView.setRefreshing(false);
        getBaseActivity().hideRefreshIndicator();
        mLvFeed.stopLoadMore();
      }

      @Override
      public void onResponse(FeedsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object.hometown == 0) {
            U.getBus().post(new HometownNotSetEvent());
          }

          if (page == 1) {
            if (mFeedAdapter != null) {
              M.getRegisterHelper().unregister(mFeedAdapter);
            }
            mFeedAdapter = new FeedAdapter(response.object);
            M.getRegisterHelper().register(mFeedAdapter);
            mLvFeed.setAdapter(mFeedAdapter);

            if (TextUtils.isEmpty(response.object.hometownName)) {
              getBaseActivity().setTopTitle("老乡动态");
            } else {
              getBaseActivity().setTopTitle(String.format("老乡动态(%s)", response.object.hometownName));
            }
            getBaseActivity().setTopSubTitle(response.object.subInfo);
            getBaseActivity().setTopBarClickMode(TopBar.TITLE_CLICK_MODE_DIVIDE);

            mHometownId = response.object.hometownId;
            mHometownType = response.object.hometownType;
          } else {
            mFeedAdapter.add(response.object.posts.lists);
          }

          if (response.object.posts.lists.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
          }

        }

        mPageInfo = response.object.posts.page;

        mRefresherView.setRefreshing(false);
        getBaseActivity().hideRefreshIndicator();
        mLvFeed.stopLoadMore();
      }
    }, FeedsResponse.class,
        page,
        mTabType,
        mHometownId,
        mHometownType);
  }
}
