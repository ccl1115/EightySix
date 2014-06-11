package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.response.data.Post;

/**
 */
public class PostView extends RelativeLayout {

    @InjectView(R.id.tv_content)
    public TextView mTvContent;

    @InjectView(R.id.tv_source)
    public TextView mTvSource;

    @InjectView(R.id.tv_praise)
    public TextView mTvPraise;

    @InjectView(R.id.tv_comment)
    public TextView mTvComment;

    @InjectView(R.id.tv_last_comment)
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
