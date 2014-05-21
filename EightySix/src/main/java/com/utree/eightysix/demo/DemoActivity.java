package com.utree.eightysix.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.utree.eightysix.app.BaseActivity;

/**
 */
public class DemoActivity extends BaseActivity {

    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTopTitle("Main internal demos");

        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        mLinearLayout.addView(buildItem("地图定位测试", LocationDemoActivity.class));
        setContentView(mLinearLayout);
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
