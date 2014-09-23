package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.account.RewardAcceptedActivity;
import com.utree.eightysix.app.event.QRCodeScanEvent;
import com.utree.eightysix.data.ActiveJoin;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.request.ActiveAcceptRequest;
import com.utree.eightysix.request.ActiveJoinRequest;
import com.utree.eightysix.response.ActiveJoinResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.QRCodeGenerator;
import com.utree.eightysix.widget.IndicatorView;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
public class RewardFragment extends BaseFragment {

  private PageReward1ViewHolder mPageReward1ViewHolder;

  private QRCodeScanFragment mQRCodeFragment;

  private Circle mCircle;
  private ActiveJoin mData;

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    detachSelf();
  }

  @InjectView(R.id.ll_frame)
  public LinearLayout mLlFrame;

  @InjectView(R.id.vp_page)
  public ViewPager mVpPage;

  @InjectView(R.id.in_page)
  public IndicatorView mInPage;

  @InjectView(R.id.rb_action)
  public RoundedButton mRbAction;

  @InjectView(R.id.fl_parent)
  public FrameLayout mFlParent;

  @OnClick(R.id.rb_action)
  public void onRbActionClicked() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {

      if (mQRCodeFragment == null) {
        mQRCodeFragment = new QRCodeScanFragment();
        manager.beginTransaction().add(android.R.id.content, mQRCodeFragment).commit();
      } else if (mQRCodeFragment.isDetached()) {
        manager.beginTransaction().attach(mQRCodeFragment).commit();
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_reward, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mCircle = getArguments().getParcelable("circle");

    mLlFrame.setBackgroundDrawable(new RoundRectDrawable(U.dp2px(8), Color.WHITE));

    mVpPage.setOffscreenPageLimit(2);

    mVpPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mInPage.setPosition(position + positionOffset);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });


    getBaseActivity().getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        requestActiveJoin();
      }
    }, 100);
  }

  public boolean onBackPressed() {
    FragmentManager manager = getFragmentManager();


    if (manager != null) {
      if (mQRCodeFragment != null && mQRCodeFragment.isAdded()) {
        manager.beginTransaction().detach(mQRCodeFragment).commit();
        return true;
      }

      if (!isDetached()) {
        manager.beginTransaction().detach(this).commit();
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  @Subscribe
  public void onQRCodeScanEvent(QRCodeScanEvent event) {
    if ("LANMEI_QRCODE:c.lanmeiquan.com/acceptingAward".equals(event.getText())) {
      FragmentManager manager = getFragmentManager();
      if (manager != null) {
        manager.beginTransaction().detach(mQRCodeFragment).commit();
      }

      requestAccept();
    }
  }

  private void requestAccept() {
    getBaseActivity().request(new ActiveAcceptRequest(), new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          U.showToast("领取成功");
          requestActiveJoin();


          detachSelf();

          RewardAcceptedActivity.start(getBaseActivity(), mCircle);
        }
      }
    }, Response.class);
  }

  private void requestActiveJoin() {
    getBaseActivity().showProgressBar(true);
    getBaseActivity().request(new ActiveJoinRequest(), new OnResponse2<ActiveJoinResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        detachSelf();
        getBaseActivity().hideProgressBar();
      }

      @Override
      public void onResponse(ActiveJoinResponse response) {
        if (RESTRequester.responseOk(response)) {

          mData = response.object;

          mFlParent.setVisibility(View.VISIBLE);

          mVpPage.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
              return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
              return view.equals(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
              switch (position) {
                case 0: {
                  View view = LayoutInflater.from(RewardFragment.this.getActivity())
                      .inflate(R.layout.page_reward_1, container, false);
                  mPageReward1ViewHolder = new PageReward1ViewHolder(view);

                  mPageReward1ViewHolder.mTvMsg1.setText(mData.msg1);
                  mPageReward1ViewHolder.mTvMsg2.setText(mData.msg2);
                  mPageReward1ViewHolder.mTvMsg3.setText(mData.msg3);

                  if (mData.needFriends == 0) {
                    mPageReward1ViewHolder.mRbStatus.setText("已完成");
                    mPageReward1ViewHolder.mRbStatus.setBackgroundColor(0xff00a600);
                  } else {
                    mPageReward1ViewHolder.mRbStatus.setText("还差" + mData.needFriends + "个");
                    mPageReward1ViewHolder.mRbStatus.setBackgroundColor(0xff930000);
                  }

                  if (mData.currentFactory == 1){
                    mRbAction.setText("扫一扫，领取抱枕");
                    mRbAction.setEnabled(true);
                  } else {
                    mRbAction.setText("我也要参加");
                    mRbAction.setEnabled(true);
                  }

                  if (mData.accepted == 1) {
                    mRbAction.setText("已领取");
                    mRbAction.setEnabled(false);
                  }
                  container.addView(view);
                  return view;
                }
                case 1: {
                  View view = LayoutInflater.from(RewardFragment.this.getActivity())
                      .inflate(R.layout.page_reward_2, container, false);
                  container.addView(view);
                  return view;
                }
                case 2: {
                  View view = LayoutInflater.from(RewardFragment.this.getActivity())
                      .inflate(R.layout.page_reward_3, container, false);
                  container.addView(view);
                  return view;
                }
              }
              return null;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
              container.removeView((View) object);
            }
          });
        } else {
          detachSelf();
        }

        getBaseActivity().hideProgressBar();
      }
    }, ActiveJoinResponse.class);
  }

  private void detachSelf() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {
      if (!isDetached()) {
        manager.beginTransaction().detach(this).commit();
      }
    }
  }

  class PageReward1ViewHolder {
    @InjectView(R.id.tv_msg1)
    TextView mTvMsg1;

    @InjectView(R.id.tv_msg2)
    TextView mTvMsg2;

    @InjectView(R.id.tv_msg3)
    TextView mTvMsg3;

    @InjectView(R.id.rb_status)
    RoundedButton mRbStatus;

    PageReward1ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

}