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
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.feed.event.PostDeleteEvent;
import com.utree.eightysix.data.Paginate;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PraisesRequest;
import com.utree.eightysix.response.MsgsResponse;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.widget.AdvancedListView;
import com.utree.eightysix.widget.IRefreshable;
import com.utree.eightysix.widget.LoadMoreCallback;
import com.utree.eightysix.widget.RandomSceneTextView;
import com.utree.eightysix.widget.RefresherView;
import java.util.List;

/**
 * @author simon
 */
@Layout (R.layout.activity_msg)
public class PraiseActivity extends BaseActivity {

  @InjectView (R.id.refresh_view)
  public RefresherView mRvMsg;

  @InjectView (R.id.tv_no_new_msg)
  public TextView mTvNoNewMsg;

  @InjectView (R.id.alv_refresh)
  public AdvancedListView mAlvMsg;

  @InjectView(R.id.tv_empty_text)
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
    if (U.useFixture()) {
      showProgressBar();
      getHandler().postDelayed(new Runnable() {
        @Override
        public void run() {
          List<Post> valid = U.getFixture(Post.class, 23, "valid");
          for (Post p : valid)
          {
            if (p.read == 0) {
              mTvNoNewMsg.setVisibility(View.INVISIBLE);
            }
          }
          mAlvMsg.setAdapter(new MsgAdapter<PraiseMsgItemView>(valid) {
            @Override
            protected PraiseMsgItemView newView(Context context) {
              return new PraiseMsgItemView(context);
            }
          });
          hideProgressBar();
        }
      }, 1000);
    } else {
      mRefreshed = getIntent().getBooleanExtra("refresh", false);
      if (mRefreshed) {
        requestPraises(1);
      } else {
        cacheOutPraises(1);
      }
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

    mRvMsg.setOnRefreshListener(new IRefreshable.OnRefreshListener() {
      @Override
      public void onStateChanged(IRefreshable.State state) {
      }

      @Override
      public void onPreRefresh() {
        mRefreshed = true;
        requestPraises(1);
      }

      @Override
      public void onRefreshData() {

      }

      @Override
      public void onRefreshUI() {

      }
    });

    U.getBus().register(mAlvMsg);

    mRstvEmpty.setText(R.string.not_found_praise);
    mRstvEmpty.setSubText(R.string.not_found_praise_tip);
    mRstvEmpty.setDrawable(R.drawable.scene_5);

    mTvNoNewMsg.setText(R.string.no_new_praise);

    // clear account new praise
    Account.inst().setHasNewPraise(false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    U.getBus().unregister(mAlvMsg);
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

  private void requestPraises(final int page) {
    request(new PraisesRequest(page), new OnResponse<MsgsResponse>() {
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
            setTopTitle(getString(R.string.praise_count_obtained, response.object.myPraiseCount));
            setTopSubTitle(getString(R.string.praise_status,
                response.object.postCount, response.object.commentCount, response.object.percent));
          } else {
            mMsgAdapter.add(response.object.posts.lists);
          }

          mTvNoNewMsg.setVisibility(View.VISIBLE);

          if (response.object.posts.lists.size() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
            mTvNoNewMsg.setVisibility(View.GONE);
          } else {
            mRstvEmpty.setVisibility(View.GONE);
          }

          for (Post post : response.object.posts.lists) {
            if (post.read == 1) {
              mTvNoNewMsg.setVisibility(View.GONE);
              break;
            }
          }

          mPageInfo = response.object.posts.page;
        } else {
          if (mMsgAdapter == null || mMsgAdapter.getCount() == 0) {
            mRstvEmpty.setVisibility(View.VISIBLE);
            mTvNoNewMsg.setVisibility(View.GONE);
          }
        }
        hideProgressBar();
        mAlvMsg.stopLoadMore();
        mRvMsg.hideHeader();
      }
    }, MsgsResponse.class);

    showProgressBar();
  }

  private void cacheOutPraises(final int page) {
    cacheOut(new PraisesRequest(page), new OnResponse<MsgsResponse>() {
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


            setTopTitle(getString(R.string.praise_count_obtained, response.object.myPraiseCount));
            setTopSubTitle(getString(R.string.praise_status,
                response.object.postCount, response.object.commentCount, response.object.percent));

            mTvNoNewMsg.setVisibility(View.VISIBLE);

            if (response.object.posts.lists.size() == 0) {
              mRstvEmpty.setVisibility(View.VISIBLE);
              mTvNoNewMsg.setVisibility(View.GONE);
            } else {
              mRstvEmpty.setVisibility(View.GONE);
            }

            for (Post post : response.object.posts.lists) {
              if (post.read == 1) {
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
          mRvMsg.hideHeader();
        } else {
          requestPraises(page);
        }
      }
    }, MsgsResponse.class);

    showProgressBar();
  }

  @Subscribe
  public void onPostDeleteEvent(PostDeleteEvent event) {
    if (mMsgAdapter != null) {
      mMsgAdapter.remove(event.getPost());
    }
  }
}