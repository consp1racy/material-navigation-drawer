package net.xpece.material.navigationdrawer;

import net.xpece.material.navigationdrawer.list.NavigationListFragmentCallbacks;
import net.xpece.material.navigationdrawer.list.SupportNavigationListFragment;

/**
 * Compatibility class.
 * Use {@link net.xpece.material.navigationdrawer.list.SupportNavigationListFragment} from now on.
 * This class will be removed in next release.
 * <p/>
 * Created by Eugen on 12. 1. 2015.
 */
@Deprecated
public class NavigationDrawerFragment extends SupportNavigationListFragment {

  @Deprecated
  public interface Callbacks extends NavigationListFragmentCallbacks {}
}
