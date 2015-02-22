package net.xpece.material.navigationdrawer.list;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.xpece.material.navigationdrawer.BuildConfig;
import net.xpece.material.navigationdrawer.R;
import net.xpece.material.navigationdrawer.descriptors.CompositeNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.GraphicNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pechanecjr on 14. 12. 2014.
 */
abstract class CompactNavigationListFragmentDelegate implements
    AdapterView.OnItemClickListener, CompactNavigationListFragmentImpl {
  public static final String TAG = CompactNavigationListFragmentDelegate.class.getSimpleName();

  private static final NavigationListFragmentCallbacks DUMMY_CALLBACKS = new NavigationListFragmentCallbacks() {
    @Override
    public void onNavigationItemSelected(View view, int position, long id, NavigationItemDescriptor item) {
      //
    }
  };
  private NavigationListFragmentCallbacks mCallbacks = DUMMY_CALLBACKS;

  private ListView mListView;

  private CompactNavigationListAdapter mAdapter;
  private int mLastSelected = -1;

  private List<NavigationSectionDescriptor> mSections = new ArrayList<>(0);
  private View mHeader = null;

  public CompactNavigationListFragmentDelegate() {
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
    View view = inflater.inflate(R.layout.mnd_list_compact, container, false);
    mListView = (ListView) view.findViewById(R.id.mnd_list_compact);
    return view;
  }

  @Override
  public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    mListView.setOnItemClickListener(this);
    mListView.setSelector(Utils.getSelectorDrawable(view.getContext()));

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

    mAdapter = new CompactNavigationListAdapter(mSections);
    mAdapter.setActivatedItem(mLastSelected);
    mListView.setAdapter(mAdapter);
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
      getView().setBackgroundColor(color);
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

      int position = mAdapter.getPositionById(id);
      trySelectPosition(position);

    } else {
      throw new IllegalStateException("No adapter yet!");
    }
  }

  private void trySelectPosition(final int itemPosition) {
    final int listPosition = itemPosition + mListView.getHeaderViewsCount();
//    if (listPosition == mLastSelected) return;
    if (itemPosition < 0) {
      mListView.setItemChecked(listPosition, false);
      mAdapter.setActivatedItem(-1);
      mLastSelected = -1;
      return;
    }
    CompositeNavigationItemDescriptor item = (CompositeNavigationItemDescriptor) mAdapter.getItem(itemPosition);
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
    GraphicNavigationItemDescriptor item = (GraphicNavigationItemDescriptor) parent.getItemAtPosition(position);
    onItemClick(view, position, id, item);
  }

  private void onItemClick(View view, int position, long id, GraphicNavigationItemDescriptor item) {
    // header views and items from pinned section are not sticky, don't even try
    if (position >= 0 && position < mListView.getHeaderViewsCount()) {
//        || position > mListView.getHeaderViewsCount() + mAdapter.getCount()) {
      selectPreviousPosition(position);
    } else {
      final int itemPosition = position - mListView.getHeaderViewsCount();
      trySelectPosition(itemPosition);
    }

    mCallbacks.onNavigationItemSelected(view, position, id, item);
  }

  private static void timber(String s) {
    if (BuildConfig.DEBUG) Log.d(TAG, s);
  }
}
