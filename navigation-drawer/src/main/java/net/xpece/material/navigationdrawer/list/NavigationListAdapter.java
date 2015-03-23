package net.xpece.material.navigationdrawer.list;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.xpece.material.navigationdrawer.R;
import net.xpece.material.navigationdrawer.descriptors.CompositeNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;
import net.xpece.material.navigationdrawer.internal.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
class NavigationListAdapter extends BaseAdapter {

    private static final int TYPE_HEADING = 0; // 48dp tall heading
    private static final int TYPE_PADDING = 1; // padding 8dp
    private static final int TYPE_SEPARATOR = 2; // padding 8dp including 1dp separator at bottom
    private static final int TYPE_ITEM_START = 3;

    private SparseIntArray mItemViewTypeSet = new SparseIntArray();

    private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
    private SparseIntArray mViewTypes = new SparseIntArray();
    private SparseArray<Object> mItems = new SparseArray<>();
    private LongSparseArray<Integer> mPositions = null;

    private int mSelectedPosition = -1;
//  private long mPendingSelectedId = -1;

    // each section consists of the following
    // section heading OR padding
    // items...
    // separator

    public NavigationListAdapter(List<NavigationSectionDescriptor> sections) {
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
        return getItemViewType(position) >= TYPE_ITEM_START && position != mSelectedPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypes.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_ITEM_START + mItemViewTypeSet.size();
    }

    @Override
    public int getCount() {
        return getRealItemCount() + 2 * mSections.size();
    }

    private int getRealItemCount() {
        int count = 0;
        for (NavigationSectionDescriptor section : mSections) {
            count += section.size();
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
        if (item instanceof CompositeNavigationItemDescriptor) {
            return ((CompositeNavigationItemDescriptor) item).getId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
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

            case TYPE_SEPARATOR: {
                if (convertView == null) {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mnd_separator, parent, false);
                } else {
                    view = convertView;
                }

                Context context = parent.getContext();
                int lastPosition = getCount() - 1;
                View divider = ViewHolder.get(view, R.id.divider);
                divider.setBackgroundColor(Utils.createDividerColor(context));
                divider.setVisibility(position < lastPosition ? View.VISIBLE : View.INVISIBLE);
                break;
            }

//      case TYPE_ITEM: {
            default: {
                Context context = parent.getContext();
                CompositeNavigationItemDescriptor item = (CompositeNavigationItemDescriptor) getItem(position);

                if (convertView == null) {
                    view = item.createView(context, parent);
                } else {
                    view = convertView;
                }

                item.loadInto(view, position == mSelectedPosition);

                break;
            }

        }
        return view;
    }

    private void calculateViewTypesAndItems() {
        mItemViewTypeSet.clear();
        mViewTypes.clear();
        mItems.clear();
        if (mSections == null) {
            mPositions = new LongSparseArray<>(0);
            return;
        }
        LongSparseArray<Integer> positions = new LongSparseArray<>();

        int position = 0;
        for (int i = 0; i < mSections.size(); i++) {
            NavigationSectionDescriptor section = mSections.get(i);
            boolean hasHeading = section.hasHeading();

            mViewTypes.put(position, hasHeading ? TYPE_HEADING : TYPE_PADDING);
            mItems.put(position, section);
            position++;

            for (int j = 0; j < section.size(); j++) {
                CompositeNavigationItemDescriptor item = section.get(j);

                int viewType;
                {
                    int key = item.getClass().hashCode();
                    if (mItemViewTypeSet.indexOfKey(key) >= 0) {
                        viewType = mItemViewTypeSet.get(key);
                    } else {
                        viewType = TYPE_ITEM_START + mItemViewTypeSet.size();
                        mItemViewTypeSet.put(key, viewType);
                    }
                }

                mViewTypes.put(position, viewType);
                mItems.put(position, item);
                positions.put(item.getId(), position);
                position++;
            }

            mViewTypes.put(position, TYPE_SEPARATOR);
            position++;
        }

        mPositions = positions;
    }

    public int getPositionById(long id) {
        return mPositions.get(id, -1);
    }
}
