package net.xpece.material.navigationdrawer.list;

import android.app.Activity;
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
import net.xpece.material.navigationdrawer.descriptors.AbsNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import timber.log.Timber;

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

  private ListView mListView;
  private ViewGroup mPinnedContainer;
  private View mPinnedDivider;

  private int mTheFix = 0;

  private final ViewTreeObserver.OnGlobalLayoutListener mPinnedContainerOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
    @Override
    public void onGlobalLayout() {
      // Problem: in portrait the list has extra space at the bottom (5dp, 8px)
      // Solution: calculate the extra space and subtract it from padding
      int fix = 0;
      final int paddingBottom = mListView.getPaddingBottom();
      final int lastVisible = mListView.getLastVisiblePosition();
      final int lastPosition = mListView.getAdapter().getCount() - 1; // mAdapter.getCount() - 1;
      Timber.d("listVisible=%s, lastPosition=%s", lastVisible, lastPosition);
      if (lastVisible == lastPosition) {
        final int listHeight = mListView.getMeasuredHeight() - paddingBottom - mListView.getPaddingTop();
        final int lastBottom = mListView.getChildAt(mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition()).getBottom();
        Timber.d("listHeight=%s, lastBottom=%s", listHeight, lastBottom);
        // if last item ends before the list ends there's extra space
        if (lastBottom < listHeight) {
          if (paddingBottom == 0) mTheFix = Math.max(0, listHeight - lastBottom);
          fix = mTheFix;
          Timber.d("extraSpace=" + fix);
        }
      }
      // modify padding only after pinned section has been measured and it changed
      // padding = pinned section height - listview extra space - 1dp divider alignment
      final int pinnedHeight = mPinnedContainer.getMeasuredHeight();
      Timber.d("pinnedHeight=%s", pinnedHeight);
      final int targetPadding = pinnedHeight - fix - Utils.dpToPixelOffset(getActivity(), 1);
      Timber.d("targetPadding=%s, paddingBottom=%s", targetPadding, paddingBottom);
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
        } else {
          // on light theme on API 21 show shadow instead of line
          ViewCompat.setElevation(mPinnedContainer, getActivity().getResources().getDimension(R.dimen.mnd_unit));
          mPinnedDivider.setVisibility(View.GONE);
        }
      } else {
        // there is enough room, the section will not be pinned
        ViewCompat.setElevation(mPinnedContainer, 0);
        mPinnedDivider.setVisibility(View.VISIBLE);
      }

      if (paddingBottom == targetPadding) {
        // my work here is done
        Utils.removeOnGlobalLayoutListener(mPinnedContainer, mPinnedContainerOnGlobalLayoutListener);
      }
    }
  };

  private NavigationListAdapter mAdapter;
  private int mLastSelected = -1;

  private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
  private List<NavigationItemDescriptor> mPinnedSection = null;
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
    View view = inflater.inflate(R.layout.mnd_list, container, false);
    mListView = (ListView) view.findViewById(R.id.mnd_list);
    mPinnedContainer = (ViewGroup) view.findViewById(R.id.mnd_section_pinned);
    mPinnedDivider = view.findViewById(R.id.mnd_divider_pinned);
    return view;
  }

  @Override
  public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mLastSelected = savedInstanceState.getInt("mLastSelected");
    }

//    NavigationDrawerUtils.setProperNavigationDrawerWidth(view);
//    mPinnedContainer.getViewTreeObserver().addOnGlobalLayoutListener(mPinnedContainerOnGlobalLayoutListener);
    mPinnedDivider.setBackgroundColor(Utils.createDividerColor(getActivity()));

//    if (savedInstanceState != null) {
//      updatePinnedSection();
//      updateSections();
//    }

    mListView.setOnItemClickListener(this);
    mListView.setSelection(mLastSelected);
  }

  @Override
  public void setItems(List<? extends NavigationItemDescriptor> items) {
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

  @DebugLog
  private void updateSections() {
    if (getView() == null) return;

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      mListView.setAdapter(null);
    }
    mAdapter = new NavigationListAdapter(mSections);
    mAdapter.setActivatedItem(mLastSelected);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      if (mHeader != null) mListView.addHeaderView(mHeader);
    }
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

  @DebugLog
  private void updatePinnedSection() {
    if (getView() == null) return;

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
      final NavigationItemDescriptor item = mPinnedSection.get(i);
      final View view;
      if (i < currentCount) {
        view = mPinnedContainer.getChildAt(i + offset);
        item.loadInto(view, false);
      } else {
        view = item.createView(getActivity(), mPinnedContainer);
        Utils.setBackground(view, Utils.getDrawable(getActivity(), android.R.attr.selectableItemBackground));
        mPinnedContainer.addView(view);
      }
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
  public void setHeaderView(View view) {
    if (view == mHeader) return;
    if (mHeader != null) {
      mListView.removeHeaderView(mHeader);
    }
    if (view != null) {
      if (mListView != null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
          if (mListView.getAdapter() != null) mListView.setAdapter(null);
          mListView.addHeaderView(view);
          if (mAdapter != null) mListView.setAdapter(mAdapter);
        } else {
          mListView.addHeaderView(view);
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
      getView().setBackgroundColor(color);
      mPinnedContainer.setBackgroundColor(color);
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
      Utils.setBackground(getView(), drawable);
      Utils.setBackground(mPinnedContainer, drawable);
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
      getView().setBackgroundResource(resource);
      mPinnedContainer.setBackgroundResource(resource);
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
    Drawable d = Utils.getDrawable(getActivity(), attr);
    setBackground(d);
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
      try {
        int position = mAdapter.getPositionById(id);
        trySelectPosition(position);
//        mListView.setSelection(mLastSelected);
      } catch (Exception ex) {
        // id has been stored in adapter and the item will be marked accordingly when loaded
        // TODO the choice will not be preserved on screen orientation change yet
      }
    } else {
      throw new IllegalStateException("No adapter yet!");
    }
  }

  @DebugLog
  private void trySelectPosition(final int itemPosition) {
    final int listPosition = itemPosition + mListView.getHeaderViewsCount();
    if (listPosition == mLastSelected) return;
    if (itemPosition < 0) {
      mListView.setItemChecked(listPosition, false);
      mAdapter.setActivatedItem(-1);
      mLastSelected = -1;
      return;
    }
    NavigationItemDescriptor item = (NavigationItemDescriptor) mAdapter.getItem(itemPosition);
    if (item != null && item.isSticky()) {
      mListView.setItemChecked(mLastSelected, false);
      mListView.setItemChecked(listPosition, true);
      mAdapter.setActivatedItem(itemPosition);
      mLastSelected = listPosition;
    } else {
      selectPreviousPosition(listPosition);
    }
  }

  private void selectPreviousPosition(int deselect) {
    mListView.setItemChecked(deselect, false);
    mListView.setItemChecked(mLastSelected, true);
  }

  /**
   * @param parent
   * @param view
   * @param position
   * @param id
   */
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    AbsNavigationItemDescriptor item = (AbsNavigationItemDescriptor) parent.getItemAtPosition(position);
    onItemClick(view, position, id, item);
  }

  private void onItemClick(View view, int position, long id, NavigationItemDescriptor item) {
    // header views and items from pinned section are not sticky, don't even try
    if (position >= 0 && position < mListView.getHeaderViewsCount()) {
//        || position > mListView.getHeaderViewsCount() + mAdapter.getCount()) {
      selectPreviousPosition(position);
    } else {
      final int itemPosition = position - mListView.getHeaderViewsCount();
      trySelectPosition(itemPosition);
    }

    if (item == null || !item.onClick(view)) {
      mCallbacks.onNavigationItemSelected(view, position, id, item);
    }
  }

}
