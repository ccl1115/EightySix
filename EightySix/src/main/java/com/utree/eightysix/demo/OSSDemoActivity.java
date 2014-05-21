package com.utree.eightysix.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.utils.ViewBinding;

/**
 */
public class OSSDemoActivity extends BaseActivity {

    @ViewBinding.ViewId(R.id.bucket)
    public EditText mBucket;

    @ViewBinding.ViewId(R.id.path)
    public EditText mPath;

    @ViewBinding.ViewId(R.id.button_choose_file)
    public Button mChooseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oss_demo);

        setTitle(getString(R.string.title_oss_demo_activity));
    }
}
