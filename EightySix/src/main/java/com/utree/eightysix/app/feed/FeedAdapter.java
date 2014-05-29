package com.utree.eightysix.app.feed;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.utree.eightysix.response.Feeds;

/**
 */
class FeedAdapter extends BaseAdapter {

    private Feeds mList;

    FeedAdapter(Feeds list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
