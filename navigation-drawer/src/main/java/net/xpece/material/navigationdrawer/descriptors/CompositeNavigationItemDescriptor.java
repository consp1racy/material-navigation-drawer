package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eugen on 12. 1. 2015.
 */
public interface CompositeNavigationItemDescriptor extends NavigationItemDescriptor {

  /**
   * Load this object state into specified view.
   * @param view
   * @param selected
   */
  void loadInto(View view, boolean selected);

  /**
   * View factory. Do not attach view to parent yet.
   * <p/><strong>IMPORTANT!</strong>
   * The created view must not reflect this objects state.
   * Do not setup listeners in this method.
   * @param context
   * @param parent
   * @return
   */
  View createView(Context context, ViewGroup parent);

  /**
   * Callback when the item was clicked.
   * @param view
   * @return True if the event was consumed (items with touch sensitive widgets),
   * false if the list view's callback should be invoked.
   */
  boolean onClick(View view);
}
