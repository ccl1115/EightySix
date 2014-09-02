package com.utree.eightysix.config;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigurationActivity extends Activity {

  @InjectView (R.id.lv_config)
  public ListView mLvConfig;

  @OnClick (R.id.btn_save)
  public void onBtnSaveClicked() {
    File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/configuration.properties");
    if (!file.exists()) {
      file = new File("/sdcard/configuration.properties");
    }
    FileOutputStream fos = null;
    Adapter adapter = (Adapter) mLvConfig.getAdapter();

    if (adapter != null && file.exists()) {
      Properties properties = itemsToProperties(adapter.getItems());
      try {
        fos = new FileOutputStream(file);
        properties.store(fos, "");
        fos.flush();
      } catch (IOException e) {
        e.printStackTrace();
        Log.d("EightySix-config-tool", "configuration file save failed");
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException ignored) {
          }
        }
      }
    } else {
      Log.d("EightySix-config-tool", "configuration file save failed");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this, this);


    File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/configuration.properties");
    if (!file.exists()) {
      file = new File("/sdcard/configuration.properties");
    }

    Log.d("EightySix-config-tool", "configuration file = " + file);

    if (file.exists()) {
      FileInputStream in = null;
      try {
        in = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(in);
        mLvConfig.setAdapter(new Adapter(propertiesToItems(properties)));
      } catch (IOException e) {
        e.printStackTrace();
        Log.d("EightySix-config-tool", "configuration file loaded failed");
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException ignored) {
          }
        }
      }
    } else {
      Log.d("EightySix-config-tool", "configuration file loaded failed");
    }
  }

  private List<Item> propertiesToItems(Properties properties) {
    List<Item> items = new ArrayList<Item>();
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      Item item = new Item();
      item.key = entry.getKey().toString();
      item.value = entry.getValue().toString();
      items.add(item);
    }
    return items;
  }

  private Properties itemsToProperties(List<Item> items) {
    Properties properties = new Properties();
    for (Item item : items) {
      properties.setProperty(item.key, item.value);
    }
    return properties;
  }

  private static class Item {
    String key;
    String value;
  }

  static class ItemViewHolder {
    private Item mItem;

    @InjectView(R.id.tv_key)
    public TextView mTvKey;

    @InjectView(R.id.et_value)
    public EditText mEtValue;

    @OnTextChanged(R.id.et_value)
    public void onEtValueTextChanged(CharSequence text) {
      mItem.value = text.toString();
    }

    ItemViewHolder(View view) {
      ButterKnife.inject(this, view);
    }
  }

  static class Adapter extends BaseAdapter {

    private List<Item> mProperties;

    Adapter(List<Item> items) {
      mProperties = items;
    }

    @Override
    public int getCount() {
      return mProperties.size();
    }

    @Override
    public Item getItem(int i) {
      return mProperties.get(i);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      ItemViewHolder holder;
      if (view == null) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_config, viewGroup, false);
        holder = new ItemViewHolder(view);
        view.setTag(holder);
      } else {
        holder = (ItemViewHolder) view.getTag();
      }

      Item item = getItem(i);
      holder.mItem = getItem(i);

      holder.mEtValue.setText(item.value);
      holder.mTvKey.setText(item.key);
      return view;
    }

    public List<Item> getItems() {
      return mProperties;
    }
  }
}

