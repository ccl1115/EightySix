/**
 * <h1>导航蒙板系统<b>*需要兼容包*</b></h1>
 *
 * <p>本系统能够快速的为一个Activity里的任何一个View控件创建一个蒙板式的导航页。这个导航页实际上是一个Fragment。</p>
 *
 *
 * <h3>工作原理</h3>
 *
 * <p>首先它需要一个目标View的id。我们通过findViewById来得到这个View，计算它在屏幕上的区域targetRect。通过这个区域，开始绘制一个覆盖整个Activity的
 * 蒙板，可以定义蒙板的颜色和透明度。然而目标View的区域不会被绘制从而实现高亮的效果。</p>
 *
 * <p>接下来是在相对于这个targetRect的区域绘制一些图片或者文字。为了方便适配各种屏幕，所有的图片文字都是相对于targetRect来定义的。目前支持12个锚点，并且
 * 还可以设定额外的x，y偏移量。我们把这样一张图片或者文字抽象成一个Component接口，实际上它是一个View对象的代理。</p>
 *
 * <p>另外，我们可以不对整个Activity覆盖蒙板，而是对某一个View覆盖蒙板。</p>
 *
 * <h3>用例</h3>
 *
 * <pre><code>
 * // we are in an activity context:
 * Guide guide = new GuideBuilder(activity)
 *     .setAlpha(200).setTargetView(R.id.some_button)
 *     .addComponent(new Component() {
 *       public View getView() {
 *         return new TextView(activity);
 *       }
 *
 *       public int getAnchor() {
 *         return ANCHOR_LEFT;
 *       }
 *
 *       public int getFitPosition() {
 *         return FIT_CENTER;
 *       }
 *
 *       public int getXOffset() {
 *         return 9;
 *       }
 *
 *       public int getYOffset() {
 *         return 0;
 *       }
 *     })
 *     .createGuide();
 * guide.show(activity.getSupportFragmentManager());
 * </code></pre>
 *
 * @see com.utree.eightysix.widget.guide.GuideBuilder
 */
package com.utree.eightysix.widget.guide;

