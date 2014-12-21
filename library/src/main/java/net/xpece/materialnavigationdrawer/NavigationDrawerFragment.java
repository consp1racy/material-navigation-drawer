package net.xpece.materialnavigationdrawer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onNavigationItemSelected(AdapterView<?> parent, View view, int position, long id) {
      //
    }
  };

  private Callbacks mCallbacks = DUMMY_CALLBACKS;
  private ListView mListView;
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
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mListView = (ListView) inflater.inflate(R.layout.mnd_list, container, false);
    return mListView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    mListView.getLayoutParams().width = Math.min(
        getResources().getDimensionPixelSize(R.dimen.mnd_drawer_max_width),
        getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelOffset(R.dimen.mnd_drawer_margin)
    );

    mAdapter = new NavigationDrawerListAdapter();
    updateSections();

    if (mHeader != null) mListView.addHeaderView(mHeader);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(this);
  }

  public void setItems(List<NavigationItemDescriptor> items) {
    NavigationSectionDescriptor section = new NavigationSectionDescriptor().addItems(items);
    List<NavigationSectionDescriptor> sections = new ArrayList<>(1);
    sections.add(section);
    setSections(sections);
  }

  public void setSections(List<NavigationSectionDescriptor> sections) {
    mSections = sections;
    if (mAdapter != null) {
      updateSections();
    }
  }

  public void setPinnedSection(NavigationSectionDescriptor section) {
    mPinnedSection = section;
    if (mAdapter != null) {
      updateSections();
    }
  }

  private void updateSections() {
    mAdapter.setSections(mSections);
    //TODO handle pinned section layout
  }

  public void addHeaderView(View view) {
    mHeader = view;
    if (mListView != null) {
      if (mListView.getAdapter() != null) mListView.setAdapter(null);
      mListView.addHeaderView(mHeader);
      if (mAdapter != null) mListView.setAdapter(mAdapter);
    }
  }

  public void notifyDataSetChanged() {
    if (mAdapter != null) {
      mAdapter.notifyDataSetChanged();
    }
  }

  public void setSelectedItem(long id) {
    Log.w(TAG, "Item selected from outside.");
    if (mAdapter != null) {
      int position = mAdapter.getPositionById(id);
      trySelectPosition(position);
    }
  }

  private void trySelectPosition(final int itemPosition) {
    final int listPosition = itemPosition + mListView.getHeaderViewsCount();
    if (listPosition == mLastSelected) return;
    if (itemPosition < 0) {
      mListView.setItemChecked(listPosition, false);
      mLastSelected = -1;
      return;
    }
    NavigationItemDescriptor item = (NavigationItemDescriptor) mAdapter.getItem(itemPosition);
    if (item.isSticky()) {
      mListView.setItemChecked(mLastSelected, false);
      mListView.setItemChecked(listPosition, true);
      mLastSelected = listPosition;
    } else {
      selectPreviousPosition(listPosition);
    }
  }

  private void selectPreviousPosition(int deselect) {
    mListView.setItemChecked(deselect, false);
    mListView.setItemChecked(mLastSelected, true);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Log.w(TAG, "Item clicked.");
    if (position >= 0 && position < mListView.getHeaderViewsCount()) {
      selectPreviousPosition(position);
    } else {
      final int itemPosition = position - mListView.getHeaderViewsCount();
      trySelectPosition(itemPosition);
    }
    mCallbacks.onNavigationItemSelected(parent, view, position, id);
  }

  public static interface Callbacks {
    public void onNavigationItemSelected(AdapterView<?> parent, View view, int position, long id);
  }
}
