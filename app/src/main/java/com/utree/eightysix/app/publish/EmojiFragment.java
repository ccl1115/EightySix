/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.publish;

import android.os.Bundle;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

/**
 */
public class EmojiFragment extends EmojiconGridFragment {
  public static final Emojicon[] DATA = new Emojicon[]{
      Emojicon.fromCodePoint(0x1f600),
      Emojicon.fromCodePoint(0x1f601),
      Emojicon.fromCodePoint(0x1f602),
      Emojicon.fromCodePoint(0x1f603),
      Emojicon.fromCodePoint(0x1f604),
      Emojicon.fromCodePoint(0x1f605),
      Emojicon.fromCodePoint(0x1f606),
      Emojicon.fromCodePoint(0x1f607),
      Emojicon.fromCodePoint(0x1f608),
      Emojicon.fromCodePoint(0x1f609),
      Emojicon.fromCodePoint(0x1f60a),
      Emojicon.fromCodePoint(0x1f60b),
      Emojicon.fromCodePoint(0x1f60c),
      Emojicon.fromCodePoint(0x1f60d),
      Emojicon.fromCodePoint(0x1f60e),
      Emojicon.fromCodePoint(0x1f60f),
      Emojicon.fromCodePoint(0x1f610),
      Emojicon.fromCodePoint(0x1f611),
      Emojicon.fromCodePoint(0x1f612),
      Emojicon.fromCodePoint(0x1f613),
      Emojicon.fromCodePoint(0x1f614),
      Emojicon.fromCodePoint(0x1f615),
      Emojicon.fromCodePoint(0x1f616),
      Emojicon.fromCodePoint(0x1f617),
      Emojicon.fromCodePoint(0x1f618),
      Emojicon.fromCodePoint(0x1f619),
      Emojicon.fromCodePoint(0x1f61a),
      Emojicon.fromCodePoint(0x1f61b),
      Emojicon.fromCodePoint(0x1f61c),
      Emojicon.fromCodePoint(0x1f61d),
      Emojicon.fromCodePoint(0x1f61e),
      Emojicon.fromCodePoint(0x1f61f),
      Emojicon.fromCodePoint(0x1f620),
      Emojicon.fromCodePoint(0x1f621),
      Emojicon.fromCodePoint(0x1f622),
      Emojicon.fromCodePoint(0x1f623),
      Emojicon.fromCodePoint(0x1f624),
      Emojicon.fromCodePoint(0x1f625),
      Emojicon.fromCodePoint(0x1f626),
      Emojicon.fromCodePoint(0x1f627),
      Emojicon.fromCodePoint(0x1f628),
      Emojicon.fromCodePoint(0x1f629),
      Emojicon.fromCodePoint(0x1f62a),
      Emojicon.fromCodePoint(0x1f62b),
      Emojicon.fromCodePoint(0x1f62c),
      Emojicon.fromCodePoint(0x1f62d),
      Emojicon.fromCodePoint(0x1f62e),
      Emojicon.fromCodePoint(0x1f62f),
      Emojicon.fromChar((char) 0x274c)
  };

  public EmojiFragment() {
  }

  public static EmojiFragment newInstance() {
    EmojiFragment emojiGridFragment = new EmojiFragment();
    Bundle args = new Bundle();
    args.putSerializable("emojicons", DATA);
    emojiGridFragment.setArguments(args);
    return emojiGridFragment;
  }
}
