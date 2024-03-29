package com.utree.eightysix.app.post;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.request.ReportRequest;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;

/**
 * @author simon
 */
public class ReportDialog extends ThemedDialog {

  private String mPostId;
  private String mCommentId;

  public ReportDialog(Context context, String postId, String commentId) {
    this(context, postId);
    mCommentId = commentId;
  }

  public ReportDialog(Context context, String postId) {
    super(context);
    mPostId = postId;
  }

  public String getPostId() {
    return mPostId;
  }

  public void setPostId(String postId) {
    mPostId = postId;
  }

  public String getCommentId() {
    return mCommentId;
  }

  public void setCommentId(String commentId) {
    mCommentId = commentId;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("举报");
    setContent(R.layout.dialog_report);

    ButterKnife.inject(new ReportViewHolder(), this);

    setCanceledOnTouchOutside(false);
  }

  @Keep
  public class ReportViewHolder {
    @OnClick ({
        R.id.rb_type_1,
        R.id.rb_type_2,
        R.id.rb_type_3,
        R.id.rb_type_4,
        R.id.rb_type_5,
        R.id.rb_type_6})
    public void onRbTypeClicked(View view) {
      int type = 1;
      switch (view.getId()) {
        case R.id.rb_type_1:
          type = ReportRequest.TYPE_1;
          break;
        case R.id.rb_type_2:
          type = ReportRequest.TYPE_2;
          break;
        case R.id.rb_type_3:
          type = ReportRequest.TYPE_3;
          break;
        case R.id.rb_type_4:
          type = ReportRequest.TYPE_4;
          break;
        case R.id.rb_type_5:
          type = ReportRequest.TYPE_5;
          break;
        case R.id.rb_type_6:
          type = ReportRequest.TYPE_6;
          break;
      }

      if (mCommentId == null) {
        U.getRESTRequester().request(new ReportRequest(type, mPostId), new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {

          }
        }, Response.class);
      } else {
        U.getRESTRequester().request(new ReportRequest(type, mPostId, mCommentId), new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {

          }
        }, Response.class);
      }
      dismiss();
    }

  }

}
