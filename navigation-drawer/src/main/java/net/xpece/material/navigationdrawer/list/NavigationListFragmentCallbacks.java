package net.xpece.material.navigationdrawer.list;

import android.view.View;

import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;

/**
 * Let your {@link android.app.Activity} implement this interface so it knows what item
 * has been selected.
 * <p/>
 * Created by Eugen on 11. 1. 2015.
 */
public interface NavigationListFragmentCallbacks {
  public void onNavigationItemSelected(View view, int position, long id, NavigationItemDescriptor item);
}
