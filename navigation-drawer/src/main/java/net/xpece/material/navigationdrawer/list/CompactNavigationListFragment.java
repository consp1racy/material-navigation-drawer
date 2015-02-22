package net.xpece.material.navigationdrawer.list;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.descriptors.CompositeNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;

import java.util.List;

/**
 * Created by Eugen on 11. 1. 2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CompactNavigationListFragment extends Fragment implements CompactNavigationListFragmentImpl {

  private final CompactNavigationListFragmentDelegate mDelegate = new CompactNavigationListFragmentDelegate() {
    @Override
    public Activity getActivity() {
      return CompactNavigationListFragment.this.getActivity();
    }

    @Override
    public View getView() {
      return CompactNavigationListFragment.this.getView();
    }
  };

  public CompactNavigationListFragment() {
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
    super.onDetach();
    mDelegate.onDetach();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mDelegate.onSaveInstanceState(outState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = mDelegate.onCreateView(inflater, container, savedInstanceState);
    mDelegate.onViewCreated(view, savedInstanceState);
    return view;
  }
}
