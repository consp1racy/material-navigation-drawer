package net.xpece.material.navigationdrawer.sample.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sqisland.android.sliding_pane_layout.CrossFadeSlidingPaneLayout;

import net.xpece.material.navigationdrawer.NavigationDrawerUtils;
import net.xpece.material.navigationdrawer.descriptors.BaseNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.descriptors.SimpleNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.internal.Utils;
import net.xpece.material.navigationdrawer.list.NavigationListFragmentCallbacks;
import net.xpece.material.navigationdrawer.list.SupportCompactNavigationListFragment;
import net.xpece.material.navigationdrawer.list.SupportNavigationListFragment;
import net.xpece.material.navigationdrawer.sample.BuildConfig;
import net.xpece.material.navigationdrawer.sample.R;
import net.xpece.material.navigationdrawer.sample.widget.ToggleNavigationItemDescriptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import timber.log.Timber;

public class MainActivity extends ActionBarActivity implements NavigationListFragmentCallbacks {

//  @InjectView(R.id.app_bar)
//  Toolbar mToolbar;

  private static final List<NavigationSectionDescriptor> SECTIONS = new ArrayList<>();
  private static final NavigationSectionDescriptor PRIMARY_SECTION;
  private static final NavigationSectionDescriptor PINNED_SECTION;

  static {
    PRIMARY_SECTION = new NavigationSectionDescriptor()
        .addItem(new SimpleNavigationItemDescriptor(1).text("Goodbye").badge("Hello").sticky()
            .iconResource(R.drawable.ic_star_black_24dp)
            .activeColorResource(R.color.material_red_500)
            .badgeColorResource(R.color.material_red_500))
        .addItem(new SimpleNavigationItemDescriptor(2).text("Yes").badge("No").sticky()
            .iconResource(R.drawable.ic_star_black_24dp)
            .passiveColorResource(R.color.material_amber_500).iconColorAlwaysPassiveOn()
            .badgeColorResource(R.color.material_amber_500))
        .addItem(new SimpleNavigationItemDescriptor(3).text("Stop").badge("Go, go, go").sticky()
//            .iconResource(R.drawable.ic_star_black_24dp)
            .iconResource(android.R.color.transparent)
            .activeColorResource(R.color.material_light_green_500)
            .badgeColorResource(R.color.material_light_green_500))
        .addItem(new SimpleNavigationItemDescriptor(4).text("Why").badge("I don't know").sticky()
            .iconResource(0)
            .activeColorResource(R.color.material_light_blue_500).iconColorAlwaysPassiveOn()
            .badgeColor(0));
    SECTIONS.add(PRIMARY_SECTION);
    NavigationSectionDescriptor section2 = new NavigationSectionDescriptor().heading("Want more?")
        .addItem(new ToggleNavigationItemDescriptor(8).checked(true));
    SECTIONS.add(section2);
    NavigationSectionDescriptor section3 = new NavigationSectionDescriptor()
        .addItem(new BaseNavigationItemDescriptor(6).text("Settings")
            .iconResource(R.drawable.ic_settings_black_24dp))
        .addItem(new BaseNavigationItemDescriptor(7).text("Help & feedback")
            .iconResource(R.drawable.ic_help_black_24dp));
    PINNED_SECTION = section3;

    if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
  }

  @Optional
  @InjectView(R.id.drawer_layout)
  DrawerLayout mDrawerLayout;

  @Optional
  @InjectView(R.id.sliding_layout)
  CrossFadeSlidingPaneLayout mSlidingLayout;

  @Optional
  @InjectView(R.id.navigation_container)
  ViewGroup mNavigationContainer;

  @InjectView(R.id.content)
  View mContent;

  ActionBarDrawerToggle mDrawerToggle;

  SupportNavigationListFragment mNavFragment;
  SupportCompactNavigationListFragment mNavFragmentCompact;

  // retain this
  Long mSelectedItem;

  // so I can show new toast immediately
  Toast mToast = null;

  @SuppressLint("RtlHardcoded")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    // only setup drawer layout if there is a drawer in current layout (on phones)
    if (mDrawerLayout != null) {
      // setup drawer toggle, because i use native Action Bar and nav drawer below it
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, android.R.string.untitled, android.R.string.untitled);
      mDrawerLayout.setDrawerListener(mDrawerToggle);

      NavigationDrawerUtils.fixMinDrawerMargin(mDrawerLayout); // apply navigation margin fix

      // the following are correct the RIGHT drawer drops shadow to LEFT and vice versa
      mDrawerLayout.setDrawerShadow(R.drawable.mnd_shadow_left, Gravity.RIGHT);
      mDrawerLayout.setDrawerShadow(R.drawable.mnd_shadow_right, Gravity.LEFT);
    }

    // only setup sliding layout if there is one in current layout (on tablets)
    if (mSlidingLayout != null) {
      mSlidingLayout.setSliderFadeColor(0);
      mSlidingLayout.setShadowResourceLeft(R.drawable.mnd_shadow_left);
      mSlidingLayout.setShadowResourceRight(R.drawable.mnd_shadow_right);
      NavigationDrawerUtils.setProperNavigationDrawerWidth(mNavigationContainer);

      Drawable d = getResources().getDrawable(R.drawable.ic_menu_white_24dp);
      int c = Utils.getColor(getSupportActionBar().getThemedContext(), R.attr.colorControlNormal, 0);
      d = Utils.tintDrawable(d, c);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(d);
    }

    // on phones there is no compact version, so null check is in place
    mNavFragmentCompact = (SupportCompactNavigationListFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_compact);
    if (mNavFragmentCompact != null) {
      List<NavigationSectionDescriptor> sections = new ArrayList<>();
      sections.add(PRIMARY_SECTION);
      sections.add(PINNED_SECTION);
      mNavFragmentCompact.setHeaderView(View.inflate(this, R.layout.mnd_custom_header_compact, null), false);
      mNavFragmentCompact.setSections(sections);
    }

    // since the fragment is defined in layout, i can call this safely in onCreate
    mNavFragment = (SupportNavigationListFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
    if (mNavFragment != null) {
      NavigationDrawerUtils.setProperNavigationDrawerWidth(mNavFragment.getView());
      // set up the nav fragment
      mNavFragment.setHeaderView(View.inflate(this, R.layout.mnd_custom_header, null), true);
      mNavFragment.setSections(SECTIONS);
      mNavFragment.setPinnedSection(PINNED_SECTION);

      mNavFragment.setBackgroundResource(R.drawable.a7x_aligned);
    }

    if (savedInstanceState == null) {
      mSelectedItem = 1l;
    } else {
      mSelectedItem = savedInstanceState.getLong("mSelectedItem");
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    if (mDrawerToggle != null) mDrawerToggle.syncState();

    if (savedInstanceState == null) {
      if (mNavFragment != null) mNavFragment.setSelectedItem(mSelectedItem);
      if (mNavFragmentCompact != null) mNavFragmentCompact.setSelectedItem(mSelectedItem);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong("mSelectedItem", mSelectedItem);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    // AUTOMATICALLY ONLY ON 4.1+ !!!

    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    } else if (id == android.R.id.home) {
      if (mDrawerToggle != null) return mDrawerToggle.onOptionsItemSelected(item);
      if (mSlidingLayout != null) {
        if (mSlidingLayout.isOpen()) mSlidingLayout.closePane();
        else mSlidingLayout.openPane();
        return true;
      }
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onNavigationItemSelected(View view, int position, long id, NavigationItemDescriptor item) {
    if (mToast != null) mToast.cancel();
    mToast = Toast.makeText(this, "Item #" + id + " on position " + position + " selected!", Toast.LENGTH_SHORT);
    mToast.show();

    if (item != null && item.isSticky()) {
      // if it is sticky, save selected position and mark in all fragments
      mSelectedItem = id;
      if (mNavFragment != null) mNavFragment.setSelectedItem(id);
      if (mNavFragmentCompact != null) mNavFragmentCompact.setSelectedItem(id);
    } else {
//      if (mNavFragment != null) mNavFragment.setSelectedItem(id);
//      if (mNavFragmentCompact != null) mNavFragmentCompact.setSelectedItem(id);
    }

    if (id == 8) return; // custom toggle does not close the drawer

//    if (mDrawerLayout != null) mDrawerLayout.closeDrawers(); // uncomment to close drawer on item selected
  }

}
