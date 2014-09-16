package com.utree.eightysix.app.feed;

import android.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.drawable.RoundRectDrawable;
import com.utree.eightysix.qrcode.QRCodeScanFragment;
import com.utree.eightysix.request.ActiveJoinRequest;
import com.utree.eightysix.response.ActiveJoinResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.QRCodeGenerator;
import com.utree.eightysix.widget.IndicatorView;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
public class RewardFragment extends BaseFragment {

  private PageReward1ViewHolder mPageReward1ViewHolder;
  private PageReward2ViewHolder mPageReward2ViewHolder;

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {
      manager.beginTransaction()
          .detach(this)
          .commit();
    }
  }

  @InjectView(R.id.ll_frame)
  public LinearLayout mLlFrame;

  @InjectView(R.id.vp_page)
  public ViewPager mVpPage;

  @InjectView(R.id.in_page)
  public IndicatorView mInPage;

  @InjectView(R.id.rb_action)
  public RoundedButton mRbAction;

  @OnClick(R.id.rb_action)
  public void onRbActionClicked() {
    FragmentManager manager = getFragmentManager();
    if (manager != null) {
      manager.beginTransaction().add(android.R.id.content, new QRCodeScanFragment()).commit();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_reward, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

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
            container.addView(view);
            return view;
          }
          case 1: {
            View view = LayoutInflater.from(RewardFragment.this.getActivity())
                .inflate(R.layout.page_reward_2, container, false);
            mPageReward2ViewHolder = new PageReward2ViewHolder(view);
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

    requestActiveJoin();
  }

  private void requestActiveJoin() {
    getBaseActivity().request(new ActiveJoinRequest(), new OnResponse2<ActiveJoinResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(ActiveJoinResponse response) {
        if (RESTRequester.responseOk(response)) {
          mPageReward1ViewHolder.mTvMsg1.setText(response.object.msg1);
          mPageReward1ViewHolder.mTvMsg2.setText(response.object.msg2);
          mPageReward1ViewHolder.mTvMsg3.setText(response.object.msg3);

          if (response.object.needFriends == 0) {
            mPageReward1ViewHolder.mRbStatus.setText("已完成");
            mPageReward1ViewHolder.mRbStatus.setBackgroundColor(Color.GREEN);
          } else {
            mPageReward1ViewHolder.mRbStatus.setText("还差" + response.object.needFriends + "个");
            mPageReward1ViewHolder.mRbStatus.setBackgroundColor(Color.RED);
          }

          if (response.object.currentFactory == 1){
            mRbAction.setText("扫一扫，领取奖品");
          } else {
            mRbAction.setText("我也要参加");
          }

          new QRCodeGenerator().generate("eightysix://friend/add/" + response.object.virtualId, new QRCodeGenerator.OnResult() {
            @Override
            public void onResult(Bitmap bitmap) {
              mPageReward2ViewHolder.mIvQrCode.setImageBitmap(bitmap);
            }
          });
        }
      }
    }, ActiveJoinResponse.class);
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

  class PageReward2ViewHolder {

    @InjectView(R.id.iv_qr_code)
    ImageView mIvQrCode;

    PageReward2ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}