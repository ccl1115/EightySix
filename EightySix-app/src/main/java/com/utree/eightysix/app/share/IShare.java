package com.utree.eightysix.app.share;

import android.app.Activity;
import android.widget.Toast;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.ShareContentRequest;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public abstract class IShare {

  String mShortenAppLink;
  String mShortenPostLink;

  public abstract void shareApp(BaseActivity activity, Circle circle, String url);

  public abstract void sharePost(BaseActivity activity, Post post, String url);

  public abstract void shareComment(BaseActivity activity, Post post, String comment, String url);

  public abstract void shareTag(BaseActivity activity, Circle circle, int tagId, String url);

  protected String shareTitleForApp() {
    return "和我一起玩【蓝莓】吧！";
  }

  protected String shareTitleForPost() {
    return "分享1个%s的秘密";
  }

  protected String shareTitleForComment() {
    return "分享自【蓝莓】的精彩评论";
  }

  protected String shareContentForApp() {
    return "%s的朋友最近都在上面，旁边几个厂都火爆了，进来看看吧";
  }

  protected String shareContentForPost() {
    return "转自【蓝莓】-工厂里的秘密社区";
  }

  static IUiListener postUiCallback(final Post post) {
    return new BaseUiListener() {
      @Override
      public void onComplete(Object o) {
        super.onComplete(o);
        U.getBus().post(new SharePostEvent(post, true));
      }

      @Override
      public void onError(UiError uiError) {
        super.onError(uiError);
        U.getBus().post(new SharePostEvent(post, false));
      }

      @Override
      public void onCancel() {
        super.onCancel();
        U.getBus().post(new SharePostEvent(post, false));
      }
    };
  }

  static class BaseUiListener implements IUiListener {
    @Override
    public void onComplete(Object o) {
      U.getRESTRequester().request(new ShareContentRequest(), new OnResponse<Response>() {
        @Override
        public void onResponse(Response response) {

        }
      }, Response.class);

      if (BuildConfig.DEBUG) Toast.makeText(U.getContext(), "onComplete", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(UiError uiError) {
      if (BuildConfig.DEBUG)
        Toast.makeText(U.getContext(),
            String.format("%d: %s - %s", uiError.errorCode, uiError.errorMessage, uiError.errorDetail),
            Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
      if (BuildConfig.DEBUG) Toast.makeText(U.getContext(), "onCancel", Toast.LENGTH_LONG).show();
    }
  }
}
