package net.xpece.materialnavigationdrawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
class NavigationDrawerListAdapter extends BaseAdapter {

  protected static final int TYPE_ITEM = 0; // 48dp tall item
  protected static final int TYPE_HEADING = 1; // 48dp tall heading
  protected static final int TYPE_PADDING = 2; // padding 8dp
  protected static final int TYPE_SEPARATOR = 3; // padding 8dp including 1dp separator at bottom

  private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
  private SparseIntArray mViewTypes = new SparseIntArray();
  private SparseArray<Object> mItems = new SparseArray<>();
  private LongSparseArray<Integer> mPositions = new LongSparseArray<>();

  // each section consists of the following
  // section heading OR padding
  // items...
  // separator

  public void setSections(List<NavigationSectionDescriptor> sections) {
    notifyDataSetInvalidated();
    mSections = sections;
    calculateViewTypesAndItems();
    notifyDataSetChanged();
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
    return getItemViewType(position) == TYPE_ITEM;
  }

  @Override
  public int getItemViewType(int position) {
    return mViewTypes.get(position);
  }

  @Override
  public int getViewTypeCount() {
    return 4;
  }

  @Override
  public int getCount() {
    int count = 0;
    for (NavigationSectionDescriptor section : mSections) {
      count = count + section.size() + 2;
    }
    return count;
  }

  @Override
  public Object getItem(int position) {
    return mItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    Object item = getItem(position);
    if (item instanceof NavigationItemDescriptor) {
      return ((NavigationItemDescriptor) item).getId();
    }
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = null;
    switch (getItemViewType(position)) {
      case TYPE_PADDING: {
        if (convertView == null) {
          view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnd_padding, parent, false);
        } else {
          view = convertView;
        }
        break;
      }
      case TYPE_HEADING: {
        if (convertView == null) {
          view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnd_heading, parent, false);
        } else {
          view = convertView;
        }

        Context context = parent.getContext();
        NavigationSectionDescriptor section = (NavigationSectionDescriptor) getItem(position);
        TextView text = (TextView) view;
        text.setText(section.getHeading(context));

        break;
      }

      case TYPE_ITEM: {
        if (convertView == null) {
          view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnd_item, parent, false);
        } else {
          view = convertView;
        }

        Context context = parent.getContext();

        ImageView icon = ViewHolder.get(view, R.id.icon);
        TextView text = ViewHolder.get(view, R.id.text);
        TextView badge = ViewHolder.get(view, R.id.badge);

        NavigationItemDescriptor item = (NavigationItemDescriptor) getItem(position);

        Drawable iconDrawable = item.getIcon(context);
        int passiveColor = item.getPassiveColor(context);
        int activeColor = item.getActiveColor(context);
        int badgeColor = item.getBadgeColor(context);
        String textString = item.getText(context);
        String badgeString = item.getBadge(context);
        int textColor = Utils.getColor(context, android.R.attr.textColorPrimary, 0);

        if (iconDrawable != null) {
          icon.setImageDrawable(Utils.createStateListDrawable(iconDrawable, passiveColor, activeColor));
//          icon.setImageDrawable(Utils.tintDrawable(iconDrawable, activeColor));
          icon.setVisibility(View.VISIBLE);
        } else {
          icon.setVisibility(View.GONE);
          icon.setImageDrawable(null);
        }
        text.setText(textString);
        text.setTextColor(Utils.createColorStateList(textColor, activeColor));
        if (badgeString != null) {
          badge.setText(badgeString);
          badge.setBackgroundColor(badgeColor);
          badge.setTextColor(Utils.computeTextColor(context, badgeColor));
          badge.setVisibility(View.VISIBLE);
        } else {
          badge.setVisibility(View.GONE);
        }

        Utils.setBackground(view, Utils.createStateListDrawable(0, Utils.createDividerColor(context)));

        break;
      }

      case TYPE_SEPARATOR: {
        if (convertView == null) {
          view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnd_separator, parent, false);
        } else {
          view = convertView;
        }

        Context context = parent.getContext();
        View divider = ViewHolder.get(view, R.id.divider);
        divider.setBackgroundColor(Utils.createDividerColor(context));
        divider.setVisibility(position == getCount() - 1 ? View.INVISIBLE : View.VISIBLE);
        break;
      }
    }
    return view;
  }

  private void calculateViewTypesAndItems() {
    mViewTypes.clear();
    mItems.clear();
    mPositions.clear();
    if (mSections == null) return;

    int position = 0;
    for (int i = 0; i < mSections.size(); i++) {
      NavigationSectionDescriptor section = mSections.get(i);
      boolean hasHeading = section.hasHeading();

      mViewTypes.put(position, hasHeading ? TYPE_HEADING : TYPE_PADDING);
      mItems.put(position, section);
      position++;

      for (int j = 0; j < section.size(); j++) {
        NavigationItemDescriptor item = section.get(j);

        mViewTypes.put(position, TYPE_ITEM);
        mItems.put(position, item);
        mPositions.put(item.getId(), position);
        position++;
      }

      mViewTypes.put(position, TYPE_SEPARATOR);
      position++;
    }
  }

  public int getPositionById(long id) {
    return mPositions.get(id, -1);
  }
}
