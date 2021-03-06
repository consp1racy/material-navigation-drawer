package net.xpece.material.navigationdrawer.list;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.xpece.material.navigationdrawer.descriptors.CompositeNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;

import java.util.List;

/**
 * Created by Eugen on 11. 1. 2015.
 */
interface CompactNavigationListFragmentImpl {
    void onAttach(Activity activity);

    void onDetach();

    void onSaveInstanceState(Bundle outState);

    View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    void onViewCreated(View view, @Nullable Bundle savedInstanceState);

    void onDestroyView();

    void setItems(List<? extends CompositeNavigationItemDescriptor> items);

    void setSections(List<NavigationSectionDescriptor> sections);

    void setHeaderView(View view, boolean clickable);

    void notifyDataSetChanged();

    void setBackgroundColor(int color);

    void setBackground(Drawable drawable);

    void setBackgroundResource(@DrawableRes @ColorRes int resource);

    void setBackgroundAttr(@AttrRes int attr);

    void setSelectedItem(int id);

    void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState);

    LayoutInflater getLayoutInflater2();

    void setReselectEnabled(boolean reselectEnabled);
}
