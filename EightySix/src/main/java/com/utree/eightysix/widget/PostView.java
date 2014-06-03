package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.response.Post;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;

/**
 */
public class PostView extends RelativeLayout implements View.OnClickListener {

    @ViewId(R.id.tv_content)
    @OnClick
    public TextView mTvContent;

    @ViewId(R.id.tv_source)
    @OnClick
    public TextView mTvSource;

    @ViewId(R.id.tv_praise)
    @OnClick
    public TextView mTvPraise;

    @ViewId(R.id.tv_comment)
    @OnClick
    public TextView mTvComment;

    @ViewId(R.id.tv_last_comment)
    @OnClick
    public TextView mTvLastComment;

    public PostView(Context context) {
        this(context, null, 0);
    }

    public PostView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PostView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.item_post, this);
        U.viewBinding(this, this);

        mTvComment.setOnClickListener(this);
        mTvContent.setOnClickListener(this);
        mTvLastComment.setOnClickListener(this);
        mTvSource.setOnClickListener(this);
        mTvPraise.setOnClickListener(this);
    }

    public CharSequence getContent() {
        return mTvContent.getText();
    }

    public void setContent(String content) {
        mTvContent.setText(content);
    }

    public CharSequence getSource() {
        return mTvSource.getText();
    }

    public void setSource(String source) {
        mTvSource.setText(source);
    }

    public CharSequence getPraise() {
        return mTvPraise.getText();
    }

    public void setPraise(String praise) {
        mTvPraise.setText(praise);
    }

    public CharSequence getComment() {
        return mTvComment.getText();
    }

    public void setComment(String comment) {
        mTvComment.setText(comment);
    }

    public TextView getLastComment() {
        return mTvLastComment;
    }

    public void setLastComment(String lastComment) {
        mTvLastComment.setText(lastComment);
    }

    @Override
    public final void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.tv_content:
                break;
            case R.id.tv_source:
                break;
            case R.id.tv_praise:
                break;
            case R.id.tv_comment:
                break;
            case R.id.tv_last_comment:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, widthSize + MeasureSpec.EXACTLY);
    }

    public void setData(Post post) {
        setContent(post.content);
        setComment(String.valueOf(post.comments));
        setPraise(String.valueOf(post.praise));
        setSource(post.source);
        setLastComment(post.comment.toString());
    }
}
