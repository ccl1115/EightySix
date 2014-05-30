package com.utree.eightysix.widget.guide;

import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * 导航蒙板构建器
 * 
 * @author yulu02
 */
public class GuideBuilder {

	private Configuration mConfiguration;

	private boolean mBuilt;

	private List<Component> mComponents = new ArrayList<Component>();
	private OnVisibilityChangedListener mOnVisibilityChangedListener;

	/**
     *
     */
	public GuideBuilder() {
		mConfiguration = new Configuration();
	}

	/**
	 * 设置蒙板透明度
	 * 
	 * @param alpha
	 *            [0-255] 0 表示完全透明，255表示不透明
	 * @return GuideBuilder
	 */
	public GuideBuilder setAlpha(int alpha) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (alpha < 0 || alpha > 255) {
			throw new BuildException("Illegal alpha value, should between [0-255]");
		}
		mConfiguration.mAlpha = alpha;
		return this;
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	public GuideBuilder setTargetView(View v) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (v == null) {
			throw new BuildException("Illegal view.");
		}
		mConfiguration.mTargetView = v;
		return this;
	}

	/**
	 * 设置目标View的id
	 * 
	 * @param id
	 *            目标View的id
	 * @return GuideBuilder
	 */
	public GuideBuilder setTargetViewId(int id) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (id <= 0) {
			throw new BuildException("Illegal view id.");
		}
		mConfiguration.mTargetViewId = id;
		return this;
	}

	/**
	 * 设置蒙板View的id
	 * 
	 * @param id
	 *            蒙板View的id
	 * @return GuideBuilder
	 */
	public GuideBuilder setFullingViewId(int id) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (id <= 0) {
			throw new BuildException("Illegal view id.");
		}
		mConfiguration.mFullingViewId = id;
		return this;
	}

	/**
	 * 设置蒙板颜色的资源id
	 * 
	 * @param id
	 *            资源id
	 * @return GuideBuilder
	 */
	public GuideBuilder setFullingColorId(int id) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (id <= 0) {
			throw new BuildException("Illegal color resource id.");
		}
		mConfiguration.mFullingColorId = id;
		return this;
	}

	/**
	 * 是否在点击的时候自动退出蒙板
	 * 
	 * @param b
	 *            true if needed
	 * @return GuideBuilder
	 */
	public GuideBuilder setAutoDismiss(boolean b) {
		if (mBuilt) {
			throw new BuildException("Already created, rebuild a new one.");
		}
		mConfiguration.mAutoDismiss = b;
		return this;
	}

	/**
	 * 是否覆盖目标
	 * 
	 * @param b
	 *            true 蒙板将会覆盖整个屏幕
	 * @return GuideBuilder
	 */
	public GuideBuilder setOverlayTarget(boolean b) {
		if (mBuilt) {
			throw new BuildException("Already created, rebuild a new one.");
		}
		mConfiguration.mOverlayTarget = b;
		return this;
	}

	/**
	 * 设置进入动画
	 * 
	 * @param id
	 *            进入动画的id
	 * @return GuideBuilder
	 */
	public GuideBuilder setEnterAnimationId(int id) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (id <= 0) {
			throw new BuildException("Illegal animation resource id.");
		}
		mConfiguration.mEnterAnimationId = id;
		return this;
	}

	/**
	 * 设置退出动画
	 * 
	 * @param id
	 *            退出动画的id
	 * @return GuideBuilder
	 */
	public GuideBuilder setExitAnimationId(int id) {
		if (mBuilt) {
			throw new BuildException("Already created. rebuild a new one.");
		} else if (id <= 0) {
			throw new BuildException("Illegal animation resource id.");
		}
		mConfiguration.mExitAnimationId = id;
		return this;
	}

	/**
	 * 添加一个控件
	 * 
	 * @param component
	 *            被添加的控件
	 * @return GuideBuilder
	 */
	public GuideBuilder addComponent(Component component) {
		if (mBuilt) {
			throw new BuildException("Already created, rebuild a new one.");
		}
		mComponents.add(component);
		return this;
	}

	public GuideBuilder setOnVisibilityChangedListener(OnVisibilityChangedListener onVisibilityChangedListener) {
		if (mBuilt) {
			throw new BuildException("Already created, rebuild a new one.");
		}
		mOnVisibilityChangedListener = onVisibilityChangedListener;
		return this;
	}

    public GuideBuilder setOutsideTouchable(boolean touchable) {
        mConfiguration.mOutsideTouchable = touchable;
        return this;
    }

	// /**
	// * 创建GuideFragment
	// * @return GuideFragment
	// */
	// public GuideFragment createGuideFragment() {
	// Log.d(TAG, "Configuration: " + mConfiguration.toString());
	// GuideFragment guideFragment = new GuideFragment();
	// Component[] components = new Component[mComponents.size()];
	// guideFragment.setComponents(mComponents.toArray(components));
	// guideFragment.setConfiguration(mConfiguration);
	// guideFragment.setCallback(mOnVisibilityChangedListener);
	// mComponents = null;
	// mConfiguration = null;
	// mOnVisibilityChangedListener = null;
	// mBuilt = true;
	// return guideFragment;
	// }

	/**
	 * 创建Guide，非Fragment版本
	 * 
	 * @return Guide
	 */
	public Guide createGuide() {
		Guide guide = new Guide();
		Component[] components = new Component[mComponents.size()];
		guide.setComponents(mComponents.toArray(components));
		guide.setConfiguration(mConfiguration);
		guide.setCallback(mOnVisibilityChangedListener);
		mComponents = null;
		mConfiguration = null;
		mOnVisibilityChangedListener = null;
		mBuilt = true;
		return guide;
	}

	/**
	 * 蒙板可见发生变化时的事件监听
	 * 
	 * @author Simon
	 */
	public static interface OnVisibilityChangedListener {
		void onShown();

		void onDismiss();
	}
}
