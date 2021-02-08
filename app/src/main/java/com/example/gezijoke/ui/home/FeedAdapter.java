package com.example.gezijoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gezijoke.databinding.LayoutFeedTypeImageBinding;
import com.example.gezijoke.databinding.LayoutFeedTypeVideoBinding;
import com.example.gezijoke.model.Feed;

public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private String mCateGory;
    private Context mContext;

    protected FeedAdapter(Context context, String cateGory) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
        mCateGory = cateGory;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        return feed.itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = null;
        if (viewType == Feed.TYPE_IMAGE_TEXT) {
            dataBinding = LayoutFeedTypeImageBinding.inflate(inflater);
        } else {
            dataBinding = LayoutFeedTypeVideoBinding.inflate(inflater);
        }
        return new ViewHolder(dataBinding.getRoot(), dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding mBinding;

        public ViewHolder(@NonNull View itemView, ViewDataBinding dataBinding) {
            super(itemView);
            mBinding = dataBinding;
        }

        public void bindData(Feed item) {
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                imageBinding.setFeed(item);
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
                imageBinding.setLifecycleOwner((LifecycleOwner) mContext);
            } else if (mBinding instanceof LayoutFeedTypeVideoBinding) {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.setFeed(item);
                videoBinding.listPlayerView.bindData(
                        mCateGory,
                        item.width,
                        item.height,
                        item.cover,
                        item.url
                );
                videoBinding.setLifecycleOwner((LifecycleOwner) mContext);
            }
        }
    }
}
