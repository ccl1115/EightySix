package com.utree.eightysix.app.feed;

import android.os.Bundle;
import android.widget.ListView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.utils.ViewId;

/**
 */
@Layout(R.layout.activity_feed)
public class FeedActivity extends BaseActivity {

    @ViewId(R.id.lv_feed)
    public ListView mLvFeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLvFeed.setAdapter(new FeedAdapter());
            }
        }, 2000);
    }
}