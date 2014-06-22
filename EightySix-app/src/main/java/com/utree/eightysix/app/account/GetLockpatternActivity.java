package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.widget.Button;
import butterknife.InjectView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;

/**
 */
@Layout(R.layout.activity_get_lockpattern)
@TopTitle(R.string.get_lock_pattern)
public class GetLockpatternActivity extends BaseActivity {

    @InjectView(R.id.btn_start_find)
    public Button mBtnStartFind;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}