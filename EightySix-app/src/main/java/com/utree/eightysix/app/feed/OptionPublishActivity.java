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
import com.utree.eightysix.app.publish.event.PostPublishedEvent;
import com.utree.eightysix.data.BaseItem;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.ChangeNameRequest;
import com.utree.eightysix.request.PublishRequest;
import com.utree.eightysix.response.PublishPostResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.storage.oss.OSSImpl;
import com.utree.eightysix.widget.RoundedButton;

import java.util.Random;

/**
 * @author simon
 */
@Layout(R.layout.activity_option_publish)
public class OptionPublishActivity extends BaseActivity {

  public static final int REQUEST_PUBLISH_OPTION = 1;

  @OnClick(R.id.rb_send)
  public void onRbSendClicked() {
    requestPublish();
  }

  @OnClick(R.id.iv_close)
  public void onIvCloseClicked() {
    setResult(RESULT_CANCELED);
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

  private static final String[] BG_NAME = {
      "bg_13.jpg", "bg_20.jpg", "bg_24.jpg", "bg_28.jpg", "bg_31.jpg", "bg_40.jpg",
      "bg_40.jpg", "bg_200.jpg", "bg_49.jpg", "bg_201.jpg", "bg_63.jpg", "bg_94.jpg",
      "bg_96.jpg", "bg_101.jpg", "bg_105.jpg"
  };

  public static void start(Activity activity, String hint, String name, int circleId) {
    Intent intent = new Intent(activity, OptionPublishActivity.class);

    intent.putExtra("hint", hint);
    intent.putExtra("name", name);
    intent.putExtra("id", circleId);

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    activity.startActivityForResult(intent, REQUEST_PUBLISH_OPTION);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mCircleId = getIntent().getIntExtra("id", 0);

    if (mCircleId == 0) {
      showToast("没有对应的朋友圈", false);
      setResult(RESULT_CANCELED);
      finish();
      return;
    }

    mEtPostContent.setHint(getIntent().getStringExtra("hint"));
    mTvDisplay.setText(getIntent().getStringExtra("name"));
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    setResult(RESULT_CANCELED);
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
    final String url = U.getCloudStorage().getUrl(U.getBgBucket(), "",
        BG_NAME[new Random().nextInt(BG_NAME.length - 1)]);
    request(new PublishRequest(url, "", mEtPostContent.getText().toString(), mCircleId, 1),
        new OnResponse2<PublishPostResponse>() {
          @Override
          public void onResponseError(Throwable e) {
            showToast("发表失败，请重试");
            hideProgressBar();
          }

          @Override
          public void onResponse(PublishPostResponse response) {
            if (RESTRequester.responseOk(response)) {
              Post post = new Post();
              post.id = response.object.id;
              post.content = mEtPostContent.getText().toString();
              post.source = mTvDisplay.getText().toString();
              post.bgColor = "";
              post.bgUrl = url;
              post.type = BaseItem.TYPE_POST;

              U.getBus().post(new PostPublishedEvent(post, mCircleId));

              showToast("发表成功");

              setResult(RESULT_OK);
              finish();
            } else {
              showToast("发表失败，请重试");
            }
            hideProgressBar();
          }
        }, PublishPostResponse.class);
  }
}