package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import net.xpece.material.navigationdrawer.R;
import net.xpece.material.navigationdrawer.internal.Utils;
import net.xpece.material.navigationdrawer.internal.ViewHolder;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
public final class SimpleNavigationItemDescriptor extends BaseNavigationItemDescriptor {
  /** Badge label. */
  private String badge = null;
  @StringRes
  private int badgeId = 0;

  /** Background color of badge. Text color is calculated automatically. */
  int badgeColor = 0xff000000;
  @ColorRes
  int badgeColorId = android.R.color.black;
  @AttrRes
  int badgeColorAttr = android.R.attr.colorForeground;

  public SimpleNavigationItemDescriptor(long id) {
    super(id);
  }

  public SimpleNavigationItemDescriptor badge(String badge) {
    this.badge = badge;
    this.badgeId = 0;
    return this;
  }

  public SimpleNavigationItemDescriptor badge(@StringRes int badge) {
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

  public SimpleNavigationItemDescriptor badgeColor(int color) {
    this.badgeColor = color;
    this.badgeColorId = 0;
    this.badgeColorAttr = 0;
    return this;
  }

  public SimpleNavigationItemDescriptor badgeColorResource(@ColorRes int color) {
    this.badgeColorId = color;
    this.badgeColorAttr = 0;
    this.badgeColor = 0;
    return this;
  }

  public SimpleNavigationItemDescriptor badgeColorAttribute(@AttrRes int color) {
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

  @Override
  public SimpleNavigationItemDescriptor sticky() {
    return (SimpleNavigationItemDescriptor) super.sticky();
  }

  @Override
  public SimpleNavigationItemDescriptor notSticky() {
    return (SimpleNavigationItemDescriptor) super.notSticky();
  }

  @Override
  public SimpleNavigationItemDescriptor iconResource(@DrawableRes int icon) {
    return (SimpleNavigationItemDescriptor) super.iconResource(icon);
  }

  @Override
  public SimpleNavigationItemDescriptor iconColorAlwaysPassiveOff() {
    return (SimpleNavigationItemDescriptor) super.iconColorAlwaysPassiveOff();
  }

  @Override
  public SimpleNavigationItemDescriptor iconColorAlwaysPassiveOn() {
    return (SimpleNavigationItemDescriptor) super.iconColorAlwaysPassiveOn();
  }

  @Override
  public SimpleNavigationItemDescriptor text(String text) {
    return (SimpleNavigationItemDescriptor) super.text(text);
  }

  @Override
  public SimpleNavigationItemDescriptor text(@StringRes int text) {
    return (SimpleNavigationItemDescriptor) super.text(text);
  }

  @Override
  public SimpleNavigationItemDescriptor activeColor(int color) {
    return (SimpleNavigationItemDescriptor) super.activeColor(color);
  }

  @Override
  public SimpleNavigationItemDescriptor activeColorResource(@ColorRes int color) {
    return (SimpleNavigationItemDescriptor) super.activeColorResource(color);
  }

  @Override
  public SimpleNavigationItemDescriptor activeColorAttribute(@AttrRes int color) {
    return (SimpleNavigationItemDescriptor) super.activeColorAttribute(color);
  }

  @Override
  public SimpleNavigationItemDescriptor passiveColor(int color) {
    return (SimpleNavigationItemDescriptor) super.passiveColor(color);
  }

  @Override
  public SimpleNavigationItemDescriptor passiveColorResource(@ColorRes int color) {
    return (SimpleNavigationItemDescriptor) super.passiveColorResource(color);
  }

  @Override
  public SimpleNavigationItemDescriptor passiveColorAttribute(@AttrRes int color) {
    return (SimpleNavigationItemDescriptor) super.passiveColorAttribute(color);
  }

  @Override
  public int getLayoutId() {
    return R.layout.mnd_item_simple;
  }

  @Override
  public void loadInto(View view, boolean selected) {
    super.loadInto(view, selected);

    Context context = view.getContext();

    TextView badge = ViewHolder.get(view, R.id.badge);

    int badgeColor = getBadgeColor(context);
    String badgeString = getBadge(context);

    if (badgeString != null) {
      badge.setText(badgeString);
      if (badgeColor == 0) {
        badge.setBackgroundColor(0);
        badge.setTextAppearance(context, R.style.TextAppearance_MaterialNavigationDrawer_Badge_NoBackground);

//        int textColorPrimary = Utils.getColor(context, android.R.attr.textColorPrimary, 0xde000000);
//        int textColorSecondary = Utils.getColor(context, android.R.attr.textColorSecondary, 0x89000000);
//        ColorStateList badgeTextColor = Utils.createActivatedColor(textColorSecondary, textColorPrimary);
//        badge.setTextColor(badgeTextColor);

        int textColor;
        if (selected) {
          textColor = Utils.getColor(context, android.R.attr.textColorPrimary, 0xde000000);
        } else {
          textColor = Utils.getColor(context, android.R.attr.textColorSecondary, 0x89000000);
        }
        badge.setTextColor(textColor);
      } else {
        Utils.setBackground(badge, Utils.createRoundRect(context, badgeColor, 1));
        badge.setTextAppearance(context, R.style.TextAppearance_MaterialNavigationDrawer_Badge);
        badge.setTextColor(Utils.computeTextColor(context, badgeColor));
      }
      badge.setVisibility(View.VISIBLE);
    } else {
      badge.setVisibility(View.GONE);
    }
  }

  @Override
  public String toString() {
    return "SimpleNavigationItemDescriptor{" +
        "id=" + id +
        "sticky=" + sticky +
        '}';
  }
}
