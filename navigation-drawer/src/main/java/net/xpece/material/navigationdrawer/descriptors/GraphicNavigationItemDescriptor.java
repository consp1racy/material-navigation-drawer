package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Eugen on 26. 1. 2015.
 */
public interface GraphicNavigationItemDescriptor extends NavigationItemDescriptor {
  Drawable getIcon(Context context);

  boolean isIconColorAlwaysPassiveOff();

  String getText(Context context);

  int getActiveColor(Context context);

  int getPassiveColor(Context context);
}
