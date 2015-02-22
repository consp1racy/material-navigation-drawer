package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.internal.Utils;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public abstract class AbsNavigationItemDescriptor implements CompositeNavigationItemDescriptor {
  /** Item ID. */
  protected long id;

  protected AbsNavigationItemDescriptor(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public boolean isSticky() {
    return false;
  }

  @Override
  public void loadInto(View view, boolean selected) {
    if (selected) {
      Utils.setBackground(view, Utils.getActivatedDrawable(view.getContext()));
    } else {
      view.setBackgroundColor(0);
    }
  }

  @Override
  public final View createView(Context context, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
    onViewCreated(view);
    return view;
  }

  /**
   * The specified layout will be inflated as view.
   * @return
   */
  public abstract int getLayoutId();

  /**
   * Setup views in this method. This concerns mainly themeing.
   * <p/><strong>IMPORTANT!</strong>
   * The created view must not reflect this objects state.
   * Do not setup listeners in this method.
   * @param view
   */
  public void onViewCreated(View view) {
//    loadInto(view, false);
  }

  @Override
  public boolean onClick(View view) {
    return false;
  }

  @Override
  public String toString() {
    return "AbsNavigationItemDescriptor{" +
        "id=" + id +
        '}';
  }
}
