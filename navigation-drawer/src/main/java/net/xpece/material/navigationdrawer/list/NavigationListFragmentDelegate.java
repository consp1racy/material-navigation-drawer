package net.xpece.material.navigationdrawer.list;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import net.xpece.material.navigationdrawer.R;
import net.xpece.material.navigationdrawer.descriptors.CompositeNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
abstract class NavigationListFragmentDelegate implements
    AdapterView.OnItemClickListener, NavigationListFragmentImpl {
  public static final String TAG = NavigationListFragmentDelegate.class.getSimpleName();

  private static final NavigationListFragmentCallbacks DUMMY_CALLBACKS = new NavigationListFragmentCallbacks() {
    @Override
    public void onNavigationItemSelected(View view, int position, long id, NavigationItemDescriptor item) {
      //
    }
  };
  private NavigationListFragmentCallbacks mCallbacks = DUMMY_CALLBACKS;

  private View mView;
  private ListView mListView;
  private ViewGroup mPinnedContainer;
  private View mPinnedDivider;

  private int mTheFix = 0;
  private boolean mShouldPinnedSectionHaveBackground = false;
  private Drawable mBackground = null;
  private Drawable mPinnedSectionBackground = null;

  private final ViewTreeObserver.OnGlobalLayoutListener mPinnedContainerOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
    @Override
    public void onGlobalLayout() {
      // Problem: in portrait the list has extra space at the bottom (5dp, 8px)
      // Solution: calculate the extra space and subtract it from padding
      int fix = 0;
      final int paddingBottom = mListView.getPaddingBottom();
      final int lastVisible = mListView.getLastVisiblePosition();
      final int lastPosition = mListView.getAdapter().getCount() - 1; // mAdapter.getCount() - 1;
//      timber("listVisible=%s, lastPosition=%s", lastVisible, lastPosition);
      if (lastVisible == lastPosition) {
        final int listHeight = mListView.getMeasuredHeight() - paddingBottom - mListView.getPaddingTop();
        final int lastBottom = mListView.getChildAt(mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition()).getBottom();
//        timber("listHeight=%s, lastBottom=%s", listHeight, lastBottom);
        // if last item ends before the list ends there's extra space
        if (lastBottom < listHeight) {
          if (paddingBottom == 0) mTheFix = Math.max(0, listHeight - lastBottom);
          fix = mTheFix;
//          timber("extraSpace=" + fix);
        }
      }
      // modify padding only after pinned section has been measured and it changed
      // padding = pinned section height - listview extra space - 1dp divider alignment
      final int pinnedHeight = mPinnedContainer.getMeasuredHeight();
//      timber("pinnedHeight=%s", pinnedHeight);
      final int targetPadding = pinnedHeight - fix - Utils.dpToPixelOffset(getActivity(), 1);
//      timber("targetPadding=%s, paddingBottom=%s", targetPadding, paddingBottom);
      if (paddingBottom != targetPadding) {
        mListView.setPadding(0, 0, 0, targetPadding);
      }

      // if pinned section is at the very bottom elevate it
      if (getView() == null) return;
      final int parentHeight = getView().getHeight();
      final int pinnedBottom = mPinnedContainer.getBottom();
      if (pinnedBottom >= parentHeight) {
        // there is not enough room, the section will be pinned
        int colorBackground = Utils.getColor(getView().getContext(), android.R.attr.colorBackground, 0);
        if (Build.VERSION.SDK_INT < 21 || (colorBackground & 0xffffff) < 0xffffff / 2) {
          // on API lower than 21 and on dark theme show the line instead of shadow
          ViewCompat.setElevation(mPinnedContainer, 0);
          mPinnedDivider.setVisibility(View.VISIBLE);
          mShouldPinnedSectionHaveBackground = true; // the views would be seen about to scroll in
        } else {
          // on light theme on API 21 show shadow instead of line
          ViewCompat.setElevation(mPinnedContainer, getActivity().getResources().getDimension(R.dimen.mnd_unit));
          mPinnedDivider.setVisibility(View.GONE);
          mShouldPinnedSectionHaveBackground = true;
        }
      } else {
        // there is enough room, the section will not be pinned
        ViewCompat.setElevation(mPinnedContainer, 0);
        mPinnedDivider.setVisibility(View.VISIBLE);
        mShouldPinnedSectionHaveBackground = false;
      }
      updatePinnedSectionBackground();

      if (paddingBottom == targetPadding) {
        // my work here is done
        Utils.removeOnGlobalLayoutListener(mPinnedContainer, mPinnedContainerOnGlobalLayoutListener);
      }
    }
  };

  private NavigationListAdapter mAdapter;
  private int mLastSelected = -1;

  private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
  private List<CompositeNavigationItemDescriptor> mPinnedSection = null;
  private View mHeader = null;

  public NavigationListFragmentDelegate() {
  }

  public abstract Activity getActivity();

  public abstract View getView();

  @Override
  public void onAttach(Activity activity) {
    mCallbacks = (NavigationListFragmentCallbacks) activity;
  }

  @Override
  public void onDetach() {
    mCallbacks = DUMMY_CALLBACKS;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt("mLastSelected", mLastSelected);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.mnd_list, container, false);
    mListView = (ListView) mView.findViewById(R.id.mnd_list);
    mPinnedContainer = (ViewGroup) mView.findViewById(R.id.mnd_section_pinned);
    mPinnedDivider = mView.findViewById(R.id.mnd_divider_pinned);
    return mView;
  }

  @Override
  public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    Context context = view.getContext();

    mPinnedDivider.setBackgroundColor(Utils.createDividerColor(context));

    mListView.setOnItemClickListener(this);
    mListView.setSelector(Utils.getSelectorDrawable(context));

    if (savedInstanceState != null) {
      mLastSelected = savedInstanceState.getInt("mLastSelected");
    }
  }

  @Override
  public void setItems(List<? extends CompositeNavigationItemDescriptor> items) {
    NavigationSectionDescriptor section = new NavigationSectionDescriptor().addItems(items);
    List<NavigationSectionDescriptor> sections = new ArrayList<>(1);
    sections.add(section);
    setSections(sections);
  }

  /**
   * Set all sections that would be shown in the navigation list except optional pinned section.
   *
   * @param sections
   */
  @Override
  public void setSections(List<NavigationSectionDescriptor> sections) {
    mSections = sections;
    updateSections();
  }

  private void updateSections() {
    if (getView() == null) return;

    mAdapter = new NavigationListAdapter(mSections);
    mAdapter.setActivatedItem(mLastSelected - getHeaderViewsCount());
    mListView.setAdapter(mAdapter);

    mPinnedContainer.getViewTreeObserver().addOnGlobalLayoutListener(mPinnedContainerOnGlobalLayoutListener);
  }

  /**
   * Use this method to set a section that would be pinned at the bottom of the screen when there's
   * not enough room for the whole list to show. Typically this section would contain
   * {@code Settings} and {@code Help & feedback} menu items.
   *
   * @param section
   */
  @Override
  public void setPinnedSection(NavigationSectionDescriptor section) {
    if (section == null || !section.equals(mPinnedSection)) {
      mPinnedSection = section;
      updatePinnedSection();
    }
  }

  private void updatePinnedSection() {
    if (getView() == null) return;

    Drawable selector = Utils.getSelectorDrawable(getView().getContext());

    final int offset = 2; // plus 1 for the divider view plus 1 for padding
    int targetCount = mPinnedSection == null ? 0 : mPinnedSection.size();
//    while (mPinnedContainer.getChildCount() > targetCount + offset) {
//      View view = mPinnedContainer.getChildAt(offset);
//      view.setOnClickListener(null);
//      mPinnedContainer.removeView(view);
//    }
    // TODO temporarily removing all, theres no knowing precise AbsNavItemDesc subtype
    while (mPinnedContainer.getChildCount() > offset) {
      View view = mPinnedContainer.getChildAt(offset);
      view.setOnClickListener(null);
      mPinnedContainer.removeView(view);
    }

    int currentCount = mPinnedContainer.getChildCount() - offset;
    for (int i = 0; i < targetCount; i++) {
      final CompositeNavigationItemDescriptor item = mPinnedSection.get(i);
      final View view;
      if (i < currentCount) {
        view = mPinnedContainer.getChildAt(i + offset);
        item.loadInto(view, false);
      } else {
        Context context = getActivity();
        view = item.createView(context, mPinnedContainer);
        item.loadInto(view, false);
        mPinnedContainer.addView(view);
      }
      Utils.setBackground(view, selector.getConstantState().newDrawable());

      final int relativePosition = i;
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//          mCallbacks.onNavigationItemSelected(v, mAdapter.getCount() + relativePosition, item.getId());
          onItemClick(v, mAdapter.getCount() + relativePosition, item.getId(), item);
        }
      });
    }
    if (targetCount > 0) {
      mPinnedContainer.setVisibility(View.VISIBLE);
    } else {
      mPinnedContainer.setVisibility(View.GONE);
    }

    mPinnedContainer.getViewTreeObserver().addOnGlobalLayoutListener(mPinnedContainerOnGlobalLayoutListener);
  }

  /**
   * Use this method to set a header view. This header is selectable by default so you can use it as
   * a no-op close drawer button. Alternatively you would set up an {@link android.view.View.OnClickListener}
   * beforehand. <em>The header view is not managed by this class, it's completely in your hands.</em>
   *
   * @param view
   */
  @Override
  public void setHeaderView(View view, boolean clickable) {
    if (view == mHeader) return;
    if (mHeader != null) {
      mListView.removeHeaderView(mHeader);
    }
    if (view != null) {
      if (mListView != null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
          if (mListView.getAdapter() != null) mListView.setAdapter(null);
          mListView.addHeaderView(view, null, clickable);
          if (mAdapter != null) mListView.setAdapter(mAdapter);
        } else {
          mListView.addHeaderView(view, null, clickable);
        }
      }
    }
    mHeader = view;
  }

  /**
   * Call this method when the source data set has changed, e.g. when you changed badges.
   */
  @Override
  public void notifyDataSetChanged() {
    if (mAdapter != null) {
      mAdapter.notifyDataSetChanged();
    }
  }

  /**
   * Use this to set a color as the navigation list background.
   * Remember not to use translucent or transparent colors (pinned section has to have an opaque background).
   *
   * @param color
   */
  @Override
  public void setBackgroundColor(int color) {
    if (getView() != null) {
//      mView.setBackgroundColor(color);
//      mPinnedContainer.setBackgroundColor(color);

      mBackground = new ColorDrawable(color);
      updateBackground();
    }
  }

  /**
   * Use this to set a drawable as the navigation list background.
   * Remember not to use vertical gradients (pinned section has to have an opaque background).
   *
   * @param drawable
   */
  @Override
  public void setBackground(Drawable drawable) {
    if (getView() != null) {
//      Utils.setBackground(mView, drawable);
//      Utils.setBackground(mPinnedContainer, drawable);

      mBackground = drawable;
      updateBackground();
    }
  }

  /**
   * Use this to resolve a drawable or color resource ID as a drawable and set it as the navigation list background.
   * Remember not to use vertical gradients (pinned section has to have an opaque background).
   *
   * @param resource
   */
  @Override
  public void setBackgroundResource(@DrawableRes @ColorRes int resource) {
    if (getView() != null) {
//      mView.setBackgroundResource(resource);
//      mPinnedContainer.setBackgroundResource(resource);

      mBackground = getView().getResources().getDrawable(resource);
      updateBackground();
    }
  }

  /**
   * Use this to resolve an attribute as a drawable and set it as the navigation list background.
   * Remember not to use vertical gradients (pinned section has to have an opaque background).
   *
   * @param attr
   */
  @Override
  public void setBackgroundAttr(@AttrRes int attr) {
    if (getView() != null) {
      Drawable d = Utils.getDrawable(mView.getContext(), attr);
      setBackground(d);
    }
  }

  /**
   * Use this method for marking selected item from outside. Typically you call this in
   * {@link android.app.Activity#onPostCreate(android.os.Bundle)} after you determine which
   * section was selected previously or by default.
   *
   * @param id
   */
  @Override
  public void setSelectedItem(long id) {
    if (mAdapter != null) {

      int position = mAdapter.getPositionById(id);
      if (position >= 0) {
        if (trySelectPosition(position)) {
          scrollToPosition(position);
        }
      }
    } else {
      throw new IllegalStateException("No adapter yet!");
    }
  }

  private void scrollToPosition(int position) {
    int last = mListView.getLastVisiblePosition();
    int first = mListView.getFirstVisiblePosition();
    if (last < position) {
      mListView.setSelection(position - (last - first) - 1);
    } else if (first > position) {
      mListView.setSelection(position - 1);
    }
  }

  private boolean trySelectPosition(final int itemPosition) {
    final int listPosition = itemPosition + getHeaderViewsCount();
//    if (listPosition == mLastSelected) return;
    if (itemPosition < 0) {
      // this should never happen
//      selectPosition(-1, mLastSelected);
//      mAdapter.setActivatedItem(-1);
//      mLastSelected = -1;
//      return;
      throw new IllegalArgumentException("Position to select is less than 0.");
    }
    CompositeNavigationItemDescriptor item = (CompositeNavigationItemDescriptor) mAdapter.getItem(itemPosition);
    if (item != null && item.isSticky()) {
//      Utils.timber(TAG, "item=" + item + ", itemPosition=" + itemPosition + ", listPosition=" + listPosition);
      mAdapter.setActivatedItem(itemPosition);
      selectPosition(listPosition, mLastSelected);
      mLastSelected = listPosition;
      return true;
    } else {
      selectPosition(mLastSelected, listPosition);
      return false;
    }
  }

  private void selectPosition(int select, int deselect) {
    if (deselect >= 0) mListView.setItemChecked(deselect, false);
    if (select >= 0) mListView.setItemChecked(mLastSelected, true);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    CompositeNavigationItemDescriptor item = (CompositeNavigationItemDescriptor) parent.getItemAtPosition(position);
    onItemClick(view, position, id, item);
  }

  private void onItemClick(View view, int position, long id, CompositeNavigationItemDescriptor item) {
    // header views and items from pinned section are not sticky, don't even try
    if (position < getHeaderViewsCount()) {
//        || position > mListView.getHeaderViewsCount() + mAdapter.getCount()) {
      selectPosition(mLastSelected, position);
    } else {
      final int itemPosition = position - getHeaderViewsCount();
      trySelectPosition(itemPosition);
    }

    if (item == null || !item.onClick(view)) {
      mCallbacks.onNavigationItemSelected(view, position, id, item);
    }
  }

  private int getHeaderViewsCount() {return mListView.getHeaderViewsCount();}

  private void updateBackground() {
    Utils.setBackground(mView, mBackground);
    mPinnedSectionBackground = mBackground.getConstantState().newDrawable();
    updatePinnedSectionBackground();
  }

  private void updatePinnedSectionBackground() {
    if (mShouldPinnedSectionHaveBackground) {
      Utils.setBackground(mPinnedContainer, mPinnedSectionBackground);
    } else {
      mPinnedContainer.setBackgroundResource(0);
    }
  }
}
