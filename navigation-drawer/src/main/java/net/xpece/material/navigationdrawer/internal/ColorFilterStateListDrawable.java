package net.xpece.material.navigationdrawer.internal;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseArray;

/**
 * @author CC-BY-SA Daniele Segato http://stackoverflow.com/a/19111613/2444099
 * // TODO consider replacing by something similar http://stackoverflow.com/a/23222355/2444099
 */
public class ColorFilterStateListDrawable extends StateListDrawable {
  private SparseArray<ColorFilter> mFilters = new SparseArray<>();
  private int mCount = 0;
  private int mSelected = -1;

  public ColorFilterStateListDrawable() {
    super();
  }

  @Override
  public void addState(int[] stateSet, Drawable drawable) {
    super.addState(stateSet, drawable);
    mCount++;
  }

  public void addState(int[] stateSet, Drawable drawable, int color) {
    int index = mCount;
    addState(stateSet, drawable);
    mFilters.put(index, new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
  }

  @Override
  public boolean selectDrawable(int idx) {
    // reset color filter of previous drawable
    if (mSelected != idx) {
      setColorFilter(getColorFilter(idx));
    }
    // do the switching
    boolean result = super.selectDrawable(idx);
    // setup new color filter
    if (getCurrent() != null) {
      mSelected = result ? idx : mSelected;
      if (!result) {
        setColorFilter(getColorFilter(mSelected));
      }
    } else {
      mSelected = -1;
      setColorFilter(null);
    }
    return result;
  }

  private ColorFilter getColorFilter(int index) {
    return mFilters != null ? mFilters.get(index) : null;
  }
}
