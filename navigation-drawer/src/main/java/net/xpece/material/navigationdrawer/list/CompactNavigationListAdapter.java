package net.xpece.material.navigationdrawer.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.xpece.material.navigationdrawer.R;
import net.xpece.material.navigationdrawer.descriptors.GraphicNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
class CompactNavigationListAdapter extends BaseAdapter {
  private static final String TAG = CompactNavigationListAdapter.class.getSimpleName();

  private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
  private SparseArray<GraphicNavigationItemDescriptor> mItems = new SparseArray<>();
  private LongSparseArray<Integer> mPositions = null;

  private int mSelectedPosition = -1;
//  private long mPendingSelectedId = -1;

  public CompactNavigationListAdapter(List<NavigationSectionDescriptor> sections) {
    setSections(sections);
  }

  private void setSections(List<NavigationSectionDescriptor> sections) {
    notifyDataSetInvalidated();
    mSections = sections;
    calculateViewTypesAndItems();
    notifyDataSetChanged();
  }

  public void setActivatedItem(int position) {
//    mPendingSelectedId = -1;
    if (mSelectedPosition != position) {
      mSelectedPosition = position;
      notifyDataSetChanged();
    }
  }

  @Override
  public void notifyDataSetInvalidated() {
    mPositions = null;

    super.notifyDataSetInvalidated();
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public boolean areAllItemsEnabled() {
    return false;
  }

  @Override
  public boolean isEnabled(int position) {
    return position != mSelectedPosition;
  }

  @Override
  public int getCount() {
    return getRealItemCount();
  }

  private int getRealItemCount() {
    int count = 0;
    for (NavigationSectionDescriptor section : mSections) {
      count += section.size();
    }
    return count;
  }

  @Override
  public GraphicNavigationItemDescriptor getItem(int position) {
    return mItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    GraphicNavigationItemDescriptor item = getItem(position);
    if (item != null) {
      return item.getId();
    }
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
//    FrameLayout view;
    ImageView imageView;

    Context context = parent.getContext();
    GraphicNavigationItemDescriptor item = getItem(position);

    if (convertView == null) {
//      view = new FrameLayout(context);

      imageView = new ImageView(context);
      int minSize = context.getResources().getDimensionPixelSize(R.dimen.mnd_list_item_height_normal);
      imageView.setMinimumHeight(minSize);
      imageView.setMinimumWidth(minSize);
      imageView.setScaleType(ImageView.ScaleType.CENTER);

//      view.addView(imageView,
//          new FrameLayout.LayoutParams(
//              ViewGroup.LayoutParams.WRAP_CONTENT,
//              ViewGroup.LayoutParams.WRAP_CONTENT,
//              Gravity.CENTER));
    } else {
//      view = (FrameLayout) convertView;
//      imageView = (ImageView) view.getChildAt(0);
      imageView = (ImageView) convertView;
    }

    Drawable iconDrawable = item.getIcon(context);
    boolean tintIcon = item.isIconColorAlwaysPassiveOff();
    int passiveColor = item.getPassiveColor(context);
    int activeColor = item.getActiveColor(context);
    String textString = item.getText(context);
    boolean selected = position == mSelectedPosition;

    if (iconDrawable != null) {
      if (tintIcon && selected) {
        imageView.setImageDrawable(Utils.tintDrawable(iconDrawable, activeColor));
      } else {
        imageView.setImageDrawable(Utils.tintDrawable(iconDrawable, passiveColor));
      }
    } else {
      imageView.setImageDrawable(null);
    }
    imageView.setContentDescription(textString);

    if (selected) {
      Utils.setBackground(imageView, Utils.getActivatedDrawable(context));
    } else {
      imageView.setBackgroundColor(0);
//      Utils.setBackground(imageView, Utils.getSelectorDrawable(context));
    }

//    int unit = context.getResources().getDimensionPixelOffset(R.dimen.mnd_unit);
//    view.setPadding(unit, position == 0 ? unit : 0, unit, position == getCount() - 1 ? unit : 0);
//    return view;

    return imageView;
  }

  private void calculateViewTypesAndItems() {
    mItems.clear();
    if (mSections == null) {
      mPositions = new LongSparseArray<>(0);
      return;
    }
    LongSparseArray<Integer> positions = new LongSparseArray<>();

    int position = 0;
    for (int i = 0; i < mSections.size(); i++) {
      NavigationSectionDescriptor section = mSections.get(i);
//      mItems.put(position, section);
//      position++;

      for (int j = 0; j < section.size(); j++) {
        NavigationItemDescriptor item = section.get(j);
        if (!(item instanceof GraphicNavigationItemDescriptor)) continue;
        mItems.put(position, (GraphicNavigationItemDescriptor) item);
        positions.put(item.getId(), position);
        position++;
      }

//      position++;
    }

    mPositions = positions;
  }

  public int getPositionById(long id) {
    return mPositions.get(id, -1);
  }
}
