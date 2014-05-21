package com.utree.eightysix.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;

/**
 */
public class DemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTopTitle("测试界面");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(buildItem(getString(R.string.title_location_demo_activity), LocationDemoActivity.class));
        linearLayout.addView(buildItem(getString(R.string.title_oss_demo_activity), OSSDemoActivity.class));
        setContentView(linearLayout);
    }

    private TextView buildItem(String text, final Class<?> clazz) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setPadding(20, 20, 20, 20);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DemoActivity.this, clazz));
            }
        });
        return textView;
    }
}
