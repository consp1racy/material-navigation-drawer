package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.internal.Utils;
import net.xpece.material.navigationdrawer.internal.ViewHolder;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public abstract class AbsNavigationItemDescriptor implements NavigationItemDescriptor {
  /** Item ID. */
  protected final long id;

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
    Context context = view.getContext();

    Utils.setBackground(view, Utils.createActivatedDrawable(0, Utils.createActivatedColor(context)));
  }

  @Override
  public View createView(Context context, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
    loadInto(view, false);
    return view;
  }

  protected static <T extends View> T askViewHolder(View view, int id) {
    return ViewHolder.get(view, id);
  }

  @Override
  public boolean onClick(View view) {
    return false;
  }
}
