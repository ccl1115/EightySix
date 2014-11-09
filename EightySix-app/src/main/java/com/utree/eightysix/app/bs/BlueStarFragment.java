package com.utree.eightysix.app.bs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.baidu.android.common.util.CommonParam;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.app.share.SharePostEvent;
import com.utree.eightysix.app.web.BaseWebActivity;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.ReceiveStarRequest;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.Random;

/**
 */
public class BlueStarFragment extends BaseFragment {

  private int mStarType;

  @InjectView(R.id.fl_parent)
  public FrameLayout mFlParent;
  private Post mPost;
  private String mStarToken;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_blue_star, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mStarType = getArguments().getInt("starType");
    mStarToken = getArguments().getString("starToken");
    mPost = getArguments().getParcelable("post");

    view.postDelayed(new Runnable() {
      @Override
      public void run() {
        showBlueStar();
      }
    }, 1000);
  }

  private void requestReceiveStart() {
    getBaseActivity().showProgressBar(true);
    U.getRESTRequester().request(new ReceiveStarRequest(mStarToken), new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        getBaseActivity().hideProgressBar();
      }

      @Override
      public void onResponse(Response response) {
        getBaseActivity().hideProgressBar();
        mFlParent.removeAllViews();
        showResultDialog();
      }
    }, Response.class);
  }

  private void showBlueStar() {
    if (getActivity() == null) return;
    final ImageView imageView = new ImageView(getActivity());
    imageView.setImageResource(R.drawable.ic_blue_star);
    FrameLayout.LayoutParams params =
        new FrameLayout.LayoutParams(U.dp2px(48), U.dp2px(48));
    Random random = new Random();
    params.setMargins(random.nextInt(U.dp2px(200)), random.nextInt(U.dp2px(400)), 0, 0);

    mFlParent.addView(imageView, params);

    AnimatorSet set = new AnimatorSet();

    set.playTogether(
        ObjectAnimator.ofFloat(imageView, "rotation" ,0f, 720f),
        ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 0.2f, 1f),
        ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 0.2f, 1f)
    );
    set.setDuration(1000);
    set.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        imageView.setImageResource(R.drawable.ic_blue_face);
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    set.start();

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mStarType == 1) {
          showClickDialog();
        } else if (mStarType == 2) {
          showShareDialog();
        }

        AnimatorSet set = new AnimatorSet();

        set.playTogether(
            ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.6f, 1.2f, 1f),
            ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.8f, 0.9f, 1f)
        );

        set.setDuration(500);
        set.start();
      }
    });
  }

  private void showClickDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    dialog.setTitle(getResources().getString(R.string.congrats_to_get_one_blue_star));

    TextView content = new TextView(getActivity());
    content.setText("点击即可领取成功！");
    final int p = U.dp2px(32);
    content.setPadding(p, p, p, p);
    content.setGravity(Gravity.CENTER);

    dialog.setContent(content);

    dialog.setPositive("点击领取", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        requestReceiveStart();
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  private void showShareDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    dialog.setTitle(getResources().getString(R.string.congrats_to_get_one_blue_star));

    TextView content = new TextView(getActivity());
    final int p = U.dp2px(32);
    content.setPadding(p, p, p, p);
    content.setText("分享后，即可领取成功！");
    content.setGravity(Gravity.CENTER);

    dialog.setContent(content);

    dialog.setPositive("分享并领取", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getBaseActivity().showProgressBar();
        U.getShareManager().sharePostToQzone(getBaseActivity(), mPost, true);
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  private void showResultDialog() {
    final ThemedDialog dialog = new ThemedDialog(getActivity());
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    dialog.setTitle(getResources().getString(R.string.congrats_to_get_one_blue_star));

    TextView content = new TextView(getActivity());
    final int p = U.dp2px(32);
    content.setPadding(p, p, p, p);
    content.setText("领取成功！");
    content.setGravity(Gravity.CENTER);

    dialog.setContent(content);

    dialog.setPositive("查看我的蓝星", new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        BaseWebActivity.start(view.getContext(), "",
            String.format("%s%s?userid=%s&factoryid=%d&virtualImei=%s", U.getConfig("api.host"),
                C.API_PROMOTION, Account.inst().getUserId(), mPost.factoryId, CommonParam.getCUID(U.getContext())));
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  @Subscribe
  public void onSharePostEvent(SharePostEvent event) {
    if (event.getPost().equals(mPost)) {
      if (event.isSuccess() && event.isFromBs()) {
        requestReceiveStart();
      }
    }
  }
}