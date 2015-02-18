package net.xpece.material.navigationdrawer.descriptors;

/**
 * Created by Eugen on 26. 1. 2015.
 */
public interface NavigationItemDescriptor {
  /**
   * Get unique item ID. This only makes sense for navigation items. Valid IDs are greater than 0.
   * @return
   */
  long getId();

  boolean isSticky();
}
