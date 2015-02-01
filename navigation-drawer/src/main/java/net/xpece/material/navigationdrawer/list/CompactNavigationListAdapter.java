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
    Object item = getItem(position);
    if (item instanceof GraphicNavigationItemDescriptor) {
      return ((GraphicNavigationItemDescriptor) item).getId();
    }
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ImageView view;

    Context context = parent.getContext();
    GraphicNavigationItemDescriptor item = getItem(position);

    if (convertView == null) {
      view = new ImageView(context);
      int minSize = context.getResources().getDimensionPixelSize(R.dimen.mnd_list_item_height_normal);
      view.setMinimumHeight(minSize);
      view.setMinimumWidth(minSize);
      view.setScaleType(ImageView.ScaleType.CENTER);

      Utils.setBackground(view, Utils.createActivatedDrawable(0, Utils.createActivatedColor(context)));
    } else {
      view = (ImageView) convertView;
    }

    Drawable iconDrawable = item.getIcon(context);
    boolean tintIcon = item.isIconColorAlwaysPassiveOff();
    int passiveColor = item.getPassiveColor(context);
    int activeColor = item.getActiveColor(context);
    String textString = item.getText(context);

    if (iconDrawable != null) {
      if (tintIcon) {
        view.setImageDrawable(Utils.createActivatedDrawable(iconDrawable, passiveColor, activeColor));
      } else {
        view.setImageDrawable(Utils.tintDrawable(iconDrawable, passiveColor));
      }
    } else {
      view.setImageDrawable(null);
//      throw new IllegalStateException(TAG + ": Provided null drawable for id=" +item.getId());
    }
    view.setContentDescription(textString);

    // TODO selected position ...?

    return view;
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
