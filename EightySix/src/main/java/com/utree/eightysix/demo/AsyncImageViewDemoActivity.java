package com.utree.eightysix.demo;

import android.os.Bundle;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.widget.AsyncImageView;

/**
 */
public class AsyncImageViewDemoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncImageView asyncImageView = new AsyncImageView(this);

        setContentView(asyncImageView);

        asyncImageView.setUrl("http://misc.360buyimg.com/lib/img/e/logo-201305.png");
    }
}