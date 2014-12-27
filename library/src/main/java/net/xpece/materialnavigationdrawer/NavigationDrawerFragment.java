package net.xpece.materialnavigationdrawer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
public class NavigationDrawerFragment extends Fragment implements
    AdapterView.OnItemClickListener {
  public static final String TAG = NavigationDrawerFragment.class.getSimpleName();

  private static final Callbacks DUMMY_CALLBACKS = new Callbacks() {
    @Override
    public void onNavigationItemSelected(View view, int position, long id) {
      //
    }
  };
  private Callbacks mCallbacks = DUMMY_CALLBACKS;

  private ListView mListView;
  private ViewGroup mPinnedContainer;
  private View mPinnedDivider;

  private final ViewTreeObserver.OnGlobalLayoutListener mPinnedContainerOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
    @Override
    public void onGlobalLayout() {
      // Problem: in portrait the list has extra space at the bottom (5dp, 8px)
      // Solution: calculate the extra space and subtract it from padding
      int fix = 0;
      if (mListView.getLastVisiblePosition() == mAdapter.getCount() - 1) {
        int listHeight = mListView.getMeasuredHeight();
        int lastBottom = mListView.getChildAt(mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition()).getBottom();
        // if last item ends before the list ends there's extra space
        if (lastBottom < listHeight) {
          fix = listHeight - lastBottom;
        }
      }
      // modify padding only after pinned section has been measured and it changed
      // padding = pinned section height - listview extra space - 1dp divider alignment
      int pinnedHeight = mPinnedContainer.getMeasuredHeight() - fix - Utils.dpToPixelOffset(getActivity(), 1);
      if (pinnedHeight > 0 && mListView.getPaddingBottom() != pinnedHeight) {
        mListView.setPadding(0, 0, 0, pinnedHeight);
      }

      // if pinned section is at the very bottom elevate it
      int parentHeight = getView().getHeight();
      int pinnedBottom = mPinnedContainer.getBottom();
      if (pinnedBottom >= parentHeight) {
        ViewCompat.setElevation(mPinnedContainer, getActivity().getResources().getDimension(R.dimen.mnd_unit));
        if (Build.VERSION.SDK_INT >= 21) {
          mPinnedDivider.setVisibility(View.INVISIBLE);
        } else {
          mPinnedDivider.setVisibility(View.VISIBLE);
        }
      } else {
        ViewCompat.setElevation(mPinnedContainer, 0);
        mPinnedDivider.setVisibility(View.VISIBLE);
      }
    }
  };

  private NavigationDrawerListAdapter mAdapter;
  private int mLastSelected = -1;

  private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
  private List<NavigationItemDescriptor> mPinnedSection = null;
  private View mHeader = null;

  public NavigationDrawerFragment() {
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    mCallbacks = (Callbacks) activity;
  }

  @Override
  public void onDetach() {
    mCallbacks = DUMMY_CALLBACKS;

    super.onDetach();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
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
    mPinnedContainer.getViewTreeObserver().addOnGlobalLayoutListener(mPinnedContainerOnGlobalLayoutListener);

    mPinnedDivider.setBackgroundColor(Utils.createDividerColor(getActivity()));

    mAdapter = new NavigationDrawerListAdapter();
    mAdapter.setActivatedItem(mLastSelected);
    updatePinnedSection();
    updateSections();

    if (mHeader != null) mListView.addHeaderView(mHeader);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(this);
    mListView.setSelection(mLastSelected);
  }

  @Override
  public void onDestroyView() {
    Utils.removeOnGlobalLayoutListener(mPinnedContainer, mPinnedContainerOnGlobalLayoutListener);
    super.onDestroyView();
  }

  public void setItems(List<NavigationItemDescriptor> items) {
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
  public void setSections(List<NavigationSectionDescriptor> sections) {
    mSections = sections;
    if (mAdapter != null) {
      updateSections();
    }
  }

  private void updateSections() {
    mAdapter.setSections(mSections);
  }

  /**
   * Use this method to set a section that would be pinned at the bottom of the screen when there's
   * not enough room for the whole list to show. Typically this section would contain
   * {@code Settings} and {@code Help & feedback} menu items.
   *
   * @param section
   */
  public void setPinnedSection(NavigationSectionDescriptor section) {
    if (section == null || !section.equals(mPinnedSection)) {
      mPinnedSection = section;
      updatePinnedSection();
    }
  }

  private void updatePinnedSection() {
    final int offset = 1; // plus 1 for the divider view
    int targetCount = mPinnedSection == null ? 0 : mPinnedSection.size();
    while (mPinnedContainer.getChildCount() > targetCount + offset) {
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
        NavigationDrawerListAdapter.loadNavigationItem(view, item, false);
      } else {
        view = NavigationDrawerListAdapter.createNavigationItem(getActivity(), mPinnedContainer, item);
        Utils.setBackground(view, Utils.getDrawable(getActivity(), android.R.attr.selectableItemBackground));
        mPinnedContainer.addView(view);
      }
      final int relativePosition = i;
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//          mCallbacks.onNavigationItemSelected(v, mAdapter.getCount() + relativePosition, item.getId());
          onItemClick(v, mAdapter.getCount() + relativePosition, item.getId());
        }
      });
    }
    if (targetCount > 0) {
      mPinnedContainer.setVisibility(View.VISIBLE);
    } else {
      mPinnedContainer.setVisibility(View.GONE);
    }
  }

  /**
   * Use this method to add a header view. This header is selectable by default so you can use it as
   * a no-op close drawer button. Alternatively you would set up an {@link android.view.View.OnClickListener}
   * beforehand. <em>The header view is not managed by this class, it's completely in your hands.</em>
   *
   * @param view
   */
  public void addHeaderView(View view) {
    mHeader = view;
    if (mListView != null) {
      if (mListView.getAdapter() != null) mListView.setAdapter(null);
      mListView.addHeaderView(mHeader);
      if (mAdapter != null) mListView.setAdapter(mAdapter);
    }
  }

  /**
   * Call this method when the source data set has changed, e.g. when you changed badges.
   */
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
  public void setSelectedItem(long id) {
    if (mAdapter != null) {
      try {
        int position = mAdapter.getPositionById(id);
        trySelectPosition(position);
//        mListView.setSelection(mLastSelected);
      } catch (Exception ex) {
        //
      }
    } else {
      throw new IllegalStateException("No adapter yet!");
    }
  }

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
    onItemClick(view, position, id);
//    parent.setSelection(position);
  }

  private void onItemClick(View view, int position, long id) {
    // header views and items from pinned section are not sticky, don't even try
    if (position >= 0 && position < mListView.getHeaderViewsCount()) {
//        || position > mListView.getHeaderViewsCount() + mAdapter.getCount()) {
      selectPreviousPosition(position);
    } else {
      final int itemPosition = position - mListView.getHeaderViewsCount();
      trySelectPosition(itemPosition);
    }
    mCallbacks.onNavigationItemSelected(view, position, id);
  }

  /**
   * Let your {@link android.app.Activity} implement this interface so it knows what item
   * has been selected.
   */
  public static interface Callbacks {
    public void onNavigationItemSelected(View view, int position, long id);
  }
}
