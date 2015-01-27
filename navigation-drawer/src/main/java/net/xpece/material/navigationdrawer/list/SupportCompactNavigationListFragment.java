package net.xpece.material.navigationdrawer.list;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.descriptors.CompositeNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;

import java.util.List;

/**
 * Created by Eugen on 11. 1. 2015.
 */
public class SupportCompactNavigationListFragment extends Fragment
    implements CompactNavigationListFragmentImpl {

  private final CompactNavigationListFragmentDelegate mDelegate = new CompactNavigationListFragmentDelegate() {
    @Override
    public Activity getActivity() {
      return SupportCompactNavigationListFragment.this.getActivity();
    }

    @Override
    public View getView() {
      return SupportCompactNavigationListFragment.this.getView();
    }
  };

  public SupportCompactNavigationListFragment() {
  }

  @Override
  public void setItems(List<? extends CompositeNavigationItemDescriptor> items) {
    mDelegate.setItems(items);
  }

  @Override
  public void setSections(List<NavigationSectionDescriptor> sections) {
    mDelegate.setSections(sections);
  }

  @Override
  public void setHeaderView(View view, boolean clickable) {
    mDelegate.setHeaderView(view, clickable);
  }

  @Override
  public void notifyDataSetChanged() {
    mDelegate.notifyDataSetChanged();
  }

  @Override
  public void setBackgroundColor(int color) {
    mDelegate.setBackgroundColor(color);
  }

  @Override
  public void setBackground(Drawable drawable) {
    mDelegate.setBackground(drawable);
  }

  @Override
  public void setBackgroundResource(@DrawableRes @ColorRes int resource) {
    mDelegate.setBackgroundResource(resource);
  }

  @Override
  public void setBackgroundAttr(@AttrRes int attr) {
    mDelegate.setBackgroundAttr(attr);
  }

  @Override
  public void setSelectedItem(long id) {
    mDelegate.setSelectedItem(id);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mDelegate.onAttach(activity);
  }

  @Override
  public void onDetach() {
    mDelegate.onDetach();
    super.onDetach();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mDelegate.onSaveInstanceState(outState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return mDelegate.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mDelegate.onViewCreated(view, savedInstanceState);
  }
}
