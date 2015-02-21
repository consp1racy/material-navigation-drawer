package net.xpece.material.navigationdrawer.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import net.xpece.material.navigationdrawer.BuildConfig;
import net.xpece.material.navigationdrawer.R;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
public class Utils {

  public static final int APPCOMPAT_ATTR_SELECTABLE_ITEM_BACKGROUND;
  public static final int APPCOMPAT_ATTR_ACTIVATED_BACKGROUND_INDICATOR;

  static {
    final String clsName = "android.support.v7.appcompat.R$attr";
    int selectableItemBackground;
    int activatedBackgroundIndicator;
    try {
      selectableItemBackground = Class.forName(clsName).getField("selectableItemBackground").getInt(null);
    } catch (Exception e) {
      selectableItemBackground = 0;
    }
    try {
      activatedBackgroundIndicator = Class.forName(clsName).getField("activatedBackgroundIndicator").getInt(null);
    } catch (Exception e) {
      activatedBackgroundIndicator = 0;
    }
    APPCOMPAT_ATTR_SELECTABLE_ITEM_BACKGROUND = selectableItemBackground;
    APPCOMPAT_ATTR_ACTIVATED_BACKGROUND_INDICATOR = activatedBackgroundIndicator;
  }

  private Utils() {
  }

//  @TargetApi(21)
//  public static void setElevation(View view, float elevation) {
//    if (Build.VERSION.SDK_INT >= 21) view.setElevation(elevation);
//  }

  public static Drawable createRoundRect(Context context, int color, int cornerDp) {
    GradientDrawable gd = new GradientDrawable();
    gd.setColor(color);
    gd.setCornerRadius(Utils.dpToPixel(context, cornerDp));
    return gd;
  }

  public static int dpToPixelSize(Context context, int dp) {
    return Math.round(0.5f + dpToPixel(context, dp));
  }

  public static int dpToPixelOffset(Context context, int dp) {
    return (int) dpToPixel(context, dp);
  }

  public static float dpToPixel(Context context, int dp) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
  }

  /**
   * Sauce: http://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
   * More sauce: http://stackoverflow.com/a/596243/2444099
   *
   * @param background
   * @return
   */
  public static int computeTextColor(Context context, int background) {
    int r = (background & 0xff0000) >> 16;
    int g = (background & 0xff00) >> 8;
    int b = background & 0xff;
    double a = computeTextColorAlgorithm2(r, g, b);
//    Log.w("#computeTextColor", "r=" + r + ", g=" + g + ", b=" + b + ", contrast=" + a);
    if ((int) (a * 100) <= 50) // light blue was so close that i just had to do this ^^
      return context.getResources().getColor(R.color.mnd_text_primary_dark);
    else
      return context.getResources().getColor(R.color.mnd_text_primary_light);
  }

  private static double computeTextColorAlgorithm0(int r, int g, int b) {
    return (r / 3.0 + g / 3.0 + b / 3.0) / 255;
  }

  private static double computeTextColorAlgorithm1(int r, int g, int b) {
    return (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255;
  }

  private static double computeTextColorAlgorithm2(int r, int g, int b) {
    // Counting the perceptive luminance - human eye favors green color...
    return (0.299 * r + 0.587 * g + 0.114 * b) / 255;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static Drawable createActivatedDrawable(Drawable drawable, int passive, int active) {
    ColorFilterStateListDrawable stateful = new ColorFilterStateListDrawable();
    stateful.addState(new int[]{android.R.attr.state_activated}, drawable, active);
    stateful.addState(StateSet.WILD_CARD, drawable, passive);
    return stateful;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static Drawable createActivatedDrawable(int passive, int active) {
    StateListDrawable stateful = new StateListDrawable();
    stateful.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(active));
    stateful.addState(StateSet.WILD_CARD, new ColorDrawable(passive));
    return stateful;
  }

  public static boolean getBoolean(Context context, @AttrRes int attr, boolean fallback) {
    TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
    try {
      return ta.getBoolean(0, fallback);
    } finally {
      ta.recycle();
    }
  }

  public static int getColor(Context context, @AttrRes int attr, int fallback) {
    TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
    try {
      return ta.getColor(0, fallback);
    } finally {
      ta.recycle();
    }
  }

  public static Drawable getDrawable(Context context, @AttrRes int attr) {
    TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
    try {
      return ta.getDrawable(0);
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

  @TargetApi(16)
  public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
    if (Build.VERSION.SDK_INT >= 16) {
      view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    } else {
      view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
    }
  }

  public static int createDividerColor(Context context) {
    return Utils.getColor(context, android.R.attr.colorForeground, 0) & 0x1effffff;
  }

  public static int createActivatedColor(Context context) {
//    return Utils.getColor(context, android.R.attr.colorForeground, 0) & 0x12ffffff;
    return Utils.getColor(context, android.R.attr.colorForeground, 0) & 0x1effffff;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static Drawable getSelectorDrawable(Context context) {
    Drawable d = null;
    if (APPCOMPAT_ATTR_SELECTABLE_ITEM_BACKGROUND != 0) {
      d = getDrawable(context, APPCOMPAT_ATTR_SELECTABLE_ITEM_BACKGROUND);
    }
    if (d != null) return d;

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return context.getResources().getDrawable(android.R.drawable.list_selector_background);
    } else {
      return getDrawable(context, android.R.attr.selectableItemBackground);
    }
  }

  public static Drawable getActivatedDrawable(Context context) {
    return new ColorDrawable(createActivatedColor(context));
  }

  public static void timber(String tag, String s) {
    if (BuildConfig.DEBUG) Log.d(tag, s);
  }
}
