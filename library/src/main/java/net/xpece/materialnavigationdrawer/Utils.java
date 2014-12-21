package net.xpece.materialnavigationdrawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.View;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
class Utils {
  private Utils() {
  }

  /**
   * Sauce: http://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
   *
   * @param background
   * @return
   */
  public static int computeTextColor(Context context, int background) {
    int r = (background & 0xff0000) >> 16;
    int g = (background & 0x00ff00) >> 8;
    int b = background & 0x0000ff;

    // Counting the perceptive luminance - human eye favors green color...
    double a = 1 - (0.299 * r + 0.587 * g + 0.114 * b) / 255;

    if (a < 0.5)
      return context.getResources().getColor(R.color.abc_primary_text_material_light);
    else
      return context.getResources().getColor(R.color.abc_primary_text_material_dark);
  }

  public static Drawable createStateListDrawable(Drawable drawable, int passive, int active) {
    ColorFilterStateListDrawable stateful = new ColorFilterStateListDrawable();
    stateful.addState(new int[]{android.R.attr.state_activated}, drawable, active);
    stateful.addState(StateSet.WILD_CARD, drawable, passive);
    return stateful;
  }

  public static Drawable createStateListDrawable(int passive, int active) {
    StateListDrawable stateful = new StateListDrawable();
    stateful.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(active));
    stateful.addState(StateSet.WILD_CARD, new ColorDrawable(passive));
    return stateful;
  }

  public static ColorStateList createColorStateList(int passive, int active) {
    return new ColorStateList(
        new int[][]{new int[]{android.R.attr.state_activated}, StateSet.WILD_CARD},
        new int[]{active, passive});
  }

  public static int getColor(Context context, @AttrRes int attr, int fallback) {
    TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
    try {
      return ta.getColor(0, fallback);
    } finally {
      ta.recycle();
    }
  }

  public static Drawable tintDrawable(Context context, @DrawableRes int drawableId, @ColorRes int colorId) {
    Drawable d = context.getResources().getDrawable(drawableId);
    int c = context.getResources().getColor(colorId);
    return tintDrawable(d, c);
  }

  public static Drawable tintDrawable(Drawable d, int c) {
//    PorterDuffColorFilter cf = COLOR_FILTER_CACHE.get(c);
//    if (cf == null) {
//      cf = new PorterDuffColorFilter(c, PorterDuff.Mode.SRC_IN);
//      COLOR_FILTER_CACHE.put(c, cf);
//    }
    PorterDuffColorFilter cf = new PorterDuffColorFilter(c, PorterDuff.Mode.SRC_IN);
    d.setColorFilter(cf);
    return d;
  }

  @TargetApi(16)
  public static void setBackground(View v, Drawable d) {
    if (Build.VERSION.SDK_INT >= 16) {
      v.setBackground(d);
    } else {
      v.setBackgroundDrawable(d);
    }
  }

  public static int createDividerColor(Context context) {
    return Utils.getColor(context, android.R.attr.colorForeground, 0) & 0x1effffff;
  }

}
