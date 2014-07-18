package com.utree.eightysix.app.report;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.request.ReportRequest;
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

  public String getPostId() {
    return mPostId;
  }

  public String getCommentId() {
    return mCommentId;
  }

  public void setPostId(String postId) {
    mPostId = postId;
  }

  public void setCommentId(String commentId) {
    mCommentId = commentId;
  }

  public ReportDialog(Context context, String postId) {
    super(context);
    mPostId = postId;
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
        U.getBus().post(new ReportRequest(type, mPostId));
      } else {
        U.getBus().post(new ReportRequest(type, mPostId, mCommentId));
      }
      dismiss();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("举报");
    setContent(R.layout.dialog_report);

    ButterKnife.inject(new ReportViewHolder(), this);
  }

}
