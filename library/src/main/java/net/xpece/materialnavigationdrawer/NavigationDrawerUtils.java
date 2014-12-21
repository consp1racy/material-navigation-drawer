package net.xpece.materialnavigationdrawer;

import android.support.v4.widget.DrawerLayout;

import java.lang.reflect.Field;

/**
 * Created by pechanecjr on 20. 12. 2014.
 */
public class NavigationDrawerUtils {
  private NavigationDrawerUtils() {
  }

  /**
   * The specs tell that
   * <ol>
   * <li>Navigation Drawer should be at most 5*56dp wide on phones and 5*64dp wide on tablets.</li>
   * <li>Navigation Drawer should have right margin of 56dp on phones and 64dp on tablets.</li>
   * </ol>
   * yet the minimum margin is hardcoded to be 64dp instead of 56dp. This fixes it.
   */
  public static void fixMinDrawerMargin(DrawerLayout drawerLayout) {
    try {
      Field f = DrawerLayout.class.getDeclaredField("mMinDrawerMargin");
      f.setAccessible(true);
      f.set(drawerLayout, 0);

      drawerLayout.requestLayout();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
