package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eugen on 12. 1. 2015.
 */
public interface NavigationItemDescriptor {
  long getId();

  boolean isSticky();

  int getLayoutId();

  void loadInto(View view, boolean selected);

  View createView(Context context, ViewGroup parent);

  boolean onClick(View view);
}
