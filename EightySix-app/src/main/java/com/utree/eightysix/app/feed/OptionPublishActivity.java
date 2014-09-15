package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.ChangeNameRequest;
import com.utree.eightysix.request.PublishRequest;
import com.utree.eightysix.response.PublishPostResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.RoundedButton;

/**
 * @author simon
 */
@Layout(R.layout.activity_option_publish)
public class OptionPublishActivity extends BaseActivity {

  @OnClick(R.id.content)
  public void onFlContentClicked() {
    finish();
  }

  @OnClick(R.id.rb_send)
  public void onRbSendClicked() {
    requestPublish();
  }

  @OnClick(R.id.iv_close)
  public void onIvCloseClicked() {
    finish();
  }

  @InjectView(R.id.et_post_content)
  public EditText mEtPostContent;

  @InjectView(R.id.tv_display)
  public TextView mTvDisplay;

  @OnClick(R.id.tv_shuffle)
  public void onTvShuffleClicked() {
    requestChangeName();
  }

  public int mCircleId;

  public static void start(Context context, String hint, String name, int circleId) {
    Intent intent = new Intent(context, OptionPublishActivity.class);

    intent.putExtra("hint", hint);
    intent.putExtra("name", name);
    intent.putExtra("id", circleId);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mCircleId = getIntent().getIntExtra("id", 0);

    if (mCircleId == 0) {
      showToast("没有对应的工友圈", false);
      finish();
      return;
    }

    mEtPostContent.setHint(getIntent().getStringExtra("hint"));
    mTvDisplay.setText(getIntent().getStringExtra("name"));

  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }


  private void requestChangeName() {
    request(new ChangeNameRequest(mCircleId), new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          mTvDisplay.setText(response.message);
        }
      }
    }, Response.class);
  }

  private void requestPublish() {
    showProgressBar(true);
    request(new PublishRequest("", "0xff000000", mEtPostContent.getText().toString(), mCircleId, 1), new OnResponse2<PublishPostResponse>() {
      @Override
      public void onResponseError(Throwable e) {

      }

      @Override
      public void onResponse(PublishPostResponse response) {
        if (RESTRequester.responseOk(response)) {
          Post post = new Post();
          post.id = response.object.id;
          post.content = mEtPostContent.getText().toString();
          post.source = mTvDisplay.getText().toString();
          post.bgColor = "#FF000000";

          U.getBus().post(post);

          showToast("发表成功");
          finish();
        } else {
          showToast("发表失败，请重试");
        }
        hideProgressBar();
      }
    }, PublishPostResponse.class);
  }
}