package com.utree.eightysix.app.msg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.app.feed.event.UpdatePraiseCountEvent;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PraisesRequest;
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
@Layout (R.layout.fragment_base_msg)
@TopTitle(R.string.praise_count)
public class PraiseActivity extends BaseActivity {

  @InjectView (R.id.refresh_view)
  public SwipeRefreshLayout mRvMsg;

  @InjectView (R.id.alv_refresh)
  public AdvancedListView mAlvMsg;

  @InjectView(R.id.rstv_empty)
  public RandomSceneTextView mRstvEmpty;

  private MsgAdapter mMsgAdapter;
  private Paginate.Page mPageInfo;

  private boolean mRefreshed;

  public static void start(Context context, boolean refresh) {
    Intent intent = new Intent(context, PraiseActivity.class);
    intent.putExtra("refresh", refresh);
    context.startActivity(intent);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getTopBar().getAbLeft().setDrawable(getResources().getDrawable(R.drawable.top_bar_return));

    mRvMsg.setColorScheme(R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed,
        R.color.apptheme_primary_light_color, R.color.apptheme_primary_light_color_pressed);

    mRefreshed = getIntent().getBooleanExtra("refresh", false);
    if (mRefreshed) {
      requestPraises(1);
    } else {
      cacheOutPraises(1);
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
          requestPraises(mPageInfo.currPage + 1);
        } else {
          cacheOutPraises(mPageInfo.currPage + 1);
        }
        return true;
      }
    });

    mRvMsg.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        showRefreshIndicator(true);
        mRefreshed = true;
        requestPraises(1);
      }

      @Override
      public void onDrag(int value) {
        showRefreshIndicator(false);
      }

      @Override
      public void onCancel() {
        hideRefreshIndicator();
      }
    });

    M.getRegisterHelper().register(mAlvMsg);

    mRstvEmpty.setText(R.string.not_found_praise);
    mRstvEmpty.setSubText(R.string.not_found_praise_tip);
    mRstvEmpty.setDrawable(R.drawable.scene_5);

    // clear account new praise
    Account.inst().setHasNewPraise(false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    M.getRegisterHelper().unregister(mAlvMsg);
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }


  @Subscribe
  public void onPostEvent(Post post) {
    if (mMsgAdapter.contains(post)) {
      mMsgAdapter.notifyDataSetChanged();
    }
  }

  private void requestPraises(final int page) {
    if (page == 1) {
      showRefreshIndicator(true);
      mRvMsg.setRefreshing(true);
      ReadMsgStore.inst().clearRead();
    }
    U.request("praise_list", new OnResponse2<MsgsResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
        hideRefreshIndicator();
        mAlvMsg.stopLoadMore();
        mRvMsg.setRefreshing(false);
        mRstvEmpty.setVisibility(View.VISIBLE);
      }

      @Override
      public void onResponse(MsgsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mMsgAdapter = new MsgAdapter<PraiseMsgItemView>(response.object.posts.lists) {
              @Override
              protected PraiseMsgItemView newView(Context context) {
                return new PraiseMsgItemView(context);
              }
            };
            mAlvMsg.setAdapter(mMsgAdapter);
            setTopTitle(getString(R.string.praise_count) +
                (response.object.myPraiseCount == 0 ? "" : "（" + response.object.myPraiseCount + "）"));
            setTopSubTitle(getString(R.string.praise_count_rank,
                response.object.rank, response.object.topPraiseCount));
            U.getBus().post(new UpdatePraiseCountEvent(response.object.myPraiseCount, response.object.percent));
          } else {
            mMsgAdapter.add(response.object.posts.lists);
          }


          if (response.object.posts.lists.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
          }

          for (Post post : response.object.posts.lists) {
            if (post.read == 1) {
              break;
            }
          }

          mPageInfo = response.object.posts.page;
        } else {
          if (mMsgAdapter == null || mMsgAdapter.getCount() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
          }
        }
        hideProgressBar();
        hideRefreshIndicator();
        mAlvMsg.stopLoadMore();
        mRvMsg.setRefreshing(false);
      }
    }, MsgsResponse.class, page);
  }

  private void cacheOutPraises(final int page) {
    cacheOut(new PraisesRequest(page), new OnResponse2<MsgsResponse>() {
      @Override
      public void onResponse(MsgsResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (page == 1) {
            mMsgAdapter = new MsgAdapter<PraiseMsgItemView>(response.object.posts.lists) {
              @Override
              protected PraiseMsgItemView newView(Context context) {
                return new PraiseMsgItemView(context);
              }
            };
            mAlvMsg.setAdapter(mMsgAdapter);


            setTopTitle(getString(R.string.praise_count) +
                (response.object.myPraiseCount == 0 ? "" : "（" + response.object.myPraiseCount + "）"));

            setTopSubTitle(getString(R.string.praise_count_rank,
                response.object.rank, response.object.topPraiseCount));


            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }

            for (Post post : response.object.posts.lists) {
              if (post.read == 1) {
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
          requestPraises(page);
        }
      }

      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
        hideRefreshIndicator();
        mAlvMsg.stopLoadMore();
        mRvMsg.setRefreshing(false);
        mRstvEmpty.setVisibility(View.VISIBLE);
      }

    }, MsgsResponse.class);
  }

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    if (mMsgAdapter != null) {
      mMsgAdapter.remove(event.getPost());
    }
  }
}