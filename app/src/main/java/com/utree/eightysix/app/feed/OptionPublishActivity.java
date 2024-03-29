package com.utree.eightysix.app.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.squareup.otto.Subscribe;
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
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;

import java.util.Random;

/**
 * @author simon
 */
@Layout(R.layout.activity_option_publish)
public class OptionPublishActivity extends BaseActivity {

  public static final int REQUEST_PUBLISH_OPTION = 1;
  private static final String[] BG_NAME = {
      "bg_13.jpg", "bg_20.jpg", "bg_24.jpg", "bg_28.jpg", "bg_31.jpg", "bg_40.jpg",
      "bg_40.jpg", "bg_200.jpg", "bg_49.jpg", "bg_201.jpg", "bg_63.jpg", "bg_94.jpg",
      "bg_96.jpg", "bg_101.jpg", "bg_105.jpg"
  };
  public boolean mRequesting;
  public int mCircleId;
  @InjectView(R.id.et_post_content)
  public EditText mEtPostContent;
  @InjectView(R.id.tv_display)
  public TextView mTvDisplay;
  @InjectView(R.id.tv_send)
  public TextView mTvSend;

  public static void start(Activity activity, String hint, String name, int circleId) {
    Intent intent = new Intent(activity, OptionPublishActivity.class);

    intent.putExtra("hint", hint);
    intent.putExtra("name", name);
    intent.putExtra("id", circleId);

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    activity.startActivityForResult(intent, REQUEST_PUBLISH_OPTION);
  }

  @OnClick(R.id.tv_send)
  public void onRbSendClicked() {
    requestPublish();
  }

  @OnClick(R.id.iv_close)
  public void onIvCloseClicked() {
    setResult(RESULT_CANCELED);
    finish();
  }

  @OnClick(R.id.tv_shuffle)
  public void onTvShuffleClicked() {
    requestChangeName();
  }

  @OnTextChanged(R.id.et_post_content)
  public void onEtPostContentChanged(CharSequence cs) {
    if (cs.length() == 0) {
      mTvSend.setEnabled(false);
    } else {
      mTvSend.setEnabled(true);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    hideTopBar(false);

    mCircleId = getIntent().getIntExtra("id", 0);

    if (mCircleId == 0) {
      showToast("没有对应的打工圈", false);
      setResult(RESULT_CANCELED);
      finish();
      return;
    }

    mEtPostContent.setHint(getIntent().getStringExtra("hint"));
    mTvDisplay.setText("临时名：" + getIntent().getStringExtra("name"));
  }

  @Override
  @Subscribe
  public void onLogout(Account.LogoutEvent event) {
    setResult(RESULT_CANCELED);
    finish();
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }


  private void requestChangeName() {
    if (mRequesting) {
      return;
    }
    showProgressBar(true);
    mRequesting = true;
    request(new ChangeNameRequest(mCircleId), new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        hideProgressBar();
        mRequesting = false;
      }

      @Override
      public void onResponse(Response response) {
        if (RESTRequester.responseOk(response)) {
          mTvDisplay.setText("临时名：" + response.message);
        }
        hideProgressBar();
        mRequesting = false;
      }
    }, Response.class);
  }

  private void requestPublish() {
    showProgressBar(true);
    final String url = U.getCloudStorage().getUrl(U.getBgBucket(), "",
        BG_NAME[new Random().nextInt(BG_NAME.length - 1)]);
    PublishRequest build = new PublishRequest.Builder().bgUrl(url).bgColor("")
        .content(mEtPostContent.getText().toString())
        .sourceType(1)
        .factoryId(mCircleId)
        .build();
    RequestData<PublishPostResponse> res = new RequestData<PublishPostResponse>(build);
    res.setHost(U.getConfig("api.host.second"));
    request(res, new OnResponse2<PublishPostResponse>() {
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