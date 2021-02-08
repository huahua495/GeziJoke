package com.example.gezijoke.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gezijoke.R;
import com.example.gezijoke.model.Feed;
import com.example.gezijoke.ui.AbsListFragment;
import com.example.gezijoke.ui.MutableDataSource;
import com.example.libnavannotation.FragmentDestination;
import com.google.android.exoplayer2.upstream.DummyDataSource;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {


    @Override
    protected void afterCreateView() {
        mViewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                mAdapter.submitList(feeds);
            }
        });
    }

    @Override
    public PagedListAdapter getAdapter() {
        String feedType = getArguments() == null ? "all" : getArguments().getString("feedType");

        return new FeedAdapter(getContext(), feedType);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        Feed feed = mAdapter.getCurrentList().get(mAdapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config=mAdapter.getCurrentList().getConfig();
                if (data!=null&&data.size()>0){
                    MutableDataSource dataSource=new MutableDataSource();
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPageList(config);
                    submitList(pagedList);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }
}