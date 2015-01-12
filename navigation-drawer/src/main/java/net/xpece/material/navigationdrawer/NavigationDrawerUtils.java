package net.xpece.material.navigationdrawer;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewTreeObserver;

import net.xpece.material.navigationdrawer.internal.Utils;

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

  public static void setProperNavigationDrawerWidth(final View view) {
    final Context context = view.getContext();
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        Utils.removeOnGlobalLayoutListener(view, this);

        int smallestWidthPx = context.getResources().getDisplayMetrics().widthPixels
            < context.getResources().getDisplayMetrics().heightPixels
            ? context.getResources().getDisplayMetrics().widthPixels
            : context.getResources().getDisplayMetrics().heightPixels;
        int drawerMargin = context.getResources().getDimensionPixelOffset(R.dimen.mnd_drawer_margin);

        view.getLayoutParams().width = Math.min(
            context.getResources().getDimensionPixelSize(R.dimen.mnd_drawer_max_width),
//            context.getResources().getDisplayMetrics().widthPixels
            smallestWidthPx - drawerMargin
        );
        view.requestLayout();
      }
    });
  }
}
