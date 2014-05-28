package com.utree.eightysix.app.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.utils.ViewBinding;

/**
 */
@Layout(R.layout.activity_get_lockpattern)
@TopTitle(R.string.get_lock_pattern)
public class GetLockpatternActivity extends BaseActivity {

    @ViewBinding.ViewId(R.id.btn_start_find)
    public Button mBtnStartFind;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        final int id = v.getId();

        switch (id) {
            case R.id.btn_start_find:
                break;
            default:
                break;
        }
    }
}