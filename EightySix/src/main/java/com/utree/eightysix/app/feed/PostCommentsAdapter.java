package com.utree.eightysix.app.feed;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.response.data.Comment;
import com.utree.eightysix.response.data.Post;
import com.utree.eightysix.utils.Utils;
import com.utree.eightysix.widget.AsyncImageView;
import com.utree.eightysix.widget.PostPostView;
import java.util.List;

/**
 * @author simon
 */
class PostCommentsAdapter extends BaseAdapter {

  private static final int TYPE_POST = 0;
  private static final int TYPE_COMMENT = 1;

  private Post mPost;
  private List<Comment> mComments;

  public PostCommentsAdapter(Post post, List<Comment> comments) {
    mPost = post;
    mComments = comments;
  }

  public void add(List<Comment> comments) {
    if (mComments == null) {
      mComments = comments;
    } else {
      mComments.addAll(comments);
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return 1 + (mComments == null ? 0 : mComments.size());
  }

  @Override
  public Object getItem(int position) {
    return position == 0 ? mPost : mComments.get(position - 1);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    switch (getItemViewType(position)) {
      case TYPE_POST:
        convertView = getPostView(position, convertView, parent);
        break;
      case TYPE_COMMENT:
        convertView = getCommentView(position, convertView, parent);
        break;
    }
    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? TYPE_POST : TYPE_COMMENT;
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  private View getCommentView(int position, View convertView, final ViewGroup parent) {
    CommentViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
      holder = new CommentViewHolder(convertView);
      convertView.setTag(holder);
    } else {
      holder = (CommentViewHolder) convertView.getTag();
    }

    final Comment comment = (Comment) getItem(position);
    Resources resources = parent.getContext().getResources();

    holder.mAivPortrait.setUrl(comment.avatar);
    holder.mIvHeart.setImageDrawable(comment.praised == 1 ?
        resources.getDrawable(R.drawable.ic_heart_red_pressed) :
        resources.getDrawable(R.drawable.ic_heart_grey_normal));

    holder.mIvHeart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        comment.praised = comment.praised == 1 ? 0 : 1;
        ((BaseAdapter) ((ListView) parent).getAdapter()).notifyDataSetChanged();
      }
    });
    holder.mTvComment.setText(comment.content);
    final String floor = comment.isHost == 1 ? "楼主" : position + "楼";
    holder.mTvInfo.setText(String.format("%s | %s | 赞(%d)", floor, Utils.timestamp(comment.timestamp), comment.praise));
    return convertView;
  }

  private View getPostView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new PostPostView(parent.getContext());
    }

    Post post = (Post) getItem(position);
    PostPostView feedPostView = (PostPostView) convertView;
    feedPostView.setData(post);

    return convertView;
  }

  public static class CommentViewHolder {

    @InjectView (R.id.aiv_portrait)
    public AsyncImageView mAivPortrait;

    @InjectView (R.id.tv_comment)
    public TextView mTvComment;

    @InjectView (R.id.tv_info)
    public TextView mTvInfo;

    @InjectView (R.id.iv_heart)
    public ImageView mIvHeart;

    public CommentViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }
}
