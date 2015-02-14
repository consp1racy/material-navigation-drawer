package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.internal.Utils;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public abstract class AbsNavigationItemDescriptor implements CompositeNavigationItemDescriptor {
  /** Item ID. */
  protected final long id;

  protected AbsNavigationItemDescriptor(long id) {
    this.id = id;
  }

  private Drawable mActive;

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
      Utils.setBackground(view, mActive);
    } else {
      Utils.setBackground(view, new ColorDrawable(0));
    }
  }

  @Override
  public final View createView(Context context, ViewGroup parent) {
    mActive = Utils.getActivatedDrawable(context);

    View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
    loadInto(view, false);
    return view;
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
