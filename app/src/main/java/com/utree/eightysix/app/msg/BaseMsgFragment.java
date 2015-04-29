/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.msg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.MsgsRequest;
import com.utree.eightysix.response.MsgsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.view.SwipeRefreshLayout;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;

/**
 */
public abstract class BaseMsgFragment extends BaseFragment {

  @InjectView(R.id.refresh_view)
  public SwipeRefreshLayout mRefreshLayout;

  @InjectView(R.id.alv_refresh)
  public AdvancedListView mAlvRefresh;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  protected MsgAdapter mMsgAdapter;

  private Paginate.Page mPageInfo;

  private boolean mRefreshed = true;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_base_msg, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mAlvRefresh.setLoadMoreCallback(new LoadMoreCallback() {
      @Override
      public View getLoadMoreView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_load_more, parent, false);
      }

      @Override
      public boolean hasMore() {
        return mPageInfo != null && (mPageInfo.currPage < mPageInfo.countPage);
      }

      @Override
      public boolean onLoadMoreStart() {
        if (mRefreshed) {
          requestMsgs(mPageInfo.currPage + 1);
        } else {
          cacheOutMsg(mPageInfo.currPage + 1);
        }
        return true;
      }
    });

    mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getBaseActivity().showRefreshIndicator(true);
        mRefreshed = true;
        requestMsgs(1);
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

    M.getRegisterHelper().register(mAlvRefresh);

    mRstvEmpty.setText(R.string.not_found_msg);
    mRstvEmpty.setSubText(R.string.not_found_msg_tip);
    mRstvEmpty.setDrawable(R.drawable.scene_3);

    // clear account msg new count
    Account.inst().setNewCommentCount(0);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (isActive()) {
      requestMsgs(1);
    }
  }

  @Override
  protected void onActive() {
    if (isAdded()) {
      requestMsgs(1);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    M.getRegisterHelper().unregister(mMsgAdapter);
  }

  protected abstract int getCreateType();

  private void requestMsgs(final int page) {
    if (page == 1) {
      mRefreshLayout.setRefreshing(true);
      getBaseActivity().showRefreshIndicator(true);
      ReadMsgStore.inst().clearRead();
    }
    getBaseActivity().request(new MsgsRequest(getCreateType(), page), new OnResponse2<MsgsResponse>() {
      @Override
      public void onResponse(MsgsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            M.getRegisterHelper().unregister(mMsgAdapter);
            mMsgAdapter = new MsgAdapter<CommentMsgItemView>(response.object.posts.lists) {
              @Override
              protected CommentMsgItemView newView(Context context) {
                return new CommentMsgItemView(context);
              }

              @Subscribe
              public void onMsgDeleteEvent(MsgDeleteEvent event) {
                remove(event.getPost());
              }
            };
            M.getRegisterHelper().register(mMsgAdapter);
            mAlvRefresh.setAdapter(mMsgAdapter);

            mAlvRefresh.setVisibility(View.VISIBLE);

            for (Post post : response.object.posts.lists) {
              if (post.read == 0) {
                break;
              }
            }
          } else {
            mMsgAdapter.add(response.object.posts.lists);
          }

          if (response.object.posts.page.countPage == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
            mAlvRefresh.setVisibility(View.GONE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
            mAlvRefresh.setVisibility(View.VISIBLE);
          }

          mPageInfo = response.object.posts.page;
        } else {
          cacheOutMsg(page);
        }
        getBaseActivity().hideProgressBar();
        getBaseActivity().hideRefreshIndicator();
        mAlvRefresh.stopLoadMore();
        mRefreshLayout.setRefreshing(false);
      }

      @Override
      public void onResponseError(Throwable e) {
        mRstvEmpty.setVisibility(View.VISIBLE);
        getBaseActivity().hideProgressBar();
        getBaseActivity().hideRefreshIndicator();
        mAlvRefresh.stopLoadMore();
        mRefreshLayout.setRefreshing(false);
      }
    }, MsgsResponse.class);
  }

  private void cacheOutMsg(final int page) {
    getBaseActivity().cacheOut(new MsgsRequest(getCreateType(), page), new OnResponse2<MsgsResponse>() {
      @Override
      public void onResponse(MsgsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            M.getRegisterHelper().unregister(mMsgAdapter);
            mMsgAdapter = new MsgAdapter<CommentMsgItemView>(response.object.posts.lists) {
              @Override
              protected CommentMsgItemView newView(Context context) {
                return new CommentMsgItemView(context);
              }

              @Subscribe
              public void onMsgDeleteEvent(MsgDeleteEvent event) {
                remove(event.getPost());
              }
            };
            M.getRegisterHelper().register(mMsgAdapter);
            mAlvRefresh.setAdapter(mMsgAdapter);


            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }

            for (Post post : response.object.posts.lists) {
              if (post.read == 0) {
                break;
              }
            }
          } else {
            mMsgAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;
          getBaseActivity().hideProgressBar();
          mAlvRefresh.stopLoadMore();
          mRefreshLayout.setRefreshing(false);
        } else {
          if (mMsgAdapter == null || mMsgAdapter.getCount() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onResponseError(Throwable e) {
        mRstvEmpty.setVisibility(View.VISIBLE);
        getBaseActivity().hideProgressBar();
        mAlvRefresh.stopLoadMore();
        mRefreshLayout.setRefreshing(false);
      }
    }, MsgsResponse.class);
  }
}
