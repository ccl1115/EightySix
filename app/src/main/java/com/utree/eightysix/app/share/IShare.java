package com.utree.eightysix.app.share;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.data.Circle;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.ShareContentRequest;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public abstract class IShare {

  String mShortenAppLink;
  String mShortenPostLink;

  public abstract void shareApp(BaseActivity activity, Circle circle, String url);

  public abstract void sharePost(BaseActivity activity, Post post, String url, boolean fromBs);

  public abstract void shareComment(BaseActivity activity, Post post, String comment, String url);

  public abstract void shareTag(BaseActivity activity, Circle circle, int tagId, String url);

  public abstract void shareBainian(BaseActivity activity, String recipient, String content, String url);

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

  protected abstract String shareTitleForBainian();

  protected abstract String shareContentForBainian();

  static IUiListener postUiCallback(final Post post, final boolean fromBs) {
    return new BaseUiListener() {
      @Override
      public void onComplete(Object o) {
        if (!fromBs) {
          super.onComplete(o);
        }
        U.getBus().post(new SharePostEvent(post, true, fromBs));
      }

      @Override
      public void onError(UiError uiError) {
        super.onError(uiError);
        U.getBus().post(new SharePostEvent(post, false, fromBs));
      }

      @Override
      public void onCancel() {
        super.onCancel();
        U.getBus().post(new SharePostEvent(post, false, fromBs));
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

      U.request("share_callback", new OnResponse2<Response>() {
        @Override
        public void onResponseError(Throwable e) {

        }

        @Override
        public void onResponse(Response response) {

        }
      }, Response.class, null, null);

      if (BuildConfig.DEBUG) {
        U.showToast("onComplete");
      }
    }

    @Override
    public void onError(UiError uiError) {
      if (BuildConfig.DEBUG) {
        U.showToast(String.format("%d: %s - %s", uiError.errorCode, uiError.errorMessage, uiError.errorDetail));
      }
    }

    @Override
    public void onCancel() {
      if (BuildConfig.DEBUG) {
        U.showToast("onCancel");
      }
    }
  }

  IUiListener defaultListener() {
    return new IUiListener() {

      @Override
      public void onComplete(Object o) {
        if (BuildConfig.DEBUG) U.showToast("onComplete");
        U.request("share_callback", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {

          }
        }, Response.class, null, null);
      }

      @Override
      public void onError(UiError uiError) {
        if (BuildConfig.DEBUG)
          U.showToast(String.format("%d: %s - %s", uiError.errorCode, uiError.errorMessage, uiError.errorDetail));
      }

      @Override
      public void onCancel() {
        if (BuildConfig.DEBUG) {
          U.showToast("onCancel");
        }
      }
    };
  }
}
