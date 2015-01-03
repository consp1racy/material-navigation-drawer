package net.xpece.materialnavigationdrawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
public class NavigationItemDescriptor {
  /** Item ID. */
  private final long id;
  /** Whether item stays active after selecting. */
  private boolean sticky = false;
  /** Icon resource ID. Gets colored appropriately. */
  @DrawableRes
  private int iconId = 0;
  private Drawable icon = null;
  /** Whether the icon should be tinted. */
  private boolean tintIcon = true;
  /** Item label. */
  private String text = null;
  @StringRes
  private int textId = 0;
  /** Badge label. */
  private String badge = null;
  @StringRes
  private int badgeId = 0;

  /**
   * Whether the passive colors have been modified.
   * If not use primary text color for icon in case of dark theme.
   */
  private boolean customPassiveColor = false;

  /** Color of item icon and text when selected. */
  private int activeColor = 0xff000000;
  @ColorRes
  private int activeColorId = R.color.mnd_text_primary_light;
  @AttrRes
  int activeColorAttr = android.R.attr.textColorPrimary;
  /** Color of item icon when not selected. Should be 54% black or 100% white. */
  private int passiveColor = 0xde000000;
  @ColorRes
  private int passiveColorId = R.color.mnd_text_secondary_light;
  @AttrRes
  int passiveColorAttr = android.R.attr.textColorSecondary;
  /** Background color of badge. Text color is calculated automatically. */
  int badgeColor = 0xff000000;
  @ColorRes
  int badgeColorId = android.R.color.black;
  @AttrRes
  int badgeColorAttr = android.R.attr.colorForeground;

  public NavigationItemDescriptor() {
    this.id = -1;
  }

  public NavigationItemDescriptor(long id) {
    this.id = id;
  }

  public long getId() {
    return this.id;
  }

//  public NavigationItemDescriptor icon(Drawable icon) {
//    this.icon = icon;
//    this.iconId = 0;
//    return this;
//  }

  public NavigationItemDescriptor iconResource(@DrawableRes int icon) {
    this.iconId = icon;
    this.icon = null;
    return this;
  }

  public Drawable getIcon(Context context) {
    if (iconId != 0) {
      return context.getResources().getDrawable(iconId).mutate();
    } else {
      return icon;
    }
  }

  public NavigationItemDescriptor iconColorAlwaysPassiveOff() {
    this.tintIcon = true;
    return this;
  }

  public NavigationItemDescriptor iconColorAlwaysPassiveOn() {
    this.tintIcon = false;
    return this;
  }

  public boolean getTintIcon() {
    return this.tintIcon;
  }

  public NavigationItemDescriptor text(String text) {
    this.text = text;
    this.textId = 0;
    return this;
  }

  public NavigationItemDescriptor text(@StringRes int text) {
    this.textId = text;
    this.text = null;
    return this;
  }

  public String getText(Context context) {
    if (textId != 0) {
      return context.getString(textId);
    } else {
      return text;
    }
  }

  public NavigationItemDescriptor badge(String badge) {
    this.badge = badge;
    this.badgeId = 0;
    return this;
  }

  public NavigationItemDescriptor badge(@StringRes int badge) {
    this.badgeId = badge;
    this.badge = null;
    return this;
  }

  public String getBadge(Context context) {
    if (badgeId != 0) {
      return context.getString(badgeId);
    } else {
      return badge;
    }
  }

  public NavigationItemDescriptor activeColor(int color) {
    this.activeColor = color;
    this.activeColorId = 0;
    this.activeColorAttr = 0;
    return this;
  }

  public NavigationItemDescriptor activeColorResource(@ColorRes int color) {
    this.activeColorId = color;
    this.activeColor = 0;
    this.activeColorAttr = 0;
    return this;
  }

  public NavigationItemDescriptor activeColorAttribute(@AttrRes int color) {
    this.activeColorAttr = color;
    this.activeColorId = 0;
    this.activeColor = 0;
    return this;
  }

  public int getActiveColor(Context context) {
    if (activeColorAttr != 0) {
      return Utils.getColor(context, activeColorAttr, 0xff000000);
    } else if (activeColorId != 0) {
      return context.getResources().getColor(activeColorId);
    } else {
      return activeColor;
    }
  }

  public NavigationItemDescriptor passiveColor(int color) {
    this.customPassiveColor = true;
    this.passiveColor = color;
    this.passiveColorId = 0;
    this.passiveColorAttr = 0;
    return this;
  }

  public NavigationItemDescriptor passiveColorResource(@ColorRes int color) {
    this.customPassiveColor = true;
    this.passiveColorId = color;
    this.passiveColorAttr = 0;
    this.passiveColor = 0;
    return this;
  }

  public NavigationItemDescriptor passiveColorAttribute(@AttrRes int color) {
    this.customPassiveColor = true;
    this.passiveColorAttr = color;
    this.passiveColorId = 0;
    this.passiveColor = 0;
    return this;
  }

  public int getPassiveColor(Context context) {
    if (!customPassiveColor) {
      // icons in dark themes should be 100% white
      final int secondary = Utils.getColor(context, android.R.attr.textColorSecondary, 0xde000000);
      if ((secondary & 0xffffff) == 0xffffff) {
        return Utils.getColor(context, android.R.attr.textColorPrimary, 0xffffffff);
      } else {
        return secondary;
      }
    } else if (passiveColorAttr != 0) {
      return Utils.getColor(context, passiveColorAttr, 0xde000000);
    } else if (passiveColorId != 0) {
      return context.getResources().getColor(passiveColorId);
    } else {
      return passiveColor;
    }
  }

  public NavigationItemDescriptor badgeColor(int color) {
    this.badgeColor = color;
    this.badgeColorId = 0;
    this.badgeColorAttr = 0;
    return this;
  }

  public NavigationItemDescriptor badgeColorResource(@ColorRes int color) {
    this.badgeColorId = color;
    this.badgeColorAttr = 0;
    this.badgeColor = 0;
    return this;
  }

  public NavigationItemDescriptor badgeColorAttribute(@AttrRes int color) {
    this.badgeColorAttr = color;
    this.badgeColorId = 0;
    this.badgeColor = 0;
    return this;
  }

  public int getBadgeColor(Context context) {
    if (badgeColorAttr != 0) {
      return Utils.getColor(context, badgeColorAttr, 0xff000000);
    } else if (badgeColorId != 0) {
      return context.getResources().getColor(badgeColorId);
    } else {
      return badgeColor;
    }
  }

  public NavigationItemDescriptor sticky() {
    sticky = true;
    return this;
  }

  public NavigationItemDescriptor notSticky() {
    sticky = false;
    return this;
  }

  public boolean isSticky() {
    return sticky;
  }

  @Override
  public String toString() {
    return "NavigationItemDescriptor{" +
        "id=" + id +
        ", sticky=" + sticky +
        ", iconId=" + iconId +
        (text == null ? "" : ", text='" + text + '\'') +
        (textId == 0 ? "" : ", textId=" + textId) +
        (badge == null ? "" : ", badge='" + badge + '\'') +
        (badgeId == 0 ? "" : ", badgeId=" + badgeId) +
        (activeColor == 0 ? "" : ", activeColor=" + activeColor) +
        (activeColorId == 0 ? "" : ", activeColorId=" + activeColorId) +
        (activeColorAttr == 0 ? "" : ", activeColorAttr=" + activeColorAttr) +
        (passiveColor == 0 ? "" : ", passiveColor=" + passiveColor) +
        (passiveColorId == 0 ? "" : ", passiveColorId=" + passiveColorId) +
        (passiveColorAttr == 0 ? "" : ", passiveColorAttr=" + passiveColorAttr) +
        (badgeColor == 0 ? "" : ", badgeColor=" + badgeColor) +
        (badgeColorId == 0 ? "" : ", badgeColorId=" + badgeColorId) +
        (badgeColorAttr == 0 ? "" : ", badgeColorAttr=" + badgeColorAttr) +
        '}';
  }
}
