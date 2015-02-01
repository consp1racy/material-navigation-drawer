package net.xpece.material.navigationdrawer.descriptors;

import android.content.Context;
import android.os.Build;
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

  private int mActive, mPassive;

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
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      if (selected) {
        view.setBackgroundColor(mActive);
      } else {
        view.setBackgroundColor(mPassive);
      }
    }
//    else {
//      Utils.setBackground(view, Utils.createActivatedDrawable(mPassive, mActive));
//    }
  }

  @Override
  public final View createView(Context context, ViewGroup parent) {
    mActive = Utils.createActivatedColor(context);
    mPassive = 0;

    View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      Utils.setBackground(view, Utils.createActivatedDrawable(mPassive, mActive));
    }
    loadInto(view, false);
    return view;
  }

//  protected static <T extends View> T askViewHolder(View view, int id) {
//    return ViewHolder.get(view, id);
//  }

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
