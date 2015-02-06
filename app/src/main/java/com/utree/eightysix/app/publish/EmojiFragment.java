/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.publish;

import android.os.Bundle;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconHandler;
import com.rockerhieu.emojicon.emoji.Emojicon;

/**
 */
public class EmojiFragment extends EmojiconGridFragment {

  public EmojiFragment() {
  }

  public static EmojiFragment newInstance() {
    EmojiFragment emojiGridFragment = new EmojiFragment();

    int[] emojis = EmojiconHandler.getAll();

    Emojicon[] data = new Emojicon[emojis.length];
    for (int i = 0; i < emojis.length; i++) {
      if (emojis[i] < 0xffff) {
        data[i] = Emojicon.fromChar((char) emojis[i]);
      } else {
        data[i] = Emojicon.fromCodePoint(emojis[i]);
      }
    }

    Bundle args = new Bundle();
    args.putSerializable("emojicons", data);
    emojiGridFragment.setArguments(args);
    return emojiGridFragment;
  }

  public static EmojiFragment newInstance(int[] emojis) {

    EmojiFragment emojiGridFragment = new EmojiFragment();

    Emojicon[] data = new Emojicon[emojis.length];
    for (int i = 0; i < emojis.length; i++) {
      if (emojis[i] < 0xffff) {
        data[i] = Emojicon.fromChar((char) emojis[i]);
      } else {
        data[i] = Emojicon.fromCodePoint(emojis[i]);
      }
    }

    Bundle args = new Bundle();
    args.putSerializable("emojicons", data);
    emojiGridFragment.setArguments(args);
    return emojiGridFragment;
  }
}
