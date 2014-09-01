package com.utree.eightysix.app.msg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
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
 * @author simon
 */
@Layout (R.layout.activity_msg)
@TopTitle (R.string.messages)
public class MsgActivity extends BaseActivity {

  private static final int MSG_ANIMATE = 0x1;

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRvMsg;

  @InjectView (R.id.tv_no_new_msg)
  public TextView mTvNoNewMsg;

  @InjectView (R.id.tv_empty_text)
  public RandomSceneTextView mRstvEmpty;

  @InjectView (R.id.alv_refresh)
  public AdvancedListView mAlvMsg;

  private MsgAdapter mMsgAdapter;
  private Paginate.Page mPageInfo;

  private boolean mRefreshed;

  public static void start(Context context, boolean refresh) {
    Intent intent = new Intent(context, MsgActivity.class);
    intent.putExtra("refresh", refresh);
    context.startActivity(intent);
  }

  public static Intent getIntent(Context context, boolean refresh) {
    Intent intent = new Intent(context, MsgActivity.class);
    intent.putExtra("refresh", refresh);
    return intent;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mRvMsg.setColorSchemeResources(R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color,
        R.color.apptheme_primary_light_color_pressed);

    mRefreshed = getIntent().getBooleanExtra("refresh", false);
    if (mRefreshed) {
      requestMsgs(1);
    } else {
      cacheOutMsg(1);
    }

    mAlvMsg.setLoadMoreCallback(new LoadMoreCallback() {
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

    mRvMsg.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        showRefreshIndicator(true);
        mRefreshed = true;
        requestMsgs(1);
        ReadMsgStore.inst().clearRead();
      }

      @Override
      public void onDrag() {
        showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        hideRefreshIndicator();
      }
    });

    M.getRegisterHelper().register(mAlvMsg);

    mRstvEmpty.setText(R.string.not_found_msg);
    mRstvEmpty.setSubText(R.string.not_found_msg_tip);
    mRstvEmpty.setDrawable(R.drawable.scene_3);

    // clear account msg new count
    Account.inst().setNewCommentCount(0);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    M.getRegisterHelper().unregister(mAlvMsg);
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

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    if (mMsgAdapter != null) {
      mMsgAdapter.remove(event.getPost());
    }
  }

  private void requestMsgs(final int page) {
    if (page == 1) {
      mRvMsg.setRefreshing(true);
      showRefreshIndicator(true);
    }
    request(new MsgsRequest(page), new OnResponse2<MsgsResponse>() {
      @Override
      public void onResponse(MsgsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mMsgAdapter = new MsgAdapter<CommentMsgItemView>(response.object.posts.lists) {
              @Override
              protected CommentMsgItemView newView(Context context) {
                return new CommentMsgItemView(context);
              }
            };
            mAlvMsg.setAdapter(mMsgAdapter);

            mTvNoNewMsg.setVisibility(View.VISIBLE);

            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
              mTvNoNewMsg.setVisibility(View.GONE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }

            for (Post post : response.object.posts.lists) {
              if (post.read == 0) {
                mTvNoNewMsg.setVisibility(View.GONE);
                break;
              }
            }
          } else {
            mMsgAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;
        } else {
          if (mMsgAdapter == null || mMsgAdapter.getCount() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
            mTvNoNewMsg.setVisibility(View.GONE);
          }
        }
        hideProgressBar();
        hideRefreshIndicator();
        mAlvMsg.stopLoadMore();
        mRvMsg.setRefreshing(false);
      }

      @Override
      public void onResponseError(Throwable e) {
        mRstvEmpty.setVisibility(View.VISIBLE);
        hideProgressBar();
        hideRefreshIndicator();
        mAlvMsg.stopLoadMore();
        mRvMsg.setRefreshing(false);
      }
    }, MsgsResponse.class);
  }

  private void cacheOutMsg(final int page) {
    cacheOut(new MsgsRequest(page), new OnResponse2<MsgsResponse>() {
      @Override
      public void onResponse(MsgsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mMsgAdapter = new MsgAdapter<CommentMsgItemView>(response.object.posts.lists) {
              @Override
              protected CommentMsgItemView newView(Context context) {
                return new CommentMsgItemView(context);
              }
            };
            mAlvMsg.setAdapter(mMsgAdapter);

            mTvNoNewMsg.setVisibility(View.VISIBLE);

            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
              mTvNoNewMsg.setVisibility(View.GONE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }

            for (Post post : response.object.posts.lists) {
              if (post.read == 0) {
                mTvNoNewMsg.setVisibility(View.GONE);
                break;
              }
            }
          } else {
            mMsgAdapter.add(response.object.posts.lists);
          }
          mPageInfo = response.object.posts.page;
          hideProgressBar();
          mAlvMsg.stopLoadMore();
          mRvMsg.setRefreshing(false);
        } else {
          requestMsgs(page);
        }
      }

      @Override
      public void onResponseError(Throwable e) {
        mRstvEmpty.setVisibility(View.VISIBLE);
        mTvNoNewMsg.setVisibility(View.GONE);
        hideProgressBar();
        mAlvMsg.stopLoadMore();
        mRvMsg.setRefreshing(false);
      }
    }, MsgsResponse.class);
  }
}