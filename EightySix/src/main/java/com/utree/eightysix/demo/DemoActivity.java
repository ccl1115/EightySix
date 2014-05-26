package com.utree.eightysix.demo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.utree.eightysix.R;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.widget.TopBar;

/**
 */
public class DemoActivity extends BaseActivity {

    private static final int[] ACTION_ICONS = {
            R.drawable.ic_action_email,
            R.drawable.ic_action_help
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTopTitle("测试界面");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(buildItem(getString(R.string.title_location_demo_activity), LocationDemoActivity.class));
        linearLayout.addView(buildItem(getString(R.string.title_oss_demo_activity), OSSDemoActivity.class));
        setContentView(linearLayout);

        getTopBar().setActionAdapter(new TopBar.ActionAdapter() {

            @Override
            public String getTitle(int position) {
                return null;
            }

            @Override
            public Drawable getIcon(int position) {
                return getResources().getDrawable(ACTION_ICONS[position]);
            }

            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public int getCount() {
                return 2;
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
