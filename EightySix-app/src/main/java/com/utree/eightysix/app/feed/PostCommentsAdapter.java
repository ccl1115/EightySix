package com.utree.eightysix.app.feed;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.annotations.Keep;
import com.utree.eightysix.app.feed.event.PostCommentPraiseEvent;
import com.utree.eightysix.app.feed.event.ReloadCommentEvent;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.ColorUtil;
import com.utree.eightysix.widget.FontPortraitView;
import com.utree.eightysix.widget.RandomSceneTextView;
import java.util.ArrayList;
import java.util.List;

/**
 * @author simon
 */
class PostCommentsAdapter extends BaseAdapter {

  private static final int TYPE_POST = 0;
  private static final int TYPE_COMMENT = 1;
  private static final int TYPE_NOT_FOUND = 2;
  private static final int TYPE_RELOAD = 3;

  private Post mPost;
  private List<Comment> mComments;

  private boolean mNeedReload;

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

  public void add(Comment comment) {
    if (mComments == null) {
      mComments = new ArrayList<Comment>();
    }
    mComments.add(comment);
    notifyDataSetChanged();
  }

  public void setPost(Post post) {
    mPost = post;
    notifyDataSetChanged();
  }

  public void setNeedReload(boolean need) {
    mNeedReload = need;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    if (mNeedReload) return 2;
    int count = 1;
    if (mComments != null) {
      count += Math.max(mComments.size(), 1);
    }
    return count;
  }

  @Override
  public Object getItem(int position) {
    if (position == 0) {
      return mPost;
    } else if (mComments != null) {
      if (mComments.size() == 0) {
        return null;
      } else {
        return mComments.get(position - 1);
      }
    }
    return null;
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
      case TYPE_NOT_FOUND:
        convertView = getNotFoundView(convertView, parent);
        break;
      case TYPE_RELOAD:
        convertView = getReloadView(convertView, parent);
        break;
    }
    return convertView;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return TYPE_POST;
    } else if (mNeedReload) {
      return TYPE_RELOAD;
    } else if (mComments != null && mComments.size() == 0) {
      return TYPE_NOT_FOUND;
    } else {
      return TYPE_COMMENT;
    }
  }

  @Override
  public int getViewTypeCount() {
    return 4;
  }

  public void remove(String commentId) {
    for (Comment c : mComments) {
      if (c == null) continue;
      if (c.id.equals(commentId)) {
        c.delete = 1;
        notifyDataSetChanged();
        break;
      }
    }
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

    holder.mIvHeart.setImageDrawable(comment.praised == 1 ?
        resources.getDrawable(R.drawable.ic_heart_red_pressed) :
        resources.getDrawable(R.drawable.ic_heart_grey_normal));

    holder.mIvHeart.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        comment.praised = 1;
        comment.praise++;
        U.getBus().post(new PostCommentPraiseEvent(comment, false));
        notifyDataSetChanged();
      }
    });
    final String floor;
    if (comment.owner == 1) {
      floor = "楼主";
      holder.mFpvPortrait.setEmotion(' ');
      holder.mFpvPortrait.setBackgroundResource(R.drawable.host_portrait);
    } else {
      floor = comment.floor + "楼";
      if (comment.avatar != null && comment.avatar.length() == 1) {
        holder.mFpvPortrait.setEmotion(comment.avatar.charAt(0));
      }
      if (comment.avatarColor != null) {
        holder.mFpvPortrait.setEmotionColor(ColorUtil.strToColor(comment.avatarColor));
      }
    }
    holder.mTvInfo.setText(String.format("%s | %s | 赞(%d)", floor, comment.time, comment.praise));

    if (comment.delete == 1) {
      holder.mTvComment.setText("该评论已被删除");
      holder.mTvComment.setTextColor(resources.getColor(R.color.apptheme_primary_grey_color));
    } else {
      holder.mTvComment.setText(comment.content);
      if (comment.owner == 1) {
        holder.mTvComment.setTextColor(resources.getColor(R.color.apptheme_primary_light_color));
      } else {
        holder.mTvComment.setTextColor(resources.getColor(android.R.color.black));
      }
    }
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

  private View getNotFoundView(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = new RandomSceneTextView(parent.getContext());
    }

    RandomSceneTextView view = (RandomSceneTextView) convertView;
    view.setDrawable(null);
    int padding = U.dp2px(8);
    view.setPadding(padding, padding, padding, padding);
    view.setText(R.string.not_found_comment);

    return convertView;
  }

  private View getReloadView(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_comment_reload, parent, false);
    }

    convertView.findViewById(R.id.tv_reload).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        U.getBus().post(new ReloadCommentEvent());
      }
    });

    return convertView;
  }

  @Keep
  public static class CommentViewHolder {

    @InjectView (R.id.fpv_portrait)
    public FontPortraitView mFpvPortrait;

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
