package net.xpece.material.navigationdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eugen on 10. 1. 2015.
 */
public abstract class AbsNavigationItemDescriptor {
  /** Item ID. */
  protected final long id;

  protected AbsNavigationItemDescriptor(long id) {
    this.id = id;
  }

  public long getId() {
    return this.id;
  }

  public boolean isSticky() {
    return false;
  }

  public abstract int getLayoutId();

  public void loadInto(View view, boolean selected) {
    Context context = view.getContext();

    Utils.setBackground(view, Utils.createActivatedDrawable(0, Utils.createActivatedColor(context)));
  }

  public View createView(Context context, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
    loadInto(view, false);
    return view;
  }

  protected static <T extends View> T askViewHolder(View view, int id) {
    return ViewHolder.get(view, id);
  }

  protected void onClick(View view) {
    // no-op
  }
}
