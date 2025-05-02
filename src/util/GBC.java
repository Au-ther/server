package util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * 采用GBC的实例对象对某个采用了GridBagLayout布局管理的容器上的组件进行
 * 位置、大小、间距等特性的限制
 * @author ibm
 *
 */
public class GBC extends GridBagConstraints {

	private static final long serialVersionUID = 1L;

/**
   * 设置组件初始位置(位于哪行哪列)
   * @param x
   * @param y
   */
  public GBC(int x, int y) {
    this.gridx = x;
    this.gridy = y;
  }

  public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
    this.gridx = gridx;
    this.gridy = gridy;
    this.gridwidth = gridwidth;
    this.gridheight = gridheight;
  }

  public GBC setAnchor(int anchor) {
    this.anchor = anchor;
    return this;
  }
  /**
   * 设置组件的显示区域大于组件大小时，组件大小横向纵向变化情况
   * @param fill
   * @return
   */
  public GBC setFill(int fill) {
    this.fill = fill;
    return this;
  }
  
  /**
   * 设置组件的宽度和高度
   * @param weightx
   * @param weighy
   * @return
   */
  public GBC setWeight(double weightx, double weighty) {
    this.weightx = weightx;
    this.weighty = weighty;
    return this;
  }
  /**
   * 设置组件前、后、左、右之间的间距大小
   * @param distance
   * @return
   */
  public GBC setInset(int distance) {
    this.insets = new Insets(distance, distance, distance, distance);
    return this;
  }
  
  /**
   * 设置组件前、后、左、右之间的间距大小
   * @param distance
   * @return
   */
  public GBC setInset(int top, int left, int bottom, int right) {
    this.insets = new Insets(top, left, bottom, right);
    return this;
  }

  public GBC setIpad(int ipadx, int ipady) {
    this.ipadx = ipadx;
    this.ipady = ipady;
    return this;
  }
}
