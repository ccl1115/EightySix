package com.utree.eightysix.app.feed;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.OnClick;
import com.utree.eightysix.utils.ViewId;

/**
 */
public class PostView extends RelativeLayout implements View.OnClickListener {

    @ViewId(R.id.tv_content)
    @OnClick
    public TextView mContent;

    @ViewId(R.id.tv_source)
    @OnClick
    public TextView mSource;

    @ViewId(R.id.tv_praise)
    @OnClick
    public TextView mPraise;

    @ViewId(R.id.tv_comment)
    @OnClick
    public TextView mComment;

    @ViewId(R.id.tv_last_comment)
    @OnClick
    public TextView mLastComment;

    @Override
    public final void onClick(View v) {
        if (mOnClickListener == null) return;

        final int id = v.getId();

        switch (id) {
            case R.id.tv_content:
                mOnClickListener.onContentClick(v);
                break;
            case R.id.tv_source:
                mOnClickListener.onSourceClick(v);
                break;
            case R.id.tv_praise:
                mOnClickListener.onPraiseClick(v);
                break;
            case R.id.tv_comment:
                mOnClickListener.onCommentClick(v);
                break;
            case R.id.tv_last_comment:
                mOnClickListener.onLastCommentClick(v);
                break;
            default:
                break;
        }
    }

    public interface OnClickListener {
        void onContentClick(View v);

        void onSourceClick(View v);

        void onPraiseClick(View v);

        void onCommentClick(View v);

        void onLastCommentClick(View v);
    }

    private OnClickListener mOnClickListener;

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

        mComment.setOnClickListener(this);
        mContent.setOnClickListener(this);
        mLastComment.setOnClickListener(this);
        mSource.setOnClickListener(this);
        mPraise.setOnClickListener(this);
    }

    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, (int) (widthSize * 0.85f) + MeasureSpec.EXACTLY);
    }
}
